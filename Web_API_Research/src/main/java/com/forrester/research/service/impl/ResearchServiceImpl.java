package com.forrester.research.service.impl;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.contentful.java.cda.CDAArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.ContentfulClient;
import com.forrester.research.clients.contentful.ContentfulGraphQLClient;
import com.forrester.research.clients.contentful.response.ContentfulModelStore;
import com.forrester.research.clients.contentful.response.GraphQLResponse;
import com.forrester.research.clients.contentful.response.models.RecommendedResearch;
import com.forrester.research.clients.contentful.response.models.Research;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.forrester.research.clients.pdfgenerator.PdfGeneratorClient;
import com.forrester.research.clients.permissions.response.ResearchPermission;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.exception.NotAcceptableException;
import com.forrester.research.exception.ServiceException;
import com.forrester.research.service.ResearchService;
import com.forrester.research.usage.service.ResearchUsageService;
import com.forrester.research.utils.*;
import com.forrester.research.view.ResearchOverView;
import com.forrester.research.view.ResearchView;

@Service
@RefreshScope
public class ResearchServiceImpl implements ResearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchServiceImpl.class);
	private static final String CONTENT_ID = "contentId";
	public static final String BRANDED_IMAGE = "brandedImage";
	public static final String DOWNLOADABLE_ASSETS = "downloadableAssets";

	@Autowired
	private ContentfulModelStore contentfulModelStore;
	@Autowired
	private ContentFormatter contentFormatter;
	@Autowired
	private ResearchCacheUtil researchCacheUtil;
	@Autowired
	private ResearchAnalystUtil researchAnalystUtil;
	@Autowired
	private ResearchPriceUtil priceUtil;
	@Autowired
	private UserPermissionUtil permissionsUtil;
	@Autowired
	private PdfGeneratorClient pdfClient;
	@Autowired
	private ContentfulClient contentfulClient;
	@Autowired
	private ContentfulGraphQLClient contentfulGraphQLClient;
	@Autowired
	private ResearchUsageService researchUsageService;
	@Autowired
	private ResearchUtil researchViewUtil;
	@Autowired
	private ObjectMapper oMapper;

	@Autowired
	private AwsGeneratePresignedUrlUtil awsGeneratePresignedUrlUtil;

	@Value("${forr.researchservice.redis.enableCache}")
	private boolean cacheEnabled;

	@Value("${forr.researchservice.default.editorialImage}")
	private String defaultEditorialImage;

	@Autowired
	URLMasker urlMasker;

	@Override
	public void postResearchForBIReportingById(String entryId)
			throws ServiceException, NotAcceptableException, DataNotFoundException {
		Class<?> contentModel = contentfulModelStore.getModel(Constants.RESEARCH);
		Research research = (Research) contentfulClient.getContentDetailsByField(contentModel, CONTENT_ID, entryId);
		research = fetchAdditionalResearchData(research);
		populateRecommendedResearch(entryId, research);
		researchUsageService.sendResearchMetadataToKafka(research,contentFormatter.getFormattedResearch(research));
	}


	@Override
	@LogThis
	public Object getResearchById(String entryId, MultiValueMap<String, String> headers, boolean formattedContent)
			throws ServiceException, DataNotFoundException, AuthorizationException {
		LOGGER.info("Entering getResearchById() for content with id: {}", entryId);
		ResearchView researchView = new ResearchView();
		ResearchPermission permission = permissionsUtil.validateTokenAndReturnPermission(headers, entryId);
		ResearchView response = null;
		boolean isReturnFullContent = permission.isHasAccess() || permission.isFlexAccessRedeemed();
		//retrieve data if available in cache if enabled
		Object cachedResponse;
		if(cacheEnabled) {
			cachedResponse=researchCacheUtil.getDataFromCache(entryId, formattedContent, isReturnFullContent, researchView);
			if(null != cachedResponse) {
				ResearchView view =  (ResearchView)cachedResponse;
				if(null !=view.getResearch()) {
					LOGGER.info("Cached content retrieved with id: {}", entryId);
					if(!formattedContent) {
						Map<Object,Object> researchMap = (LinkedHashMap)(view.getResearch());
						researchUsageService.doReporting(headers, permission, researchMap, false);
						researchViewUtil.populateCourtesyView(permission, researchMap);
						view.setResearch(researchMap);
						return view;
					}
					else {
						return view.getResearch();
					}
				}
			}
		}
		Class<?> contentModel = contentfulModelStore.getModel(Constants.RESEARCH);
		Research research = (Research) contentfulClient.getContentDetailsByField(contentModel, CONTENT_ID, entryId);
		research = fetchAdditionalResearchData(research);
		if (null != research) {
			response = mapResponseView(entryId, formattedContent,
					isReturnFullContent, researchView, research);
			if(!formattedContent) {
				researchViewUtil.populateCourtesyView(permission, (Map<Object,Object>)response.getResearch());
				researchUsageService.doReporting(headers, permission, research, false);
			}
			else {
				return response.getResearch();
			}
		}
		return response;
	}

	private void setStatus(Map<Object,Object> researchMap) {
		try {
			final Map researchMapResponse = contentfulGraphQLClient.getResearchId(researchMap.getOrDefault("id", StringUtils.EMPTY).toString());
			final Object data = ((Map) researchMapResponse.getOrDefault("data", Collections.emptyMap())).get("research");
			researchMap.put("status", data != null ? "published" : "draft");
		} catch (DataNotFoundException | ServiceException e) {
			researchMap.put("status", "published");
		}
	}

	private List<JsonNode> populateBrandedImage(JsonNode researchNode) {
		List<JsonNode> updatedResearchJson = new ArrayList<>();
		if (researchNode.isArray())
			researchNode.forEach(child -> updatedResearchJson.addAll(populateBrandedImage(child)));
		else {
			researchNode.fieldNames().forEachRemaining(key -> {
				if (key.equals("contentType")) {
					JsonNode contentType = oMapper.valueToTree(researchNode.get(key).get("sys"));
					populateBrandedImageBasedOnContainers(contentType, researchNode, updatedResearchJson);
				}
			});
		}
		return updatedResearchJson;
	}

	private List<JsonNode> populateBrandedImageBasedOnContainers(JsonNode contentType, JsonNode researchNode,
			List<JsonNode> updatedResearchJson) {
		contentType.fieldNames().forEachRemaining(fieldName -> {
			if (fieldName.equalsIgnoreCase("id")) {
				if (contentType.get(fieldName).asText().equalsIgnoreCase("researchGraphicContainer")) {
					updatedResearchJson.add(updateDownloadableAssets(researchNode));
				} else if (contentType.get(fieldName).asText().equalsIgnoreCase("layoutAssembly")) {
					updatedResearchJson.add(updateLayoutAssembly(researchNode));
				} else {
					updatedResearchJson.add(researchNode);
				}
			}
		});
		return updatedResearchJson;
	}

	private ObjectNode updateLayoutAssembly(JsonNode researchNode) {
		List<JsonNode> updatedResearchJson = new ArrayList<>();
		researchNode.fieldNames().forEachRemaining(key -> {
			if (key.equals("containers")) {
				JsonNode containers = oMapper.valueToTree(researchNode.get(key));
				populateBrandedImageBasedOnLayoutAssembly(containers, updatedResearchJson);
			}
		});
		ObjectNode objectNode = oMapper.valueToTree(researchNode);
		ArrayNode arrayNode = oMapper.valueToTree(updatedResearchJson);
		objectNode.replace("containers", arrayNode);
		return objectNode;
	}

	private void populateBrandedImageBasedOnLayoutAssembly(JsonNode containers, List<JsonNode> updatedResearchJson) {
		containers.forEach(container -> container.fieldNames().forEachRemaining(child -> {
			if (child.equals("contentType")) {
				JsonNode contentType = oMapper.valueToTree(container.get(child).get("sys"));
				contentType.fieldNames().forEachRemaining(fieldName -> {
					if (fieldName.equalsIgnoreCase("id")) {
						if (contentType.get(fieldName).asText().equalsIgnoreCase("researchGraphicContainer")) {
							updatedResearchJson.add(updateDownloadableAssets(container));
						} else {
							updatedResearchJson.add(container);
						}
					}
				});
			}
		}));
	}

	private JsonNode updateDownloadableAssets(JsonNode researchNode) {
		JsonNode downloadableAssets = researchNode.get(DOWNLOADABLE_ASSETS);
		ObjectNode objectNode = oMapper.valueToTree(researchNode);
		ArrayNode arrayNode = new ArrayNode(null);
		if (null != downloadableAssets) {
			arrayNode = oMapper.valueToTree(downloadableAssets);
		}
		ObjectNode updateBrandedImage = oMapper.valueToTree(researchNode.get(BRANDED_IMAGE).get(0));
		updateBrandedImage.replace("containerId", researchNode.get(CONTENT_ID));

		ArrayNode arrayNodeResult = new ArrayNode(null);
		arrayNodeResult.add(updateBrandedImage);
		arrayNodeResult.addAll(arrayNode);

		objectNode.replace(DOWNLOADABLE_ASSETS, arrayNodeResult);
		objectNode.remove(BRANDED_IMAGE);
		researchNode = oMapper.valueToTree(objectNode);
		return researchNode;
	}

	private Research fetchAdditionalResearchData(Research research){
		if (null != research) {
			researchAnalystUtil.populateLatestAuthorsAndContributorsData(research);
			research = researchViewUtil.handleSlug(research);
			priceUtil.getPriceByType(research);
			populateEditorialImage(research);
			researchViewUtil.createContentUrl(research);
		}
		return research;
	}

	private void populateEditorialImage(Research research) {
		if (null != research.getEditorialImageContent() && research.getEditorialImageContent().mimeType().startsWith("image")) {
			research.setEditorialImage(urlMasker.maskPublicImage(research.getEditorialImageContent().url(),
					research.getEditorialImageContent().mimeType()));
		}
		else {
			research.setEditorialImage(urlMasker.maskEditorialImage(defaultEditorialImage));
		}
	}

	@SuppressWarnings("unchecked")
	private ResearchView mapResponseView(String entryId, boolean formattedContent,
			boolean hasAccess, ResearchView researchView, Research research) {
		if(hasAccess) {
			populateRecommendedResearch(entryId, research);
			String researchString;
			Map<Object, Object> researchMap;
			if (null != research.getBody()) {
				JsonNode researchNode = oMapper.valueToTree(research.getBody());
				Object researchObject = populateBrandedImage(researchNode);
				research.setBody((List<CDAArray>) researchObject);
			}
			try {
				researchString = oMapper.writeValueAsString(research);
				researchString=researchString.replace("\u00A0", " ");
				researchMap = oMapper.readValue(researchString, LinkedHashMap.class);
			} catch (JsonProcessingException e) {
				LOGGER.info("JsonProcessingException inside mapResponseView with full access.", e);
				researchMap = oMapper.convertValue(research, LinkedHashMap.class);
			}

			researchView.setResearch(researchMap);
			setStatus((Map<Object,Object>) researchView.getResearch());

			if(cacheEnabled) {
				researchCacheUtil.populateCache(true,entryId,researchMap);
			}

			if(formattedContent) {
				researchView.setResearch(contentFormatter.getFormattedResearch(research));
				return researchView;
			}
		}
		else {
			if (null != research.getBody()) {
				JsonNode researchNode = oMapper.valueToTree(research.getBody().subList(0, 1));
				Object researchObject = populateBrandedImage(researchNode);
				research.setBody((List<CDAArray>) researchObject);
			}
			Object researchOverView = new ResearchOverView(research);
			Map<Object, Object> researchOverviewMap;
			String researchOVString;
			try {
				researchOVString = oMapper.writeValueAsString(researchOverView);
				researchOVString=researchOVString.replace("\u00A0", " ");
				researchOverviewMap = oMapper.readValue(researchOVString, LinkedHashMap.class);
			} catch (JsonProcessingException e) {
				LOGGER.info("JsonProcessingException inside mapResponseView without full access.", e);
				researchOverviewMap = oMapper.convertValue(researchOverView, LinkedHashMap.class);
			}
			researchView.setResearch(researchOverviewMap);
			setStatus((Map<Object,Object>) researchView.getResearch());
			if(cacheEnabled) {
				researchCacheUtil.populateCache(false, entryId, researchOverviewMap);
			}
			if(formattedContent) {
				researchView.setResearch(contentFormatter.getFormattedResearch(researchOverView));
				return researchView;
			}
			researchView.setResearch(researchOverviewMap);
		}
		return researchView;
	}


	private void populateRecommendedResearch(String entryId, Research research) {
		if (research!=null && research.getRecommendedResearchContent() != null) {
			List<RecommendedResearch> recommendedResearchList = new ArrayList<>();
			LOGGER.info("Populating recommended research for: {}", entryId);
			researchViewUtil.mapToResearchView(research, recommendedResearchList);
			research.setRecommendedResearch(recommendedResearchList);
		}
	}

	/**
	 * This method is used to fetch research Ids from contentful graphql.
	 *
	 * @param containerType String
	 * @param skip long
	 * @param limit long
	 * @param publishDate Date
	 * @return GraphQLResponse
	 * @throws DataNotFoundException Exception
	 * @throws ServiceException Exception
	 */
	@Override
	@LogThis
	public GraphQLResponse getResearchIds(String containerType, long skip, long limit, Date publishDate) throws ServiceException, DataNotFoundException {
		return contentfulGraphQLClient.getResearchIds(containerType, skip, limit, publishDate);
	}

	/**
	 * This method is used to return list of content object for given content ids
	 *
	 * @param ids String[]
	 * @param headers MultiValueMap from String to String
	 * @param formattedContent boolean
	 * @param metaDataOnly boolean
	 * @throws DataNotFoundException Exception
	 * @throws AuthorizationException Exception
	 * @throws ServiceException Exception
	 * @return List<Object>
	 */
	@Override
	@LogThis
	public List<Object> getResearchByIds(String[] ids, MultiValueMap<String, String> headers,
			boolean formattedContent, boolean metaDataOnly) throws DataNotFoundException, AuthorizationException, ServiceException {
		List<ResearchPermission> researchPermissions = permissionsUtil.validateTokenAndReturnPermissions(headers, ids, metaDataOnly);
		Map<String, Boolean> idAndAccess = permissionsUtil.getAccessFromPermissions(researchPermissions);
		Map<String, Object> resultMap = getCachedResponse(formattedContent, researchPermissions);

		String[] uncachedIds = Arrays.stream(ids).filter(id -> !resultMap.containsKey(id)).toArray(String[]::new);
		if (ArrayUtils.isNotEmpty(uncachedIds)){
			Map<String, Object> uncachedResponse = getAndProcessUncachedResponse(formattedContent, idAndAccess, uncachedIds);
			resultMap.putAll(uncachedResponse);
		}
		return Arrays.asList(resultMap.values().toArray());
	}

	/**
	 * From the list of entryIds that were provided, get the ones which has its response cached, and add it to the map that is used to
	 * provide the result
	 *
	 * @param formattedContent boolean
	 * @param researchPermissions List of ResearchPermission
	 * @return Map from String to Object
	 */
	private Map<String, Object> getCachedResponse(boolean formattedContent, List<ResearchPermission> researchPermissions) {
		Map<String, Object> resultMap = new HashMap<>();
		if (cacheEnabled) {
			for (ResearchPermission researchPermission : researchPermissions) {
				ResearchView cachedResponse = (ResearchView)researchCacheUtil
						.getDataFromCache(researchPermission.getId(), formattedContent, researchPermission.isHasAccess(),
								new ResearchView());
				if (null != cachedResponse) {
					LOGGER.info("getCachedResponse: retreived cached data for {}", researchPermission.getId() );
					resultMap.put(researchPermission.getId(), cachedResponse.getResearch());
				}
			}
		}
		return resultMap;
	}

	/**
	 * Takes in ids that were not present in cache, makes a call to contentful to get the details and then processes the additional
	 * fields or information that is required. Returns a map with the entryId/contentId as key and the response as the value.
	 *
	 * @param formattedContent boolean
	 * @param idAndAccess Map from String to Boolean
	 * @param uncachedIds String[]
	 * @return Map from String to Object
	 * @throws ServiceException Exception
	 */
	private Map<String, Object> getAndProcessUncachedResponse(boolean formattedContent, Map<String, Boolean> idAndAccess,
			String[] uncachedIds) throws ServiceException {
		Class<?> contentModel = contentfulModelStore.getModel(Constants.RESEARCH);
		LOGGER.info("Data not found in cache and querying contentful for:{}", uncachedIds );
		List<Object> researchObjects = contentfulClient.getEntryDetailsByEntryIds(contentModel, uncachedIds);

		Map<String, Object> uncachedResponse = new HashMap<>();
		researchObjects.parallelStream().forEach(researchObject -> {
			if (null != researchObject) {
				Research research = (Research) researchObject;
				research = fetchAdditionalResearchData(research);
				uncachedResponse.put(research.getContentId(),
				mapResponseView(research.getContentId(), formattedContent, idAndAccess.getOrDefault(research.getContentId(), true), new ResearchView(), research).getResearch());
			}
		});
		return uncachedResponse;
	}

    @Override
    public String getResearchPdfById(String entryId, MultiValueMap<String, String> headers)
               throws AuthorizationException, DataNotFoundException, ServiceException {
          ResearchPermission permission = permissionsUtil.validateTokenAndReturnPermission(headers, entryId);
          if (permission != null && (permission.isHasAccess() || permission.isFlexAccessRedeemed())
                    && headers.get(HttpHeaders.AUTHORIZATION.toLowerCase()) != null) {
               String idToken = headers.getFirst(HttpHeaders.AUTHORIZATION.toLowerCase());
               ResponseEntity<ByteArrayResource> resource = pdfClient.getResearchPdf(idToken, entryId);
               if (null != resource) {
            	    String preSignedURL = awsGeneratePresignedUrlUtil.uploadAndGetPresignedUrl(resource, entryId);
                    ResearchView researchView = new ResearchView();
                    if (cacheEnabled) {
                          Object cachedResponse = researchCacheUtil.getDataFromCache(entryId, false, permission.isHasAccess(),
                                    researchView);
                          if (null != cachedResponse) {
                               ResearchView view = (ResearchView) cachedResponse;
                               if (null != view.getResearch()) {
                                    LOGGER.info("Cached content retrieved with id: {}", entryId);
                                    Map<Object, Object> researchMap = (LinkedHashMap) (view.getResearch());
                                    researchUsageService.doReporting(headers, permission, researchMap, true);
                                    return preSignedURL;
                               }
                          }
                          LOGGER.info("Data not found in cache and querying contentful for:{}", entryId);
                          doReportingUsingContentfulObj(entryId, headers, permission);
                          return preSignedURL;
                    }
                    doReportingUsingContentfulObj(entryId, headers, permission);
                    return preSignedURL;
               }
               throw new DataNotFoundException("PDF not found for the entryId :"+ entryId);
          }
          throw new AuthorizationException("User does not have access to this content.");
    }

    private void doReportingUsingContentfulObj(String entryId, MultiValueMap<String, String> headers,
               ResearchPermission permission) {
          try {
               Class<?> contentModel = contentfulModelStore.getModel(Constants.RESEARCH);
               Research research = (Research) contentfulClient.getContentDetailsByField(contentModel,
                          CONTENT_ID, entryId);
               research = fetchAdditionalResearchData(research);
               researchUsageService.doReporting(headers, permission, research, true);
          } catch (Exception e) {
               LOGGER.error("Cannot report pdf download. Error: {}", e.getMessage(), e);
          }
    }
}

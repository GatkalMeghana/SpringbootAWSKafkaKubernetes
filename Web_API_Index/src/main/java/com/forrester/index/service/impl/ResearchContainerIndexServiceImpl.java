package com.forrester.index.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forrester.index.clients.cmscontent.ContentExtractor;
import com.forrester.index.clients.cmscontent.response.Contents;
import com.forrester.index.clients.cmscontent.response.MainContent;
import com.forrester.index.clients.cmscontent.response.ReportSection;
import com.forrester.index.clients.content.ContentServiceClient;
import com.forrester.index.clients.contentful.response.GraphQLResponse;
import com.forrester.index.clients.contentful.response.ResearchCollection;
import com.forrester.index.clients.contentful.response.ResearchItem;
import com.forrester.index.clients.contentful.response.ResponseData;
import com.forrester.index.clients.contentful.response.asset.GraphQLAssetCollectionResponse;
import com.forrester.index.clients.contentful.response.researchGraphicContainer.GraphQlRgcCollectionResponse;
import com.forrester.index.clients.graphql.GraphQLClient;
import com.forrester.index.clients.research.ResearchContainerClient;
import com.forrester.index.clients.research.response.Asset;
import com.forrester.index.clients.research.response.ResearchContainerDTO;
import com.forrester.index.clients.taxonomy.TaxonomyClient;
import com.forrester.index.clients.taxonomy.response.TaxonomyResponse;
import com.forrester.index.clients.vectorize.VectorizeClient;
import com.forrester.index.elasticsearch.data.ESResearchContainer;
import com.forrester.index.elasticsearch.data.Searchables;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.ResearchContainerIndexService;
import com.forrester.index.utils.ContentType;
import com.forrester.index.utils.DeIndexQueryBuilder;
import com.forrester.index.utils.IndexAliasesBuilder;
import com.forrester.index.utils.LogThis;
import com.forrester.index.view.BulkUploadView;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.BulkFailureException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResourceUtil;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.forrester.index.utils.Constants.*;
import static com.forrester.index.utils.StringUtils.SANITIZE_JSON;

@Service
public class ResearchContainerIndexServiceImpl implements ResearchContainerIndexService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchContainerIndexServiceImpl.class);
	public static final int CONTENTFUL_QUERY_LIMIT = 50;


	@Autowired
	private ElasticsearchOperations elasticSearchTemplate;

	@Value("${forr.elasticsearch.package.synonym}")
	private String synonymFilePath;

	@Value("${forr.research.index.delay.enabled}")
	private boolean researchIndexDelayEnabled;

	@Value("${forr.research.index.delay.duration}")
	private long researchIndexDelayDuration;

	@Value("${forr.company.interview.expression}")
  	private String companyInterviewedExpression;

	@Value("${forr.research.searchable.include.body:false}")
	private boolean researchSearchableIncludeBodyField;

	@Value("${forr.research.searchable.include.body.json:false}")
	private boolean researchSearchableIncludeBodyJsonField;

	@Value("${forr.contentful.image.search.value}")
	private String imageUrlSearchValue;

	@Value("${forr.contentful.image.replace.value}")
	private String imageUrlReplaceValue;

	@Autowired
	private IndexAliasesBuilder indexAliasesBuilder;

	@Autowired
	private ResearchContainerClient researchContainerClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private VectorizeClient vectorizeClient;

	@Autowired
	private TaxonomyClient taxonomyClient;

    @Autowired
    private ContentServiceClient contentServiceClient;

    @Autowired
	private GraphQLClient graphQLClient;

	private static final String RES_PREFIX = "RES";

	private static final String CONTAINER_TYPE = "researchCollection";

	private static final String ALIAS_NAME = "readAliasFdResearchIndex";

	private static final String SIRIUS_DECISIONS = "SiriusDecisions";

	private static final String RESEARCH_TYPE = "research";

	private static final String TYPE_FORRESTER = "Forrester";

	/**
	 * This method retrieves the data for the received contentId from Research
	 * service and then pushes it into Elasticsearch to be indexed.
	 *
	 * @param researchID
	 * @throws ServiceException      if not possible to retrieve data from
	 *                               Research service
	 * @throws DataNotFoundException if the received entryId does not exists in
	 *                               Research services
	 * @return ESResearchContainer
	 */
	@LogThis
	@Override
		public ESResearchContainer indexById(String researchID) throws ServiceException, DataNotFoundException, JsonProcessingException {
		this.handleDelay();
        researchID = StringUtils.startsWith(researchID.toUpperCase(), RES_PREFIX) ? researchID
                : StringUtils.join(RES_PREFIX,researchID);
		Set<String> linkedReportIds = new HashSet<>();
		ESResearchContainer indexedEsResearchContainer = getParseAndIndexResearch(researchID, linkedReportIds);
		fetchAndprocessLinkedReportIds(linkedReportIds);
		return indexedEsResearchContainer;
	}

	/**
	 * method to process asset info, linked reports & indexing
	 * @param researchID research ids
	 * @param linkedReportIds reports linked to asset
	 * @return indexed ESResearchContainer
	 * @throws DataNotFoundException exception
	 * @throws ServiceException exception
	 * @throws JsonProcessingException exception
	 */
	private ESResearchContainer getParseAndIndexResearch(String researchID, Set<String> linkedReportIds) throws DataNotFoundException, ServiceException, JsonProcessingException {
		ResearchContainerDTO researchContainerDTOmain = researchContainerClient.getOne(researchID);
		if (researchContainerDTOmain.getFigures() != null && !researchContainerDTOmain.getFigures().isEmpty()) {
			getAssetAndLinkedIds(researchContainerDTOmain, linkedReportIds);
		}
		indexAliasesBuilder.checkAndSetIndexSuffix(ESResearchContainer.class);
		ESResearchContainer esResearchContainer = doParsing(researchContainerDTOmain);
		LOGGER.info("Parsing successful for research document with ID: {}", esResearchContainer.getDocumentID());
		ESResearchContainer indexedEsResearchContainer = elasticSearchTemplate.save(esResearchContainer);
		LOGGER.info("Successfully indexed research document with ID: {}", indexedEsResearchContainer.getDocumentID());
		return indexedEsResearchContainer;
	}

	private void fetchAndprocessLinkedReportIds(Set<String> linkedReportIds) throws ServiceException, DataNotFoundException, JsonProcessingException {
		for (String reportId : linkedReportIds) {
			getParseAndIndexResearch(reportId, new HashSet<>());
		}
	}

	/**
	 *  method to fetch images assets and linked reports
	 * @param researchContainerDTO research data
	 * @param linkedReportIds reports linked to asset
	 */
	private void getAssetAndLinkedIds(ResearchContainerDTO researchContainerDTO, Set<String> linkedReportIds) {
		List<String> assetIds = researchContainerDTO.getFigures().parallelStream()
				.map(figure -> figure.getAsset() != null ? figure.getAsset().getId() : null)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		List<GraphQLAssetCollectionResponse> graphQLAssetCollectionResponseList = fetchAssetInfofromIds(assetIds);

		Map<String, Asset> assetMap = new HashMap<>();
		graphQLAssetCollectionResponseList.parallelStream().forEach(graphQLAssetCollectionResponse ->
				assetMap.putAll(graphQLAssetCollectionResponse.getData().getAssetCollection().getItems()
						.stream()
						.map(assetItem -> {
							Asset asset = new Asset();
							asset.setId(assetItem.getSys().getId());
							asset.setPublishedDate(assetItem.getSys().getPublishedAt());
							asset.setDescription(assetItem.getDescription());
							asset.setFileType(assetItem.getContentType());
							asset.setTitle(assetItem.getTitle());
							Set<String> linkedIds = new HashSet<>();
							if (assetItem.getLinkedFrom() != null && !assetItem.getLinkedFrom().getResearchCollection().getItems().isEmpty()) {
								linkedIds = assetItem.getLinkedFrom().getResearchCollection().getItems()
										.stream()
										.map(ResearchItem::getContentId).collect(Collectors.toSet());
							}
							if (assetItem.getLinkedFrom() != null
									&& !assetItem.getLinkedFrom().getResearchGraphicContainerCollection().getItems().isEmpty()) {
								List<String> containerIds = assetItem.getLinkedFrom().getResearchGraphicContainerCollection().getItems()
										.stream()
										.map(ResearchItem::getContentId).collect(Collectors.toList());
								linkedIds.addAll(processReportsFromGraphicContainerIds(containerIds));
							}
							linkedIds.remove(researchContainerDTO.getContentId());
							linkedReportIds.addAll(linkedIds);
							asset.setLinkedReports(new ArrayList<>(linkedIds));
							asset.setUrl(replaceUrlSuffix(assetItem.getUrl()));
							return asset;
						}).collect(Collectors.toMap(Asset::getId, Function.identity()))));

		researchContainerDTO.getFigures().forEach(figure -> figure.setAsset(assetMap.get(figure.getAsset().getId())));
	}

	private Set<String> processReportsFromGraphicContainerIds(List<String> containerIds) {
		List<GraphQlRgcCollectionResponse> graphQLAssetCollectionResponseList = new ArrayList<>();
		AtomicInteger counter = new AtomicInteger();

		containerIds.parallelStream().collect(Collectors.groupingBy(number -> (counter.getAndIncrement() / 100)))
				.values()
				.forEach(containerIdList -> {
					try {
						GraphQlRgcCollectionResponse graphQlRgcCollectionResponse = graphQLClient.getRGCfromIds(containerIdList);
						graphQLAssetCollectionResponseList.add(graphQlRgcCollectionResponse);
					} catch (DataNotFoundException | JsonProcessingException | ServiceException exception) {
						LOGGER.error("Exception while fetching research graphic container info from contentful " +
								"for containerIdList {}, message {}", containerIdList, exception.getMessage());
					}
				});
		Set<String> reportids = new HashSet<>();
		try {
			reportids = graphQLAssetCollectionResponseList.parallelStream().map(x -> x.getData().getResearchGraphicContainerCollection().getItems())
					.flatMap(Collection::stream)
					.map(x -> x.getLinkedFrom().getResearchCollection().getItems())
					.flatMap(Collection::stream)
					.map(ResearchItem::getContentId)
					.collect(Collectors.toSet());
		} catch (Exception e) {
			LOGGER.error("Exception while parsing report Ids for container Ids {}", containerIds);
		}
		return reportids;
	}

	/**
	 * Method to fetch image assets from contetntful in a batch of 100
	 * @param assetIds asset ids
	 * @return list of GraphQLAssetCollectionResponse
	 */
	private List<GraphQLAssetCollectionResponse> fetchAssetInfofromIds(List<String> assetIds) {
		List<GraphQLAssetCollectionResponse> graphQLAssetCollectionResponseList = new ArrayList<>();
		AtomicInteger counter = new AtomicInteger();

		assetIds.stream().collect(Collectors.groupingBy(number -> (counter.getAndIncrement() / CONTENTFUL_QUERY_LIMIT)))
				.values()
				.forEach(assetSysIds -> {
					try {
						GraphQLAssetCollectionResponse graphQLAssetCollectionResponse = graphQLClient.getAssetInfoByIds(assetSysIds, CONTENTFUL_QUERY_LIMIT);
						graphQLAssetCollectionResponseList.add(graphQLAssetCollectionResponse);
					} catch (DataNotFoundException | JsonProcessingException | ServiceException exception) {
						LOGGER.error("Exception while fetching assets info from contentful for assetIds {}, message {}", assetIds, exception.getMessage());
					}
				});
		return graphQLAssetCollectionResponseList;
	}

	private String replaceUrlSuffix(String url) {
		return url.replaceAll(imageUrlSearchValue, imageUrlReplaceValue);
	}

	/**
	 * PAE-835, to introduce delay in indexing request
	 */
	@LogThis
	private void handleDelay() {
		if(researchIndexDelayEnabled){
			try {
				LOGGER.info("Research indexing request delayed by {} ms",researchIndexDelayDuration);
				Thread.sleep(researchIndexDelayDuration);
			} catch (InterruptedException exception) {
				LOGGER.error("Exception while delaying research index API: {}",exception.getMessage());
			}
		}
	}

	/**
	 * This method deindex the entryId from elastic search.
	 *
	 * @param entryId
	 * @throws ServiceException
	 */
	@LogThis
	@Override
	public void deIndexById(String entryId) throws ServiceException {
		try {
			indexAliasesBuilder.checkAndSetIndexSuffix(ESResearchContainer.class);
			LOGGER.info("De-Indexing by the following entryId: {}", entryId);

			if (StringUtils.isNumeric(entryId)) {

				elasticSearchTemplate.delete(entryId, ESResearchContainer.class);
			} else {
				NativeSearchQuery deleteQuery = DeIndexQueryBuilder.buildDeleteQueryByField("entryId", entryId);
				elasticSearchTemplate.delete(deleteQuery, ESResearchContainer.class);
			}

		} catch (ElasticsearchStatusException e) {
			LOGGER.debug("De-Indexing failed for research document with ID: {} with cause: {}", entryId,
					e.getDetailedMessage());
			throw new ServiceException(String.format("De-Indexing failed for research document with ID: %s", entryId),
					e);
		}
	}

	/**
	 * Index into the existing Legacy Research Index without dropping the old index
	 * and creating new index
	 *
	 * @param chunkSize The size of bulk to index
	 * @return BulkUploadView
	 * @throws ServiceException
	 * @throws DataNotFoundException
	 */
	@LogThis
	@Override
	public BulkUploadView indexAll(int chunkSize) throws ServiceException, DataNotFoundException {
		Optional<IndexCoordinates> existingIndexCo = indexAliasesBuilder.createIndex(ESResearchContainer.class);
		Document indexConfig = Document.parse(StringUtils.replace(
				ResourceUtil.readFileFromClasspath("synonyms-analyzer-setting.json"), "SYNONYM-FILE-PATH", synonymFilePath));

		elasticSearchTemplate.indexOps(ESResearchContainer.class).create(indexConfig);
		elasticSearchTemplate.indexOps(ESResearchContainer.class).putMapping(elasticSearchTemplate.indexOps(ESResearchContainer.class).createMapping());

		long total = Optional.ofNullable(researchContainerClient.getResearchIds(CONTAINER_TYPE, 0, 1))
				.map(GraphQLResponse::getData).map(ResponseData::getResearchCollection)
				.map(ResearchCollection::getTotal).orElse(0L);
		long processed = 0;
		BulkUploadView.BulkUploadBuilder builder = BulkUploadView.builder(total);
		while (processed < total) {
			List<ResearchItem> items = Optional
					.ofNullable(researchContainerClient.getResearchIds(CONTAINER_TYPE, processed, chunkSize))
					.map(GraphQLResponse::getData).map(ResponseData::getResearchCollection)
					.map(ResearchCollection::getItems).orElse(Collections.emptyList());
			List<ESResearchContainer> parsedResearch = items.stream()
					.map(ResearchItem::getContentId)
					.filter(StringUtils::isNotBlank)
					.map(fetchResearchInfo(builder))
					.filter(Objects::nonNull)
					.map(researchContainerDTO -> {
						getAssetAndLinkedIds(researchContainerDTO, new HashSet<>());
						return researchContainerDTO;
					})
					.map(this::doParsing)
					.filter(Objects::nonNull).collect(Collectors.toList());

			try {
				Iterable<ESResearchContainer> indexedResearches = elasticSearchTemplate.save(parsedResearch);
				LOGGER.info("Indexing successful for {} out of {} records.", IterableUtils.size(indexedResearches),
						parsedResearch.size());
			} catch (BulkFailureException ese) {
				LOGGER.debug("Indexing failed for {} research documents with ID: {} with cause: {}",
						ese.getFailedDocuments().size(), ese.getFailedDocuments().keySet(),
						ese.getFailedDocuments().values());
				ese.getFailedDocuments().forEach((key, val) -> builder.addIndexingFailure(key, val));
			} catch (ElasticsearchStatusException esse) {
				LOGGER.debug("Indexing failed for {} research documents with ID: {} with cause: {}",
						parsedResearch.size(), esse.getResourceId(), esse.getDetailedMessage());
				parsedResearch.forEach(doc -> builder.addIndexingFailure(doc.getDocumentID(), esse.getMessage()));
			}
			processed += chunkSize;
		}
		indexAliasesBuilder.createAliases(ESResearchContainer.class, ALIAS_NAME, existingIndexCo);
		return builder.build();
	}

	private Function<String, ResearchContainerDTO> fetchResearchInfo(BulkUploadView.BulkUploadBuilder builder) {
		return researchId -> {
			try {
				return researchContainerClient.getOne(researchId);
			} catch (DataNotFoundException dataNotFoundException) {
				builder.addRetrievalFailure(researchId, dataNotFoundException.getMessage());
				return null;
			}
		};
	}

	private ESResearchContainer doParsing(ResearchContainerDTO researchContainer) {
		ESResearchContainer esResearchContainer = null;
		try {
			esResearchContainer = objectMapper.readValue(objectMapper.writeValueAsString(researchContainer), ESResearchContainer.class);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error converting research container DTO", e);
			return null;
		}

		Searchables.Builder searchablesBuilder = processSearchableInfo(researchContainer, esResearchContainer);
        esResearchContainer.setSearchables(searchablesBuilder.build());
		parseCompaniesInterviewed(esResearchContainer);

		String combined = new StringBuilder().append(esResearchContainer.getSearchables().getMainReportData())
		        .append(esResearchContainer.getSearchables().getWhatItMeans())
				.append(researchContainer.getResearchAbstract()).append(researchContainer.getTitle()).toString();
		esResearchContainer.setWordCount(StringUtils.isNotBlank(combined) ? new StringTokenizer(combined).countTokens() : 0);

		TaxonomyResponse taxonomyData = taxonomyClient.getTaxonomy(ContentType.RESEARCH_DOCUMENT,  esResearchContainer.getDocumentID());
		esResearchContainer.addTaxonomyData(taxonomyData);
		esResearchContainer.addTaxonomyAggs(taxonomyData);
		vectorizeResearch(esResearchContainer);
		addContentUrl(esResearchContainer);
		return esResearchContainer;
	}

	/**
	 * Method to fill searchable info
	 * Will fill main report data on the basis of precedence, BodyJson > body > archoveHTML > acrhiveJson
	 * @param researchContainer research Service response
	 * @param esResearchContainer entity to be indexed
	 * @return searchable
	 */
	private Searchables.Builder processSearchableInfo(ResearchContainerDTO researchContainer, ESResearchContainer esResearchContainer) {
		Searchables.Builder searchablesBuilder = Searchables.builder();
		searchablesBuilder.withResearchTitle(researchContainer.getTitle())
				.withResearchAbstract(researchContainer.getResearchAbstract());

		if (researchContainer.getBodyJson() != null && !researchContainer.getBodyJson().trim().isEmpty()) {
			searchablesBuilder.withMainReportData(researchContainer.getBodyJson());
		} else if (researchContainer.getBody() != null && !researchContainer.getBody().trim().isEmpty()) {
			searchablesBuilder.withMainReportData(researchContainer.getBody());
		} else if (Boolean.TRUE.equals(esResearchContainer.getIsArchived()) && esResearchContainer.getLegacyContentType().equalsIgnoreCase(SIRIUS_DECISIONS)) {
			searchablesBuilder.withMainReportData(esResearchContainer.getArchiveHtml());
		} else if (Boolean.TRUE.equals(esResearchContainer.getIsArchived()) && esResearchContainer.getLegacyContentType().equalsIgnoreCase(TYPE_FORRESTER)
				&& StringUtils.isNotBlank(esResearchContainer.getArchiveJson())) {
			Contents content = null;
			try {
				content = objectMapper.readValue(esResearchContainer.getArchiveJson(), Contents.class);
			} catch (JsonProcessingException e) {
				LOGGER.error("Error converting archiveJson from ESResearchContainer To Contents", e);
			}
			if (Objects.nonNull(content)) {
				List<Contents> contents = Collections.singletonList(content);
				searchablesBuilder.withMainReportData(ContentExtractor.extractContent(contents, MainContent::getMainReportSections))
						.withRecommendations(ContentExtractor.extractContent(contents, MainContent::getRecommendationsSections))
						.withWhatItMeans(ContentExtractor.extractContent(contents, MainContent::getWhatItMeansSections))
						.withHowForresterCanHelp(ContentExtractor.extractContent(contents, MainContent::getHowForrCanHelpSections));
			}
		}
		return searchablesBuilder;
	}

	/**
	 * method to fetch interviewed companies name from archiveJson
	 *
	 * @param esResearchContainer
	 */
	@LogThis
	private void parseCompaniesInterviewed(ESResearchContainer esResearchContainer) {
		if (Boolean.TRUE.equals(esResearchContainer.getIsArchived()) && !esResearchContainer.getArchiveJson().isEmpty()) {
			try {
				Contents content = objectMapper.readValue(esResearchContainer.getArchiveJson(), Contents.class);
				if (content != null && content.getMainContent() != null
						&& content.getMainContent().getSupplementalMaterialSections() != null) {
					List<Object> relatedMatListItem = content.getMainContent().getSupplementalMaterialSections()
							.stream()
							.filter(x -> x.getSectionTitle().getValue().matches(companyInterviewedExpression))
							.map(ReportSection::getSectionItems)
							.flatMap(List::stream)
							.filter(x -> x.get(TYPE).equals(RELATEDMATLIST))
							.filter(Objects::nonNull)
							.map(y -> y.get(VALUE))
							.collect(Collectors.toList());

					if (!relatedMatListItem.isEmpty()) {
						esResearchContainer.setCompaniesInterviewed(String.valueOf(((ArrayList) ((LinkedHashMap) relatedMatListItem.get(0))
								.get(RELATED_MAT_LIST_ITEM))
								.stream().collect(Collectors.joining(", "))));
					}
				}else{
					LOGGER.debug("Missing data in processing companiesInterviewed field for research {}",esResearchContainer.getContentId());
				}
			} catch (Exception e) {
				LOGGER.error("Error Processing companiesInterviewed field for research {}, message {}", esResearchContainer.getContentId(), e.getMessage());
			}
		}
	}

	private ESResearchContainer addContentUrl(ESResearchContainer esResearchContainer) {
		try {
			if(StringUtils.isNotEmpty(esResearchContainer.getContentId()) && StringUtils.isNotEmpty(esResearchContainer.getSlug())) {
				esResearchContainer.setContentURL(contentServiceClient.buildContentUrl(esResearchContainer.getContentId(),
						esResearchContainer.getSlug(), null, null, null, RESEARCH_TYPE));
			}
		} catch (ServiceException e) {
			LOGGER.error("Error while generating content URL", e);
			return null;
		} catch (JsonProcessingException e) {
			LOGGER.error("Error converting content url data", e);
			return null;
		}
		return esResearchContainer;
	}

	private ESResearchContainer vectorizeResearch(ESResearchContainer esResearchContainer) {

		JSONObject vectorizedTitle = new JSONObject();
		vectorizedTitle.put("text", SANITIZE_JSON.apply(esResearchContainer.getTitle()));
		List<Double> listOfTitleVectors = vectorizeClient.getVectorizedData(vectorizedTitle);

		StringBuilder combinedDataForVectorization = new StringBuilder(String.join(StringUtils.SPACE,
				esResearchContainer.getTitle(),
				esResearchContainer.getSubTitle(),
				esResearchContainer.getResearchAbstract(),
				esResearchContainer.getSearchables().getMainReportData(),
				esResearchContainer.getSearchables().getRecommendations(),
				esResearchContainer.getSearchables().getWhatItMeans(),
				esResearchContainer.getSearchables().getHowForresterCanHelp()));

		JSONObject vectorizedData = new JSONObject();
		vectorizedData.put("text", SANITIZE_JSON.apply(combinedDataForVectorization.toString()));

		List<Double> listOfSearchableVectors = vectorizeClient.getVectorizedData(vectorizedData);

		esResearchContainer.setTitle_vector(listOfTitleVectors);
		esResearchContainer.setSearchable_vector(listOfSearchableVectors);
		return esResearchContainer;
	}
}

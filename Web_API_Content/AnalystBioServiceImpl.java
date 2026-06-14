package com.forrester.content.service.impl;

import com.forrester.content.clients.analyst.response.AnalystBio;
import com.forrester.content.clients.analyst.response.Bio;
import com.forrester.content.clients.analyst.response.CMSAnalystResponse;
import com.forrester.content.clients.analyst.response.Heading;
import com.forrester.content.clients.cms.CMSContentClient;
import com.forrester.content.clients.contentful.utils.LogThis;
import com.forrester.content.clients.lscs.LSCSQueryClient;
import com.forrester.content.clients.taxonomy.TaxonomyClient;
import com.forrester.content.clients.taxonomy.response.Tag;
import com.forrester.content.clients.taxonomy.response.TaxonomyResponse;
import com.forrester.content.clients.user.UserClient;
import com.forrester.content.clients.user.response.UserProfileResponse;
import com.forrester.content.dto.Analyst;
import com.forrester.content.exception.DataNotFoundException;
import com.forrester.content.service.AnalystBioService;
import com.forrester.content.util.ContentMetadataDateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RefreshScope
public class AnalystBioServiceImpl implements AnalystBioService {

	public static final String GUEST = "Guest";
	public static final String LINKEDIN = "LinkedIn";
	public static final String FACEBOOK = "Facebook";
	public static final String TWITTER = "Twitter";
	public static final String WEIBO = "Weibo";
	public static final String BIO_PREFIX = "BIO";
	public static final String ACTIVE_VALUE = "1";
	public static final String BUSINESS_TOPICS = "Business Topics";
	public static final String ROLE_PARENT_TAXONOMY = "Old Role";
	public static final String INDUSTRY_PARENT = "Industry";
	public static final String TECH_SERVICES_CAT = "Technology and Services Categories";
	public static final String SERVICES = "Services";

	@Autowired
	private CMSContentClient cmsContentClient;

	@Autowired
	private UserClient userClient;

	@Autowired
	private TaxonomyClient taxonomyClient;

	@Autowired
	private ThreadPoolTaskExecutor contentRetrievalExecutor;
	
	@Autowired
	private LSCSQueryClient lscsQueryClient;

	/**
	 * This method get Analyst bio details from CMS Content service, User Service
	 * and Taxonomy Service for given bio Id
	 * 
	 * @param bioId
	 * @param idToken
	 * @return AnalystBio
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * 
	 */
	@Override
	@LogThis
	public AnalystBio getAnalystBioDetails(String bioId, String idToken)
			throws InterruptedException, ExecutionException, DataNotFoundException {

		Future<CMSAnalystResponse> futureCmsContent = contentRetrievalExecutor
				.submit(() -> cmsContentClient.getCMSAnalystBioByID(StringUtils.remove(bioId, BIO_PREFIX)));
		Future<TaxonomyResponse> futureTaxonomy = contentRetrievalExecutor
				.submit(() -> taxonomyClient.getTaxonomy(bioId));
		Future<UserProfileResponse> futureUserProfile = contentRetrievalExecutor
				.submit(() -> userClient.getUserProfile(idToken));

		CMSAnalystResponse cmsAnalystResponse = futureCmsContent.get();
		TaxonomyResponse taxonomyResponse = futureTaxonomy.get();
		UserProfileResponse userProfileResponse = futureUserProfile.get();

		return convertAnalystBio(cmsAnalystResponse, taxonomyResponse, userProfileResponse);
	}

	/**
	 * This method convert CMS Analyst Content, Taxonomy Tags and User Profile
	 * Details into AnalystBio.
	 * 
	 * @param cmsAnalystResponse
	 * @param taxonomyResponse
	 * @param userProfileResponse
	 * @return AnalystBio
	 */
	@LogThis
	private AnalystBio convertAnalystBio(CMSAnalystResponse cmsAnalystResponse, TaxonomyResponse taxonomyResponse,
			UserProfileResponse userProfileResponse) {
		AnalystBio.Builder analystBioBuilder = AnalystBio.builder();
		Bio bio = cmsAnalystResponse.getBio();
		if (bio != null) {
			if (bio.getHeading() != null) {
				Heading heading = bio.getHeading();
				String fullName = Stream.of(heading.getFirstName(), heading.getMiddleName(), heading.getLastName()).filter(StringUtils::isNotBlank).collect(Collectors.joining(StringUtils.SPACE));
				analystBioBuilder.setAnalystBioId(heading.getMetaId()).setEducation(heading.getEducation())
						.setFirstName(heading.getFirstName()).setMiddleName(heading.getMiddleName())
						.setLastName(heading.getLastName()).setTitle(heading.getTitle()).setFullName(fullName).setBioTitle(heading.getBioTitle());

				analystBioBuilder.setAnalystBlog(heading.getAnalystBlog());
			}
			if (bio.getPeopleSoftInfo() != null) {
				analystBioBuilder.setEmail(bio.getPeopleSoftInfo().getBusinessEmailAddress());
			}
			if (cmsAnalystResponse.getMetadata() != null) {
				analystBioBuilder.setExternallyActive(cmsAnalystResponse.getMetadata().getPropertyExternallyActive().equals(ACTIVE_VALUE))
						.setPublishedDate(ContentMetadataDateUtil.convertDate(cmsAnalystResponse.getMetadata().getPublishDate(), ContentMetadataDateUtil.ANALYST_BIO_PUBLISHED_DATE_FORMAT))
						.setUserType(cmsAnalystResponse.getMetadata().getPropertyUserType())
						.setUrlName(cmsAnalystResponse.getMetadata().getPropertyUrlName());
			}
			List<String> rolesFromTaxonomy = getTagValues(taxonomyResponse, ROLE_PARENT_TAXONOMY);
			analystBioBuilder.setPreviousWorkExperience(bio.getProsePreviousWorkExperience())
					.setPrimaryRoles(rolesFromTaxonomy)
					.setResearchFocus(bio.getProseResearchFocus())
					.setRoles(rolesFromTaxonomy);
			if (bio.getSocialmedia() != null) {
				analystBioBuilder.setSocialMedia(getSocialMedia(bio));
			}

			List<String> industries = getTagValues(taxonomyResponse, INDUSTRY_PARENT);
			List<String> businessTopics = getTagValues(taxonomyResponse, BUSINESS_TOPICS);
			List<String> techAndServicesCategories = getTagValues(taxonomyResponse, TECH_SERVICES_CAT);

			List<String> topics = Stream.of(industries, businessTopics, techAndServicesCategories)
					.flatMap(List::stream)
					.distinct()
					.collect(Collectors.toList());
			analystBioBuilder.setTopics(topics);
			
			if (bio.getImage() != null) {
				analystBioBuilder.setImage(bio.getImage().getAnalystImage())
						.setImageHighRes(bio.getImage().getAnalystHighRes());
			}
			analystBioBuilder.setIndustries(industries);
		}
		Map<String, List<Tag>> tags = new HashMap<>();
		if (!taxonomyResponse.getTags().isEmpty()) {
			tags.put(BUSINESS_TOPICS, getTagList(taxonomyResponse, BUSINESS_TOPICS));
			tags.put(SERVICES, getTagList(taxonomyResponse, SERVICES));
		}
		analystBioBuilder.setTags(tags);

		if (StringUtils.isNotEmpty(userProfileResponse.getUserType()) && !userProfileResponse.getUserType().equalsIgnoreCase(GUEST)) {
			analystBioBuilder.setIsClient(true);
		}

		return analystBioBuilder.build();
	}

	/**
	 * This method return socialMedia details which are not null.
	 * 
	 * @param bio
	 * @return Map<String, String>
	 */
	public Map<String, String> getSocialMedia(Bio bio) {
		Map<String, String> socialMediaMap = new HashMap<>();
		if (bio.getSocialmedia().getLinkedIn() != null) {
			socialMediaMap.put(LINKEDIN, bio.getSocialmedia().getLinkedIn());
		}
		if (bio.getSocialmedia().getFacebook() != null) {
			socialMediaMap.put(FACEBOOK, bio.getSocialmedia().getFacebook());
		}
		if (bio.getSocialmedia().getTwitter() != null) {
			socialMediaMap.put(TWITTER, bio.getSocialmedia().getTwitter());
		}
		if (bio.getSocialmedia().getWeibo() != null) {
			socialMediaMap.put(WEIBO, bio.getSocialmedia().getWeibo());
		}
		return socialMediaMap;
	}

	/**
	 * This method checks for Tag whether parent is present or not and matches for
	 * type.
	 * 
	 * @param tag
	 * @param type
	 * @return boolean
	 */
	private boolean isParentPresent(Tag tag, String type) {
		boolean isParent = tag.getParent() != null;
		if (isParent && tag.getParent().getName().equalsIgnoreCase(type)) {
			return true;
		} else if (isParent) {
			return isParentPresent(tag.getParent(), type);
		}
		return false;
	}

	private List<String> getTagValues(TaxonomyResponse taxonomyResponse, String parentType){
		return getTagList(taxonomyResponse, parentType)
				.stream()
				.map(Tag::getName)
				.map(StringUtils::chomp)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
	}
	
	/**
	 * This method gets the tag list for specified type.
	 * 
	 * @param taxonomyResponse
	 * @param type
	 * @return List<Tag>
	 */
	private List<Tag> getTagList(TaxonomyResponse taxonomyResponse, String type) {
		return taxonomyResponse.getTags().stream().filter(tag -> isParentPresent(tag, type))
				.collect(Collectors.toList());
	}

	/**
	 * Purpose of this method is to get all analyst from Teamsite and return
	 * Property.Title and Property.Endeca_id.
	 */
    @Override
    @LogThis
    public List<Analyst> getAllAnalyst() throws DataNotFoundException {
    	return lscsQueryClient.getAllAnalystBios();
    }

}

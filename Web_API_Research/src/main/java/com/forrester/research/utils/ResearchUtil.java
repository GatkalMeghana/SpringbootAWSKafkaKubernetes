package com.forrester.research.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.contentful.java.cda.CDAEntry;
import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.response.models.RecommendedResearch;
import com.forrester.research.clients.contentful.response.models.Research;
import com.forrester.research.clients.permissions.response.ResearchPermission;
import com.github.slugify.Slugify;
import com.google.gson.internal.LinkedTreeMap;

/**
 * @author sgopal
 *
 */
@Service
public class ResearchUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchUtil.class);
	/**
	 * @param research Research
	 * @param recommendedResearchList List of RecommendedResearch
	 */
	@SuppressWarnings("unchecked")
	public void mapToResearchView(Research research, List<RecommendedResearch> recommendedResearchList) {
		
		for(Object recommendedResearchContent:research.getRecommendedResearchContent()) {
			if(recommendedResearchContent instanceof Research) {
				Research resContent = (Research)recommendedResearchContent;
				RecommendedResearch recommendedResearch = new RecommendedResearch();
				recommendedResearch.setId(resContent.getContentId());
				recommendedResearch.setSlug(resContent.getSlug());
				recommendedResearch.setTitle(resContent.getTitle());
				recommendedResearch.setContentType(((LinkedTreeMap<String, String>)resContent.getContentType().get("sys")).get("id"));
				recommendedResearchList.add(recommendedResearch);
			}
			else if(recommendedResearchContent instanceof CDAEntry) {
				CDAEntry contentEntry = (CDAEntry)recommendedResearchContent;
				RecommendedResearch recommendedResearch = new RecommendedResearch();
				recommendedResearch.setId(contentEntry.id());
				recommendedResearch.setTitle(contentEntry.getField("en-US", "title"));
				recommendedResearch.setContentType(contentEntry.contentType().id());
				recommendedResearchList.add(recommendedResearch);
			}
			else {
				LOGGER.error("Recommended Research Content could not be mapped to view. Please check type.");
			}
		}
	}
	
	public Research handleSlug(Research research) {
		if(StringUtils.isBlank(research.getSlug()) && StringUtils.isNotBlank(research.getTitle())) {
			Slugify slg = new Slugify();
			String slug = slg.slugify(research.getTitle());
			if(slug.length() >70) {
				slug=slug.substring(0, 70);
				research.setSlug(slug.substring(0, slug.lastIndexOf("-")));
			}
			else {
				research.setSlug(slug);
			}
		}
		return research;
	}

	/**
	 * This method creates and set content url.
	 * 
	 * @param research Research
	 */
	public void createContentUrl(Research research) {
		research.setContentUrl(String.join(Constants.SLASH, Constants.REPORT, research.getSlug(), research.getContentId()));
	}

	public void populateCourtesyView(ResearchPermission researchPermission, Map researchMap) {
		if(researchPermission.getRuleApplied() != null && researchPermission.getRuleApplied().equalsIgnoreCase("purchasedCheckRule")) {
			researchMap.put("isCourtesyView",true);
		}
		researchMap.put(Constants.IS_FLEX_ACCESS_REDEEMED, researchPermission.isFlexAccessRedeemed());
	}
}
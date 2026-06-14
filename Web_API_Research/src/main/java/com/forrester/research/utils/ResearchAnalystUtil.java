package com.forrester.research.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.ContentfulClient;
import com.forrester.research.clients.contentful.response.ContentfulModelStore;
import com.forrester.research.clients.contentful.response.models.*;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.forrester.research.exception.*;
import com.google.gson.Gson;

@Component
public class ResearchAnalystUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchAnalystUtil.class);
	
	@Autowired
	private ContentfulModelStore contentfulModelStore;
	
	@Autowired
	private ContentfulClient contentfulClient;
	
	@Autowired
	URLMasker urlMasker;
	
	/**
	 * @param research Research
	 */
	public void populateLatestAuthorsAndContributorsData(Research research) {
		Map<String, List<String>> analystMap = new HashMap<>();
		List<Analyst> serializedAnalysts = new ArrayList<>();
		if (research.getAuthors() != null) {
			analystMap.put(Constants.AUTHORS, getAnalystIds(research.getAuthors(),serializedAnalysts));
		}
		if (research.getContributors() != null) {
			analystMap.put(Constants.CONTRIBUTORS, getAnalystIds(research.getContributors(),serializedAnalysts));
		}
		Map<String, List<Analyst>> authAndContributorMap = getAuthorsAndContributors(analystMap, serializedAnalysts);
		if(!authAndContributorMap.isEmpty() && (authAndContributorMap.containsKey(Constants.AUTHORS) || authAndContributorMap.containsKey(Constants.CONTRIBUTORS))) {
			research.setAuthors(authAndContributorMap.get(Constants.AUTHORS));
			research.setContributors(authAndContributorMap.get(Constants.CONTRIBUTORS));
		}
	}

	/**
	 * @param analystMap Map from String to List<String>
	 * @param serializedAuthors List of Analyst
	 * @return Map<String, List<Analyst>>
	 */
	private Map<String, List<Analyst>> getAuthorsAndContributors(Map<String, List<String>> analystMap, List<Analyst> serializedAuthors) {
		Map<String, List<Analyst>> authAndContributorMap = new HashMap<>();
		Set<String> authorIds = new HashSet<>();
		for(Map.Entry<String, List<String>> entry : analystMap.entrySet()) {
			List<String> ids = Stream.of(entry.getValue())
	                .flatMap(Collection::stream)
	                .collect(Collectors.toList());
			authorIds.addAll(ids);
		}
		
		if (!authorIds.isEmpty()) {
			List<Analyst> cfAnalysts = new ArrayList<>();
			Class<?> contentModel = contentfulModelStore.getModel(Constants.ANALYST);
			authorIds.forEach(id -> {
				Analyst analyst;
				try {
					analyst = (Analyst) contentfulClient.getContentDetailsByField(contentModel, "contentId", id);
					cfAnalysts.add(analyst);
				} catch (DataNotFoundException dnfe) {
					LOGGER.info("Data not found, please check stacktrace: {}", dnfe.getMessage());
				} catch (ServiceException se) {
					LOGGER.info("Service exception, please check stacktrace: {}", se.getMessage());
				}
			});
			
			Set<Analyst> updatedAnalysts = new HashSet<>();
			mapAnalystInfo(serializedAuthors, cfAnalysts, updatedAnalysts);
			if(!updatedAnalysts.isEmpty() && analystMap.containsKey(Constants.AUTHORS)) {
				authAndContributorMap.put(Constants.AUTHORS,	extractAuthorAndContributor(analystMap.get(Constants.AUTHORS), updatedAnalysts));
			}
			if(!updatedAnalysts.isEmpty() && analystMap.containsKey(Constants.CONTRIBUTORS)) {
				authAndContributorMap.put(Constants.CONTRIBUTORS, extractAuthorAndContributor(analystMap.get(Constants.CONTRIBUTORS), updatedAnalysts));
			}
		}
		return authAndContributorMap;
	}

	/**
	 * @param analystIds List of String
	 * @param updatedAnalysts Set of Analyst
	 * @return List<Analyst>
	 */
	private List<Analyst> extractAuthorAndContributor(List<String> analystIds, Set<Analyst> updatedAnalysts) {
		List<Analyst> updatedAnalystData = new ArrayList<>();
		for(String analystId: analystIds) {
			for(Analyst analystInfo : updatedAnalysts) {
				if(analystInfo.getContentId().equalsIgnoreCase(analystId)) {
					updatedAnalystData.add(analystInfo);
				}
			}
		}
		return updatedAnalystData;
	}

	/**
	 * @param authors List of Analyst
	 * @param serializedAuthors List of Analyst
	 * @return List<String>
	 */
	private List<String> getAnalystIds(List<Analyst> authors, List<Analyst> serializedAuthors) {
		Gson gson = new Gson();
		List<String> ids = new ArrayList<>();
		//for (Analyst analyst : authors) {
		for (int i=0;i<authors.size();i++) {
			Analyst author = gson.fromJson(gson.toJson(authors.get(i)), Analyst.class);
			serializedAuthors.add(author);
			ids.add(author.getContentId());
		}
		return ids;
	}

	/**
	 * @param serializedAnalysts List of Analyst
	 * @param cfAnalysts List of Analyst
	 * @param updatedAnalysts Set of Analyst
	 */
	private void mapAnalystInfo(List<Analyst> serializedAnalysts, List<Analyst> cfAnalysts,
			Set<Analyst> updatedAnalysts) {
		if(cfAnalysts != null && !cfAnalysts.isEmpty()) {
			for (Analyst analyst : serializedAnalysts) {
				Optional<Analyst> a = cfAnalysts.stream()
						.filter(value -> value.getContentId().equalsIgnoreCase(analyst.getContentId())).findFirst();
				if (a.isPresent()) {
					analyst.setFirstName(StringUtils.isEmpty(a.get().getFirstName()) ? null : a.get().getFirstName());
					analyst.setLastName(StringUtils.isEmpty(a.get().getLastName()) ? null : a.get().getLastName());
					analyst.setTitle(StringUtils.isEmpty(a.get().getTitle())? null : a.get().getTitle());
					analyst.setActive(a.get().isActive());
					analyst.setImageUrl(StringUtils.isEmpty(a.get().getImageUrl()) ? null : a.get().getImageUrl());
					if (a.get().getAnalystImages() != null && a.get().getAnalystImages().mimeType().startsWith("image")) {
						analyst.setImageUrl(urlMasker.maskPublicImage(a.get().getAnalystImages().url(), a.get().getAnalystImages().mimeType()));
						analyst.setAnalystImages(null);
					}
				}
				updatedAnalysts.add(analyst);
			}
		}
	}
}
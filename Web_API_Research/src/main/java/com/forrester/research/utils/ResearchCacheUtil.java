package com.forrester.research.utils;

import com.forrester.research.service.impl.ContentFormatter;
import com.forrester.research.view.ResearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class ResearchCacheUtil {
	// for contentId -> research
	private static final String CACHE_PREFIX="{research_content}";
	private static final String CACHE_KEY="RESEARCH_CACHE";
	private static final String OVERVIEW_CACHE_KEY="RESEARCH_OVERVIEW_CACHE";

	// For entryId -> contentId
	private static final String CACHE_CONTENT_ID_PREFIX="{research_content_id}";
	private static final String CACHE_CONTENT_ID_KEY="RESEARCH_CONTENT_ID_CACHE";
	private static final String RESEARCH_CONTENT_ID_OVERVIEW_CACHE_KEY="RESEARCH_CONTENT_ID_OVERVIEW_CACHE";

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ContentFormatter contentFormatter;
	
	@Value("${forr.researchservice.redis.enableCache}")
	private boolean enableCache;
	
	@Value("${forr.researchservice.redis.ttl}")
	private long ttl;
	
	@Value("${forr.researchservice.redis.ttl.unit}")
	private String ttlUnit;
	
	private HashOperations<String, String, Object> hashOperations;
	
	@PostConstruct
	private void intializeHashOperations() {
		hashOperations = redisTemplate.opsForHash();
	}
	
	public void populateCache(boolean hasAccess, String contentId, Map<Object, Object> research) {
		TimeUnit timeUnit = TimeUnit.valueOf(ttlUnit);
		if(hasAccess) {
			// Research Info
			hashOperations.put(CACHE_PREFIX+CACHE_KEY, contentId, research);
			redisTemplate.expire(CACHE_PREFIX+CACHE_KEY, ttl, timeUnit);

			// EntryId to ContentId info
			Object entryId = research.get("id");
			if(Objects.nonNull(entryId)){
				hashOperations.put(CACHE_CONTENT_ID_PREFIX+CACHE_CONTENT_ID_KEY, (String) entryId, contentId);
				redisTemplate.expire(CACHE_CONTENT_ID_PREFIX+CACHE_CONTENT_ID_KEY, ttl, timeUnit);
			}
		}
		else{
			//feed cache
			hashOperations.put(CACHE_PREFIX+OVERVIEW_CACHE_KEY, contentId, research);
			redisTemplate.expire(CACHE_PREFIX+OVERVIEW_CACHE_KEY, ttl, timeUnit);

			// EntryId to ContentId info
			Object entryId = research.get("id");
			if(Objects.nonNull(entryId)){
				hashOperations.put(CACHE_CONTENT_ID_PREFIX+RESEARCH_CONTENT_ID_OVERVIEW_CACHE_KEY, (String) entryId, contentId);
				redisTemplate.expire(CACHE_CONTENT_ID_PREFIX+RESEARCH_CONTENT_ID_OVERVIEW_CACHE_KEY, ttl, timeUnit);
			}
		}

	}

	/**
	 * @param contentId String
	 * @param formattedContent boolean
	 * @param hasAccess boolean
	 * @param researchView ResearchView
	 * @return Object
	 */
	public Object getDataFromCache(String contentId, boolean formattedContent, boolean hasAccess, ResearchView researchView) {
		if(hasAccess) {
			Object researchCache = hashOperations.get(CACHE_PREFIX+CACHE_KEY, contentId);
			if (researchCache != null) {
				researchView.setResearch(researchCache);
				if (formattedContent) {
					researchView.setResearch(contentFormatter.getFormattedResearch(researchCache));					
				}
				return researchView;
			}
		} else {
			Object researchCacheOverview = hashOperations.get(CACHE_PREFIX+OVERVIEW_CACHE_KEY, contentId);
			if(researchCacheOverview != null){
				researchView.setResearch(researchCacheOverview);
				if (formattedContent) {
					researchView.setResearch(contentFormatter.getFormattedResearch(researchCacheOverview));
				}
				return researchView;
			}
		}
		return null;		
	}
}

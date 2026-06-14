package com.forrester.content.clients.taxonomy;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.forrester.content.clients.contentful.utils.LogThis;
import com.forrester.content.clients.taxonomy.response.TaxonomyResponse;

/**
 * @author Atiqur Rehman
 * 
 *         It's used for getting tags details from taxonomy service/tagging
 *         tools
 *
 */
@Component
@RefreshScope
public class TaxonomyClient {

	private static final String APIKEY_HEADER = "apiKey";
	private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyClient.class);

	@Value("${forr.taxonomy.service.url}")
	private String taxonomyServiceURL;

	@Value("${forr.services.api.key}")
	private String apiKey;

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${forr.taxonomy.service.ids.url}")
	private String taxonomyServiceIdsURL;
	
	/**
	 * This method is used for get tagging information based contentId
	 * 
	 * @param contentId
	 * @return TaxonomyResponse
	 */

	@LogThis
	public TaxonomyResponse getTaxonomy(String contentId) {
		URI uri = UriComponentsBuilder.fromUriString(taxonomyServiceURL)
				.buildAndExpand(Collections.singletonMap("id", contentId)).toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.put(APIKEY_HEADER, Collections.singletonList(apiKey));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			ResponseEntity<TaxonomyResponse> response = restTemplate.exchange(requestEntity, TaxonomyResponse.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		} catch (Exception e) {
			LOGGER.error("Http client/server exception while getting Response from Taxonomy Service for the content {}",
					contentId, e);
		}
		return new TaxonomyResponse();
	}
	
	/**
	 * This method is used for getting tagging information based on multiple contentIds
	 * 
	 * @param contentIds
	 * @return Map<String, TaxonomyResponse>
	 */
	@LogThis
	public Map<String, TaxonomyResponse> getTaxonomies(String[] contentIds) {
		URI uri = UriComponentsBuilder.fromUriString(taxonomyServiceIdsURL)
				.queryParam("ids", String.join(",", contentIds)).build().toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.put(APIKEY_HEADER, Collections.singletonList(apiKey));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		
		try {
			ResponseEntity<Map<String, TaxonomyResponse>> response = restTemplate.exchange(requestEntity, 
					new ParameterizedTypeReference<Map<String, TaxonomyResponse>>() {});
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		} catch (Exception e) {
			LOGGER.error("Http client/server exception while getting Response from Taxonomy Service for the content {}",
					contentIds, e);
		}
		return new HashMap<>();
	}

}
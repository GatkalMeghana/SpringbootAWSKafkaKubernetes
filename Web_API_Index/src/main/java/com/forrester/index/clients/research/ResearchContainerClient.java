package com.forrester.index.clients.research;

import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.forrester.index.clients.contentful.response.GraphQLResponse;
import com.forrester.index.clients.research.response.ResearchContainerDTO;
import com.forrester.index.clients.research.response.ResearchResponse;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.utils.LogThis;

/**
 * Responsiblity of this class is to connect to research service and get research
 * document details.
 * 
 * @author meghanag
 *
 */
@Component
@RefreshScope
public class ResearchContainerClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchContainerClient.class);
	public static final String APIKEY_HEADER_TOKEN = "apiKey";

	@Value("${forr.researchservice.list.url}")
	private String researchServiceListUrl;
	
	@Value("${forr.researchservice.url}")
	private String researchServiceUrl;
	
	@Value("${forr.researchservice.api.key}")
	private String researchApiKey;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * This method get research ids using contentful graph ql query for given
	 * container type,skip and limit.
	 * 
	 * @param containerType
	 * @param skip
	 * @param limit
	 * @return GraphQLResponse
	 * @throws DataNotFoundException
	 * @throws ServiceException
	 * @return GraphQLResponse
	 */
	@LogThis
	public GraphQLResponse getResearchIds(String containerType, long skip, long limit) throws DataNotFoundException, ServiceException {
		URI uri = UriComponentsBuilder.fromUriString(researchServiceListUrl).queryParam("containerType", containerType)
				.queryParam("limit", String.valueOf(limit)).queryParam("skip", String.valueOf(skip)).build().toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(researchApiKey));

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			ResponseEntity<GraphQLResponse> response = restTemplate.exchange(requestEntity, GraphQLResponse.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
				throw new DataNotFoundException("Unable to find the research content ids with the given details");
			}
			LOGGER.error("Http client/server exception for the request: {} and containerType: {}", requestEntity, containerType, e);
			throw new ServiceException("Service exception while getting content from research service");
		} 
		return new GraphQLResponse();
	}
	
	/**
	 * This method fetch the formatted research content for the given research id
	 * from research service.
	 * 
	 * @param researchId
	 * @return ResearchContainerDTO
	 */
	@LogThis
	public ResearchContainerDTO getOne(String researchId) throws DataNotFoundException {
		return ResearchContainerHandler.from(getResearchData(researchId));
	}

	/**
	 * This method fetch the formatted research content for the given research id from research
	 * service.
	 * 
	 * @param researchId
	 * @return ResearchResponse
	 */
	@LogThis
	private ResearchResponse getResearchData(String researchId) throws DataNotFoundException {
		URI uri = UriComponentsBuilder.fromUriString(researchServiceUrl)
				.buildAndExpand(Collections.singletonMap("id", researchId)).toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(researchApiKey));

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			ResponseEntity<ResearchResponse> response = restTemplate.exchange(requestEntity, ResearchResponse.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		}catch (HttpClientErrorException httpClientErrorException){
			if(httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND){
				LOGGER.error("No data returned for research id: {}", researchId);
				throw new DataNotFoundException(String.format("No data returned for research id: %s",researchId), httpClientErrorException.getCause());
			}
		} catch (Exception e) {
			LOGGER.error(">>> Exception while getting response from research Service", e);
			throw new DataNotFoundException(String.format("Exception while getting response from research Service for id: %s",researchId), e.getCause());
		}
		return new ResearchResponse();
	}

}

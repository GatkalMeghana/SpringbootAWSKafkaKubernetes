package com.forrester.index.clients.forrservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.forrester.index.clients.forrservice.response.ForrServiceResponse;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.utils.LogThis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

/**
 * This class is used to connect ForrService service to get details of
 * forrServices.
 * 
 * @author shaileshs
 *
 */
@Component
@RefreshScope
public class ForrServiceClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ForrServiceClient.class);
	public static final String APIKEY = "apiKey";

	@Value("${forr.forrservice.url}")
	private String forrServiceUrl;

	@Value("${forr.forrservice.ids.url}")
	private String forrServiceIdsUrl;

	@Value("${forr.forrservice.api.key}")
	private String forrServiceApiKey;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RetryTemplate retryTemplate;

	/**
	 * This method get forrService data from ForrService service
	 * 
	 * @param forrServiceId
	 * @return ForrServiceResponse
	 * @throws DataNotFoundException
	 * @throws ServiceException
	 */
	@LogThis
	public ForrServiceResponse getForrServiceData(String forrServiceId) throws DataNotFoundException, ServiceException {
		URI uri = UriComponentsBuilder.fromUriString(forrServiceUrl)
				.buildAndExpand(Collections.singletonMap("id", forrServiceId)).toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.put(APIKEY, Collections.singletonList(forrServiceApiKey));

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			ResponseEntity<ForrServiceResponse> response = restTemplate.exchange(requestEntity, ForrServiceResponse.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
				throw new DataNotFoundException("Unable to find the forrService content id with the given details");
			}
			LOGGER.error("Http client/server exception for the request: {}", requestEntity, e);
			throw new ServiceException("Service exception while getting content from forrService service");
		}
		return new ForrServiceResponse();
	}

	/**
	 * Client to fetch all forrServcie Ids
	 *
	 * @return forr Servic Ids
	 * @throws DataNotFoundException No Service IDs returned
	 * @throws ServiceException      Service Exception
	 */
	@LogThis
	public Optional<JsonNode> getAllForrServiceIds() throws DataNotFoundException, ServiceException {
		URI uri = UriComponentsBuilder.fromUriString(forrServiceIdsUrl).build().toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.put(APIKEY, Collections.singletonList(forrServiceApiKey));

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			return retryTemplate.execute(args -> {
						ResponseEntity<JsonNode> response = restTemplate.exchange(requestEntity, JsonNode.class);
						if (response.getStatusCode().is2xxSuccessful()) {
							return Optional.ofNullable(response.getBody());
						}
						return Optional.empty();
					}
			);
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals((HttpStatus.NOT_FOUND)) || e.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
				throw new DataNotFoundException("Unable to find the forrService content ids");
			}
			LOGGER.error("Http client/server exception for the request: {}", requestEntity, e);
			throw new ServiceException("Service exception while getting content from forrService service");
		}
	}

}

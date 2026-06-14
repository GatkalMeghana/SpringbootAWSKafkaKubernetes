package com.forrester.research.clients.contentful;

import com.forrester.research.clients.contentful.response.GraphQLResponse;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * This class call contentful service to get all ids using graph ql.
 *
 * @author meghanag
 *
 */
@Component
@RefreshScope
public class ContentfulGraphQLClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentfulGraphQLClient.class);

	@Value("${forr.contentful.graphql.url}")
	private String contentfulGraphQlUrl;

	@Value("${forr.contentful.graphql.token}")
	private String contentfulGraphQLToken;

	@Value("${forr.contentful.graphql.byid.url}")
	private String contentfulGraphQlByIdUrl;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * This method get all research Ids as per container type from contentful using
	 * graphql.
	 *
	 * @param containerType
	 * @param skip
	 * @param limit
	 * @return GraphQLResponse
	 * @throws ServiceException
	 * @throws DataNotFoundException
	 */
	@LogThis
	public GraphQLResponse getResearchIds(String containerType, long skip, long limit, Date publishDate)
			throws ServiceException, DataNotFoundException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		URI uri = UriComponentsBuilder.fromUriString(String.format(contentfulGraphQlUrl, containerType, skip, limit, format.format(publishDate)))
				.queryParam("access_token", contentfulGraphQLToken).build().toUri();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			ResponseEntity<GraphQLResponse> response = restTemplate.exchange(requestEntity, GraphQLResponse.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
				throw new DataNotFoundException("Unable to find research ids with the given details");
			}
			LOGGER.error("Http client/server exception for the request: {}", requestEntity, e);
			throw new ServiceException("Service exception while getting Response from Contentful Graph ql Service", e);
		}
		return new GraphQLResponse();
	}

	/**
	 * This method get research by id.
	 *
	 * @param id
	 * @return Map
	 * @throws DataNotFoundException
	 * @throws ServiceException
	 */
	public Map getResearchId(String id) throws DataNotFoundException, ServiceException {

		URI uri = UriComponentsBuilder.fromUriString(String.format(contentfulGraphQlByIdUrl, id))
				.queryParam("access_token", contentfulGraphQLToken).build().toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(requestEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find research id");
            }
            LOGGER.error("Http client/server exception for the request: {}", requestEntity, e);
            throw new ServiceException("Service exception while getting Response from Contentful Graph ql Service", e);
        }
		return Collections.emptyMap();
	}
}
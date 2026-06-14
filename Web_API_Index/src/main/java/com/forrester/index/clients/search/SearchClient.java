package com.forrester.index.clients.search;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forrester.index.clients.search.response.SearchResult;
import com.forrester.index.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Component
@RefreshScope
public class SearchClient {

    static Logger logger = LoggerFactory.getLogger(SearchClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${forr.searchservice.url}")
    private String searchServiceURL;

    @Value("${forr.searchservice.api.key}")
    private String apiKey;

    @LogThis
    public Page<SearchResult> searchByTermFilter(List<Map<String, String>> termFilters, boolean isNotGuestUser, String types, Pageable page) {
        Map<String, Object> query = buildQuery(termFilters);
        return search(query, isNotGuestUser, types, page);
    }

    private Page<SearchResult> search(Map<String, Object> query, boolean checkAccess, String types, Pageable page) {
        URI uri = UriComponentsBuilder
                .fromUriString(searchServiceURL)
                .queryParam("type", types)
                .queryParam("sortOrder", "desc")
                .queryParam("sortType", "date")
                .queryParam("accessOnly", checkAccess)
                .queryParam("page", page.getPageNumber())
                .queryParam("size", page.getPageSize()).build().toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.put(Constants.APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));

        logger.debug("Search query: {}", query);

        RequestEntity<Map<String, Object>> requestEntity = new RequestEntity<>(query, httpHeaders, HttpMethod.POST, uri);
        try {
            ResponseEntity<PagedResource<SearchResult>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<PagedResource<SearchResult>>() {
            });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            logger.error("Http client/server exception for getting content metadata. Message: {}", e.getMessage());
        } catch (ResourceAccessException rae) { // This will catch a timeout issue
            if (rae.getCause() != null && rae.getCause().getClass().equals((SocketTimeoutException.class))) {
                logger.error("Timed out while searching content.", rae);
            }
            logger.error("Http client/server timeout while calling search service. Message: {}", rae.getMessage());
        }
        logger.error("Unable to perform search with the given details.");
        return Page.empty();
    }

    private Map<String, Object> buildQuery(List<Map<String, String>> termFilters) {
        Map<String, List<Map<String, String>>> filters = new HashMap<>();
        filters.put("termFilters", termFilters);

        Map<String, Object> query = new HashMap<>();
        query.put("filters", filters);

        return query;
    }

}
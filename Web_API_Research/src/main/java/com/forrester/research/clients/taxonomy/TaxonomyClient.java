package com.forrester.research.clients.taxonomy;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.clients.taxonomy.response.TaxonomyTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Component
@RefreshScope
public class TaxonomyClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${forr.taxonomy.details.url}")
    private String taxonomyTagsDetailsURL;

    @Value("${forr.taxonomy.api.key}")
    private String apiKey;

    @LogThis
    public TaxonomyTags getTags(String contentId) {
        URI uri = UriComponentsBuilder.fromUriString(taxonomyTagsDetailsURL).buildAndExpand(Collections.singletonMap("contentId", contentId)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(Constants.APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<TaxonomyTags> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<TaxonomyTags> response = restTemplate.exchange(requestEntity, TaxonomyTags.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                TaxonomyTags taxonomyTags = response.getBody();
                if (taxonomyTags != null) {
                    return taxonomyTags;
                }
            }
        }

        catch (Exception ste) { // This will catch a timeout issue
            LOGGER.error("Http client/server timeout while getting user info. Message: {}", ste.getMessage());
        }

        return new TaxonomyTags();
    }
}
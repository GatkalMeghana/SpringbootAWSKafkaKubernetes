package com.forrester.index.clients.analyst;

import com.forrester.index.clients.analyst.response.AnalystResponse;
import com.forrester.index.clients.analyst.response.MultiAnalystResponse;
import com.forrester.index.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

import static com.forrester.index.utils.Constants.APIKEY_HEADER_TOKEN;

@Service
@RefreshScope
public class AnalystClient {
    private static final Logger logger = LoggerFactory.getLogger(AnalystClient.class);
    public static final String SKIP = "skip";

    @Value("${analyst.service.base.url}")
    private String analystServicebaseUrl;
    @Value("${analyst.service.single.analyst.endpoint}")
    private String singleAnalystEndpoint;
    @Value("${analyst.service.all.analysts.endpoint}")
    private String allAnalystsEndpoint;
    @Value("${analyst.service.api.key}")
    private String analystApiKey;

    @Autowired
    private WebClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    public Optional<AnalystResponse> getAnalystById(String analystId) throws ServiceException {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("id", analystId);
        URI uri = UriComponentsBuilder.fromUriString(analystServicebaseUrl + singleAnalystEndpoint).buildAndExpand(pathVariables).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(analystApiKey));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            return retryTemplate.execute(arg -> {
                ResponseEntity<AnalystResponse> response = restTemplate.exchange(requestEntity, AnalystResponse.class);
                if (null != response && response.getStatusCode().is2xxSuccessful()) {
                    return Optional.ofNullable(response.getBody());
                }
                return Optional.empty();
            });
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                throw new ServiceException("Failed to authorize the request while retrieving analyst", exception);
            }
            logger.error("Http client/server exception while getting analyst for id: {}, message : {}", analystId, exception.getMessage());
        }
        return Optional.empty();
    }

    public List<MultiAnalystResponse> getAllAnalysts(long skip, boolean active) throws ServiceException {

        URI uri = UriComponentsBuilder.fromUriString(analystServicebaseUrl + allAnalystsEndpoint)
                .queryParam("isActive", active)
                .queryParam(SKIP, skip)
                .buildAndExpand().toUri();
        try {
            return webClient
                    .get()
                    .uri(uri)
                    .header(APIKEY_HEADER_TOKEN, analystApiKey)
                    .exchangeToMono(x -> {
                        if (x.statusCode().is2xxSuccessful()) {
                            return x.bodyToMono(new ParameterizedTypeReference<List<MultiAnalystResponse>>() {
                            });
                        } else {
                            return Mono.empty();
                        }
                    }).block();
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                throw new ServiceException("Failed to authorize the request while retrieving analysts", exception);
            }
            logger.error("Http client/server exception while getting all analysts, exception message {}", exception.getMessage());
        }
        return Collections.emptyList();
    }

}

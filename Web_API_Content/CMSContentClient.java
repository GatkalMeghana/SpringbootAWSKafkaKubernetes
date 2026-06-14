package com.forrester.content.clients.cms;

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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.forrester.content.clients.analyst.response.CMSAnalystResponse;
import com.forrester.content.clients.contentful.utils.LogThis;
import com.forrester.content.dto.ResearchContentMetadata;
import com.forrester.content.dto.SurveyContentMetadata;
import com.forrester.content.dto.WebinarContentMetadata;
import com.forrester.content.exception.DataNotFoundException;
import com.forrester.content.exception.ServiceException;

@Component
@RefreshScope
public class CMSContentClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CMSContentClient.class);
    public static final String APIKEY_HEADER_TOKEN = "apiKey";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${forr.cms.research.content.url}")
    private String cmsResearchURL;

    @Value("${forr.cms.webinar.content.url}")
    private String cmsWebinarURL;

    @Value("${forr.cms.survey.content.url}")
    private String cmsSurveyURL;
    
    @Value("${forr.cms.analyst.bio.content.url}")
    private String cmsAnalystBioURL;

    @Value("${forr.services.api.key}")
    private String apiKey;

    public Object getCMSResearchByID(String id) throws DataNotFoundException {
        URI uri = UriComponentsBuilder.fromUriString(cmsResearchURL).buildAndExpand(Collections.singletonMap("id", id)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(requestEntity, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find the research content with the given details");
            }
            LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
        }
        return null;
    }

    public Object getCMSWebinarByID(String id) throws DataNotFoundException {
        URI uri = UriComponentsBuilder.fromUriString(cmsWebinarURL).buildAndExpand(Collections.singletonMap("id", id)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(requestEntity, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find the webinar content with the given details");
            }
            LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
        }
        return null;
    }

    public Object getCMSSurveyByID(String id) throws DataNotFoundException {
        URI uri = UriComponentsBuilder.fromUriString(cmsSurveyURL).buildAndExpand(Collections.singletonMap("id", id)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(requestEntity, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find the survey content with the given details");
            }
            LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
        }
        return null;
    }

    public ResearchContentMetadata getCMSResearchContentMetadataByID(String id) throws DataNotFoundException {
        URI uri = UriComponentsBuilder.fromUriString(cmsResearchURL).buildAndExpand(Collections.singletonMap("id", id)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<ResearchContentMetadata> response = restTemplate.exchange(requestEntity, ResearchContentMetadata.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find the research content metadata with the given details: " + e);
            }
            LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
        }
        return null;
    }

    public WebinarContentMetadata getCMSWebinarContentMetadataByID(String id) throws DataNotFoundException {
        URI uri = UriComponentsBuilder.fromUriString(cmsWebinarURL).buildAndExpand(Collections.singletonMap("id", id)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<WebinarContentMetadata> response = restTemplate.exchange(requestEntity, WebinarContentMetadata.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find the webinar content metadata with the given details. Id: " + id);
            }
            LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
        }
        return null;
    }

    public SurveyContentMetadata getCMSSurveyContentMetadataByID(String id) throws DataNotFoundException {
        URI uri = UriComponentsBuilder.fromUriString(cmsSurveyURL).buildAndExpand(Collections.singletonMap("id", id)).toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
        RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
        try {
            ResponseEntity<SurveyContentMetadata> response = restTemplate.exchange(requestEntity, SurveyContentMetadata.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
                throw new DataNotFoundException("Unable to find the survey content metadata with the given details. Id: " + id);
            }
            LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
        }
        return null;
    }

	/**
	 * This method get Analyst bio details from CMS Content for given bio Id.
	 * 
	 * @param id
	 * @return CMSAnalystResponse
	 * @throws DataNotFoundException
	 * @throws ServiceException 
	 */
	@LogThis
	public CMSAnalystResponse getCMSAnalystBioByID(String id) throws DataNotFoundException, ServiceException {
		URI uri = UriComponentsBuilder.fromUriString(cmsAnalystBioURL)
				.buildAndExpand(Collections.singletonMap("id", id)).toUri();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.put(APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));
		RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
		try {
			ResponseEntity<CMSAnalystResponse> response = restTemplate.exchange(requestEntity,
					CMSAnalystResponse.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
				throw new DataNotFoundException("Unable to find the analyst bio content with the given details");
			}
			LOGGER.error("Http client/server exception for the request: {} and entryID: {}", requestEntity, id, e);
			throw new ServiceException("Service exception while getting content from cms contentful");
		} 
		return new CMSAnalystResponse();
	}

}

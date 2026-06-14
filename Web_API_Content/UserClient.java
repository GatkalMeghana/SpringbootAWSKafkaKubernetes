package com.forrester.content.clients.user;

import java.net.URI;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.forrester.content.clients.contentful.utils.LogThis;
import com.forrester.content.clients.user.response.UserProfileResponse;

@Component
@RefreshScope
public class UserClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserClient.class);
	public static final String APIKEY_HEADER = "apiKey";
	private static final String BEARER = "Bearer";

	@Value("${forr.user.service.profile.url}")
	private String userProfileURL;

	@Value("${forr.services.api.key}")
	private String apiKey;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * @param idToken
	 * @return UserProfileResponse
	 */
	@LogThis
	public UserProfileResponse getUserProfile(String idToken) {
		if(StringUtils.isNotEmpty(idToken)){
			URI uri = UriComponentsBuilder.fromUriString(userProfileURL).build().toUri();
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.put(APIKEY_HEADER, Collections.singletonList(apiKey));
			httpHeaders.setBearerAuth(idToken.substring(BEARER.length()).trim());
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);

			RequestEntity<String> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uri);
			try {
				ResponseEntity<UserProfileResponse> response = restTemplate.exchange(requestEntity,
						UserProfileResponse.class);
				if (response.getStatusCode().is2xxSuccessful()) {
					return response.getBody();
				}
			} catch (Exception e) {
				LOGGER.error("Http client/server exception for the user service request: {} and idToken: {}. Message: {}",
						requestEntity, idToken, e.getMessage());
			}
		}

		return new UserProfileResponse();
	}
}

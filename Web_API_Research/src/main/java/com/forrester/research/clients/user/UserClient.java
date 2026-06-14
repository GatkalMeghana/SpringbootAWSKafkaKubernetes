package com.forrester.research.clients.user;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.clients.user.response.UserInfo;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Component
@RefreshScope
public class UserClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserClient.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${forr.userservice.details.url}")
	private String userServiceDetailsURL;

	@Value("${forr.userservice.api.key}")
	private String apiKey;

	@LogThis
	public UserInfo getUserDetails(String idToken, String userEmail) throws AuthorizationException, DataNotFoundException {
		String userServiceUrl = StringUtils.isNotBlank(userEmail) ? userServiceDetailsURL + "/profile" : userServiceDetailsURL;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(userServiceUrl);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.put(Constants.APIKEY_HEADER_TOKEN, Collections.singletonList(apiKey));

		if (StringUtils.isNotBlank(userEmail)) {
			builder.queryParam("userEmail",userEmail);
		} else {
			httpHeaders.setBearerAuth(idToken.substring("Bearer".length()).trim());
		}

		RequestEntity<UserInfo> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, builder.build().toUri());
		try {
			ResponseEntity<UserInfo> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<UserInfo>() {
			});
			if (response.getStatusCode().is2xxSuccessful()) {
				UserInfo userInfo = response.getBody();
				if (userInfo != null) {
					return userInfo;
				}
			}
		}
		catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
				LOGGER.error("Authorization failed for the user API", e);
			}
			if (e.getStatusCode().equals((HttpStatus.NOT_FOUND))) {
				LOGGER.error(String.format("Unable to find user info with the given idToken: %s", idToken), e);
			}
			LOGGER.error("Http client/server exception for the request: {} and idToken: {}. Message: {}", requestEntity, idToken, e.getMessage());
		} catch (ResourceAccessException ste) { // This will catch a timeout issue
			LOGGER.error("Http client/server timeout while getting user info. Message: {}", ste.getMessage());
		}
		return null;
	}
}
package com.forrester.research.clients.contentful;

import com.contentful.java.cda.*;
import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author sgopal
 *         <p>
 *         Client to call the Contentful service
 */
@Component
@RefreshScope
public class ContentfulClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentfulClient.class);

	@Value("${forr.contentful.space.id}")
	private String spaceId;

	@Value("${forr.contentful.environment.id}")
	private String environmentId;

	@Value("${forr.contentful.preview.enabled}")
	private boolean isPreview;

	@Value("#{'${forr.contentful.preview.enabled}'.equalsIgnoreCase('true') ? '${forr.contentful.preview.token}' : '${forr.contentful"
			+ ".access.token}'}")
	private String accessToken;

	@Value("${forr.contentful.include.depth}")
	private int include;

	private CDAClient cdaClient;

	@PostConstruct
	public void init() {
		CDAClient.Builder builder = CDAClient.builder().setToken(accessToken).setEnvironment(environmentId)
				.setSpace(spaceId);
		if (isPreview) {
			builder.preview();
		}
		cdaClient = builder.build();
	}

	/**
	 * Get details for the given filter from contentful
	 *
	 * @param modelClass
	 * @param filter
	 * @param filterValue
	 * @return
	 * @throws DataNotFoundException
	 * @throws ServiceException
	 */
	@LogThis
	public Object getContentDetailsByField(Class<?> modelClass, String filter, String filterValue)
			throws DataNotFoundException, ServiceException {
		return callContentDetailsByField(modelClass, filter, filterValue);
	}

	public Object callContentDetailsByField(Class<?> modelClass, String filter, String filterValue) throws DataNotFoundException, ServiceException {
		try {
			TransformQuery query = cdaClient.observeAndTransform(modelClass).include(include).where("fields."+filter, filterValue);
			List<Object> allRecords = (List<Object>) query.all().blockingSingle();
			if (null != allRecords && !allRecords.isEmpty()) {
				return allRecords.get(0);
			}
			throw new DataNotFoundException(String.format("Unable to find the content of type: %s. Please provide a valid %s", modelClass.getSimpleName() ,filter));
		} catch (CDAResourceNotFoundException nfe) {
			throw new DataNotFoundException(String.format("Unable to find the content of type: %s with %s value", filter,
			filterValue));
		} catch (CDAHttpException e) {
			LOGGER.error(e.toString());
			throw new ServiceException(Constants.SERVICE_EXCEPTION_FROM_CONTENTFUL);
		}
	}

	@LogThis
	public List<Object> getEntryDetailsByEntryIds(Class<?> modelClass, String[] entryIds) throws ServiceException {
		LOGGER.info("Get info from contentful for {} ids : {}", entryIds.length, String.join(",", entryIds));
		return callEntryDetailsByEntryIds(modelClass, entryIds);
	}

	private List<Object> callEntryDetailsByEntryIds(Class<?> modelClass, String[] entryIds) throws ServiceException {
		try {
			TransformQuery query = cdaClient.observeAndTransform(modelClass).include(include);
			if (entryIds.length != 0) {
				query.where("fields.contentId", QueryOperation.HasOneOf, entryIds);
			}
			return (List<Object>) query.all().blockingSingle();
		} catch (CDAHttpException e) {
			LOGGER.debug(e.toString());
			throw new ServiceException(Constants.SERVICE_EXCEPTION_FROM_CONTENTFUL);
		}
	}
}

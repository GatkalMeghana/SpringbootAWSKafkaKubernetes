package com.forrester.research.service;

import java.util.Date;
import java.util.List;

import org.springframework.util.MultiValueMap;

import com.forrester.research.clients.contentful.response.GraphQLResponse;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.exception.NotAcceptableException;
import com.forrester.research.exception.ServiceException;

public interface ResearchService {

	Object getResearchById(String id, MultiValueMap<String, String> headers, boolean formattedContent)
			throws ServiceException, NotAcceptableException, DataNotFoundException, AuthorizationException;

	void postResearchForBIReportingById(String id) throws ServiceException, NotAcceptableException, DataNotFoundException;
	
	GraphQLResponse getResearchIds(String containerType, long skip, long limit, Date publishedDate)
			throws ServiceException, DataNotFoundException;
	
	List<Object> getResearchByIds(String[] ids, MultiValueMap<String, String> headers, boolean formattedContent, boolean metaDataOnly) throws DataNotFoundException,	AuthorizationException, ServiceException;

	String getResearchPdfById(String upperCase, MultiValueMap<String, String> headers) throws AuthorizationException, DataNotFoundException, ServiceException;

}

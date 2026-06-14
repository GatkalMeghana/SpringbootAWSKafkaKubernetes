package com.forrester.research.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.response.GraphQLResponse;
import com.forrester.research.exception.AuthorizationException;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.service.ResearchService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Responsibility of this class is to return the content for the specific id
 * provided in the request
 *
 * @author dsayyaparaju
 *
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1")
public class ResearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchController.class);

	@Autowired
	private ResearchService researchService;

	/**
	 * Responsibility of this method is to return the content for the requested
	 * content id and the given type
	 *
	 * @param id String
	 * @return Object
	 */
	@ApiOperation(value = "Retrieve the research record for a given id token and contentful id.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "", response = Object.class),
			@ApiResponse(code = 400, message = "Request not processed as Authorization header value is missing"),
            @ApiResponse(code = 401, message = "Authentication failed for given idToken"),
			@ApiResponse(code = 404, message = "Research record not found for the given id"),
			@ApiResponse(code = 500, message = "Generic exception. Please notify the web team.") })
	@GetMapping(value = "/research/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getContentById(@PathVariable("id") String id,
                              @RequestHeader MultiValueMap<String, String> headers,
                              @RequestParam(required = false, defaultValue = "false") boolean formattedContent,
                              @RequestParam(required = false) String appsource) {
		validateContentId(id);
		try {
			if(StringUtils.isNotBlank(appsource)) {
				headers.add(Constants.APPSOURCE, appsource);
			}
			return researchService.getResearchById(id.toUpperCase(), headers, formattedContent);
		} catch (DataNotFoundException unfe) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.RESEARCH_NOT_FOUND_FOR_ID);
		} catch (Exception se) {
			LOGGER.error("Error while getting content by id: {}. Exception:", id, se);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
		}
	}

	private void validateContentId(String id) {
		if(StringUtils.isBlank(id) || !StringUtils.startsWith(id.toUpperCase(), Constants.RES_PREFIX)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Constants.ID_IS_INVALID);
		}
	}


	/**
	 * Responsibility of this method is to send the content for the requested
	 * content id to kafka.
	 * ENDPOINT NOT USED AS OF NOW. LISTENER IN USE.
	 *
	 * @param id String
	 * @return Object
	 */
	@ApiOperation(value = "Retrieve the research record for a given id and send it to Kafka.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "", response = Object.class),
			@ApiResponse(code = 400, message = "Request not processed as Authorization header value is missing"),
			@ApiResponse(code = 404, message = "Research record not found for the given id"),
			@ApiResponse(code = 500, message = "Generic exception. Please notify the web team.") })
	@PostMapping(value = "/research/{id}/publish", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> reportPublishedResearchById(@PathVariable("id") String id) {
		validateContentId(id);
		try {
			researchService.postResearchForBIReportingById(id);
			return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
		} catch (DataNotFoundException unfe) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.RESEARCH_NOT_FOUND_FOR_ID);
		} catch (Exception se) {
			LOGGER.error("Error while getting content by id: {}. Exception:", id, se);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
		}
	}

	/**
	 * Responsibility of this method is to return the research Ids from contentful
	 * using graphQL.
	 *
	 * @param containerType String
	 * @param skip long
	 * @param limit long
	 * @param publishDate Date
	 * @return GraphQLResponse
	 */
	@ApiOperation(value = "Retrieve the research record for a given containerType,skip and limit from contentful using graph ql.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "", response = GraphQLResponse.class),
			@ApiResponse(code = 400, message = "Request not processed as container type value is missing"),
			@ApiResponse(code = 404, message = "Research records not found for the given containerType,skip and limit parameter."),
			@ApiResponse(code = 500, message = "Generic exception. Please notify the web team.") })
	@GetMapping(value = "/research/ids", produces = MediaType.APPLICATION_JSON_VALUE)
	public GraphQLResponse getResearchIds(@RequestParam("containerType") String containerType,
			@RequestParam("skip") long skip,
		  	@RequestParam("limit") long limit,
		  	@RequestParam(required = false, defaultValue = "1900-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  Date publishDate) {
		try {
			return researchService.getResearchIds(containerType, skip, limit, publishDate);
		} catch (Exception se) {
			LOGGER.error("Error while getting Research ids: {containerType}. Exception: {}", containerType, se);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
		}
	}

	/**
	 * Responsibility of this method is to return the content for the requested
	 * content ids
	 *
	 * @param ids String[]
	 * @param headers MultiValueMap from String to String
	 * @param formattedContent boolean
	 * @param metaDataOnly boolean
	 * @return List of Object
	 */
	@ApiOperation(value = "Retrieve all research record for a given id token and content ids.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "", response = Collection.class),
			@ApiResponse(code = 400, message = "Ids length cannot be more than 25. Use multiple calls to retrieve more than 25 records."),
			@ApiResponse(code = 500, message = "Generic exception. Please notify the web team.") })
	@GetMapping(value = "/research/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Object> getContentByIds(@RequestParam(name = "ids") String[] ids,
										@RequestHeader MultiValueMap<String, String> headers,
										@RequestParam(required = false, defaultValue = "false") boolean formattedContent,
										@RequestParam(required = false, defaultValue = "false") boolean metaDataOnly) {
		if (ids.length > 25) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Ids length cannot be more than 25. Use multiple calls to retrieve more than 25 records.");
		}
		try {
			LOGGER.info("The metadata value is {}", metaDataOnly);
			return researchService.getResearchByIds(ids, headers, formattedContent, metaDataOnly);
		} catch (Exception se) {
			LOGGER.error("Error while getting content by ids: {}. Exception:", String.join(",", ids), se);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
		}
	}

	/**
	 * Responsibility of this method is to return the pdf content for the requested
	 * content id
	 *
	 * @param contentId String
	 * @param headers MultiValue from String to String
	 * @return ByteArrayResource
	 */
	@ApiOperation(value = "Retrieve the research record for a given id token and contentful id.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "", response = Object.class),
			@ApiResponse(code = 400, message = "Request not processed as Authorization header value is missing"),
            @ApiResponse(code = 401, message = "Authentication failed for given idToken"),
			@ApiResponse(code = 404, message = "Research record not found for the given id"),
			@ApiResponse(code = 500, message = "Generic exception. Please notify the web team.") })
	@GetMapping(value = "/research/pdf/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getpdfById(@PathVariable("id") String contentId,
                              @RequestHeader MultiValueMap<String, String> headers) {
		validateContentId(contentId);
		try {
			return researchService.getResearchPdfById(contentId.toUpperCase(), headers);
		}catch(AuthorizationException ate) {
			LOGGER.error("AuthorizationException while getting content by id: {}", contentId, ate);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ate.getMessage(), ate);
		}
		catch(DataNotFoundException dnfe) {
			LOGGER.error("DataNotFoundException while getting content by id: {}", contentId, dnfe);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, dnfe.getMessage(), dnfe);
		}catch (Exception se) {
			LOGGER.error("Exception while getting content by id: {}", contentId, se);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
		}
	}
}
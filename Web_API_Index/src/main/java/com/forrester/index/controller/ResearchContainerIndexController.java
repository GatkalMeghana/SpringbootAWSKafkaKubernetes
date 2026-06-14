package com.forrester.index.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.forrester.index.elasticsearch.data.ESResearchContainer;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.service.ResearchContainerIndexService;
import com.forrester.index.utils.IndexAliasesBuilder;
import com.forrester.index.view.BulkUploadView;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1/api/researchcontainer")
public class ResearchContainerIndexController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchContainerIndexController.class);

    @Autowired
    private ResearchContainerIndexService researchContainerIndexService;
    
    @Autowired
    private IndexAliasesBuilder indexAliasesBuilder;

	/**
	 * This method queries and loads the received contentId from Research Service
	 * (ContentType: Research container Document) and then pushes it into Elasticsearch
	 * to be indexed.
	 * 
	 * @param entryId
	 * @return ESResearchContainer
	 */
    @ApiResponses(value = {@ApiResponse(code = 201, message = "", response = ESResearchContainer.class),
            @ApiResponse(code = 404, message = "Data not found for the provided entryId"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the web team.")})
    @PostMapping(value = "/{id}/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ESResearchContainer indexById(@PathVariable("id") String entryId) {
        try {
            return researchContainerIndexService.indexById(entryId);
        } catch (DataNotFoundException nfe) {
            LOGGER.error("Exception while retrieving data for the entryId: {}", entryId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, nfe.getMessage(), nfe);
        } catch (Exception se) {
            LOGGER.error("Exception while indexing entryId: {}", entryId, se);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
        }
    }

	/**
	 * This method bulk upload contentful graph ql document into Elasticsearch.
	 * 
	 * @param chunkSize
	 * @return BulkUploadView
	 */
    @ApiOperation(value = "Bulk upload Contentful Graph QL document into Elasticsearch.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = BulkUploadView.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the web team.")})
    @PutMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    public BulkUploadView indexAll(@RequestParam(name = "chunkSize", required = false, defaultValue = "50") int chunkSize) {
        try {
            return researchContainerIndexService.indexAll(chunkSize);
        } catch (Exception e) {
            LOGGER.error("Exception while bulk indexing", e);
            indexAliasesBuilder.deleteIndex(ESResearchContainer.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

	/**
	 * This method deletes the received contentId (ContentType: Research container Document)
	 * and de-indexes the entry from ElasticSearch.
	 * 
	 * @param entryId
	 */
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Entry has been deleted"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the web team.")})
    @DeleteMapping(value = "/{id}/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deIndexById(@PathVariable("id") String entryId) {
        try {
            if(entryId.isEmpty()){
                throw new Exception("documentId or entryId is required to perform this operation.");
            }
        	researchContainerIndexService.deIndexById(entryId);
        } catch (Exception se) {
            LOGGER.error("Exception while de-indexing entryId: {}", entryId, se);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
        }
    }
}

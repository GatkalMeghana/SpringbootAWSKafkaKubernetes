package com.forrester.index.controller;


import com.forrester.index.elasticsearch.data.ESAnalyst;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.InvalidContentException;
import com.forrester.index.service.AnalystIndexService;
import com.forrester.index.utils.IndexAliasesBuilder;
import com.forrester.index.view.BulkUploadView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1/api/analyst")
public class AnalystIndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalystIndexController.class);

    @Autowired
    private AnalystIndexService analystIndexService;

    @Autowired
    private IndexAliasesBuilder indexAliasesBuilder;

    /**
     * This method queries and loads the received contentId from Analyst Service (ContentType: AnalystBio only) and
     * then pushes it into Elasticsearch to be indexed.
     * If analyst is Incative, Deindex analyst from Elasticsearch
     */
    @ApiResponses(value = {@ApiResponse(code = 201, message = "", response = ESAnalyst.class),
            @ApiResponse(code = 204, message = "Analyst has been deleted"),
            @ApiResponse(code = 404, message = "Data not found for the provided analystId"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the web team.")})
    @PostMapping(value = "/{id}/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ESAnalyst> indexById(@PathVariable("id") String analystId) {
        try {
            ESAnalyst esAnalyst = analystIndexService.indexById(analystId);
            return new ResponseEntity<>(esAnalyst, HttpStatus.CREATED);
        } catch (DataNotFoundException nfe) {
            LOGGER.error("Exception while retrieving data for the entryId: {}, exception: {}", analystId, nfe.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidContentException invalidContentException) {
            LOGGER.error("Analyst with id {} is invalid and is removed from opensearch", analystId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception se) {
            LOGGER.error("Exception while indexing entryId: {}", analystId, se);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
        }
    }

    @ApiOperation(value = "Bulk upload Contentful Analyst Bio contents into Elasticsearch.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "", response = BulkUploadView.class),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the web team.")})
    @PutMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    public BulkUploadView indexAll(@RequestParam(name = "chunkSize", required = false, defaultValue = "50") int chunkSize) {
        try {
            return analystIndexService.indexAll(chunkSize);
        } catch (Exception e) {
            LOGGER.error("Exception while bulk indexing", e);
            indexAliasesBuilder.deleteIndex(ESAnalyst.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    /**
     * This method deletes the received contentId (ContentType: Analyst Bio only) and de-indexes
     * the entry from ElasticSearch.
     */
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Entry has been deleted"),
            @ApiResponse(code = 500, message = "Generic exception. Please notify the web team.")})
    @DeleteMapping(value = "/{id}/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deIndexById(@PathVariable("id") String entryId) {
        try {
            analystIndexService.deIndexById(entryId);
        } catch (Exception se) {
            LOGGER.error("Exception while de-indexing entryId: {}", entryId, se);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage(), se);
        }
    }
}

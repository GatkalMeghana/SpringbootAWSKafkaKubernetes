package com.forrester.index.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forrester.index.elasticsearch.data.ESResearchContainer;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.view.BulkUploadView;

public interface ResearchContainerIndexService {

    ESResearchContainer indexById(String entryId) throws ServiceException, DataNotFoundException, JsonProcessingException;

    BulkUploadView indexAll(int chunkSize) throws ServiceException, DataNotFoundException;

    void deIndexById(String entryId) throws ServiceException;
}

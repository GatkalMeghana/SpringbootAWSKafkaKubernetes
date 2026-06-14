package com.forrester.index.service;

import com.forrester.index.elasticsearch.data.ESAnalyst;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.InvalidContentException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.view.BulkUploadView;

public interface AnalystIndexService {

    ESAnalyst indexById(String entryId) throws ServiceException, DataNotFoundException, InvalidContentException;

    BulkUploadView indexAll(int chunkSize) throws ServiceException;

    void deIndexById(String entryId) throws ServiceException;
}

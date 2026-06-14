package com.forrester.index.scheduler;


import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.AutocompleteIndexService;
import com.forrester.index.utils.CustomRequestScopeAttr;
import com.forrester.index.view.BulkUploadView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class IndexScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexScheduler.class);

    @Autowired
    private AutocompleteIndexService autocompleteIndexService;

    @Scheduled(cron = "0 15 22 ? * MON-FRI")
    private void indexAllAutoCompleteData() throws ServiceException, DataNotFoundException {
        LOGGER.info("Starting AutoComplete bulk indexing at {}", LocalDateTime.now());

        try {
            RequestContextHolder.setRequestAttributes(new CustomRequestScopeAttr());
            BulkUploadView indexResult = this.autocompleteIndexService.indexAll();
            LOGGER.info("AutoComplete bulk indexing completed at {} , result : {}", LocalDateTime.now(), indexResult);
        } catch (Exception e) {
            LOGGER.error("Exception while AutoComplete bulk indexing : {}", e.getMessage());
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}

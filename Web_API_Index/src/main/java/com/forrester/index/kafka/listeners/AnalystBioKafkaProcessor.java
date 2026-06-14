package com.forrester.index.kafka.listeners;

import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.InvalidContentException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.AnalystIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AnalystBioKafkaProcessor implements Consumer<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalystBioKafkaProcessor.class);

    @Autowired
    private AnalystIndexService analystIndexService;

    @Override
    public void accept(String analystId) {
        try {
            analystIndexService.indexById(analystId);
        } catch (ServiceException | DataNotFoundException | InvalidContentException e) {
            LOGGER.debug("Exception while indexing analyst with ID : {}, possible cause:{} and message {}", analystId,
                    e.getCause(), e.getMessage());
        }

    }
}

package com.forrester.research.kafka.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.forrester.research.Constants;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.exception.DataNotFoundException;
import com.forrester.research.exception.NotAcceptableException;
import com.forrester.research.exception.ServiceException;
import com.forrester.research.service.ResearchService;

@Service
public class KafkaConsumerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Autowired
    ResearchService researchService;

    @LogThis
    @KafkaListener(topics = "${forr.kafka.content.update.topic}", id = "${forr.kafka.content.update.topic}" + "_id", groupId = "web_research_group")
    private void consumeMessage(String contentId) {
        try {
            validateContentId(contentId);
            researchService.postResearchForBIReportingById(contentId);
            LOGGER.info("Consumed message: Research Data posted to Kafka for contentId: {}", contentId);
        } catch (ServiceException | NotAcceptableException | DataNotFoundException e) {
            LOGGER.error("Error reporting research document with Id: {} to Kafka: {}", contentId, e.getMessage());
        }
    }

    private void validateContentId(String id) throws NotAcceptableException {
        if(StringUtils.isBlank(id) || !StringUtils.startsWith(id, Constants.RES_PREFIX)) {
            throw new NotAcceptableException(Constants.ID_IS_INVALID);
        }
    }

}

package com.forrester.index.kafka.listeners;

import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.ResearchContainerIndexService;
@Component
public class ResearchKafkaProcessor implements Consumer<String> {
	
	@Autowired
	private ResearchContainerIndexService researchContainerIndexService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ResearchKafkaProcessor.class);

	@Override
	public void accept(String docID) {
		try {
			researchContainerIndexService.indexById(docID);
		} catch (ServiceException | DataNotFoundException | JsonProcessingException e) {
			LOGGER.debug("Exception while indexing research document with ID : {}, possible cause:", docID,
					e.getCause());
		}

	}

}

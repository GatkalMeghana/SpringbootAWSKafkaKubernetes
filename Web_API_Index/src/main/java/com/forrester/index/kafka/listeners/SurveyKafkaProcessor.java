package com.forrester.index.kafka.listeners;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.SurveyIndexService;
@Component
public class SurveyKafkaProcessor implements Consumer<String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyKafkaProcessor.class);
	@Autowired
	private SurveyIndexService surveyIndexService;

	@Override
	public void accept(String docID) {
		try {
			surveyIndexService.indexById(docID);
		} catch (ServiceException | DataNotFoundException e) {
			LOGGER.debug("Exception while indexing research document with ID : {}, possible cause:", docID,
					e.getCause());
		}

	}

}

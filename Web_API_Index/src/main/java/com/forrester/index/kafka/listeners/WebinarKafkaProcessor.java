package com.forrester.index.kafka.listeners;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.WebinarIndexService;
@Component
public class WebinarKafkaProcessor implements Consumer<String>{

	private static final Logger LOGGER = LoggerFactory.getLogger(WebinarKafkaProcessor.class);
	
	@Autowired
	private WebinarIndexService webinarIndexService;
	
	@Override
	public void accept(String docID) {
		try {
			webinarIndexService.indexById(docID);
		} catch (ServiceException | DataNotFoundException e) {
			LOGGER.debug("Exception while indexing research document with ID : {}, possible cause:", docID,
					e.getCause());
		}
		
	}

}

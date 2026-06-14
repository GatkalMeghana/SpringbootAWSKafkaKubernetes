package com.forrester.index.kafka.listeners;

import com.forrester.index.utils.ContentType;
import com.forrester.index.utils.CustomSpringSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * This component listner message from Kafka and process content.
 * 
 * @author meghanag
 *
 */
@Component
public class KafkaConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
	
	@Autowired
	private AnalystBioKafkaProcessor analystKafkaProcessor;

	@Autowired
	private PlaybookKafkaProcessor playbookKafkaProcessor;

	@Autowired
	private ResearchKafkaProcessor researchKafkaProcessor;

	@Autowired
	private ForumKafkaProcessor forumKafkaProcessor;

	@Autowired
	private SurveyKafkaProcessor surveyKafkaProcessor;

	@Autowired
	private WebinarKafkaProcessor webinarKafkaProcessor;

	@Autowired
	private WorkshopKafkaProcessor workshopKafkaProcessor;

	/**
	 * This method listen kafka message, extract contentype and docid from message
	 *  and process document for indexbyid as per content type.
	 * 
	 * @param message
	 */
	//Use custom session because Kafka listener is not belong to web-context and @RequestScope IndexNameProvider is not available
	@CustomSpringSession
	@KafkaListener(topics = "${kafka.taxonomy.topic}", groupId = "${kafka.consumer.groupid}")
	public void messageListener(String message) {
		LOGGER.info("Consumed message : {}" ,message);
		ContentType contentType = ContentType.fromMessage(message);
		if (contentType == ContentType.RESEARCH_DOCUMENT) {
			researchKafkaProcessor.accept(contentType.getMetaID());
		} else if (contentType == ContentType.WEBINARS) {
			webinarKafkaProcessor.accept(contentType.getMetaID());
		} else if (contentType == ContentType.FORUMS) {
			forumKafkaProcessor.accept(contentType.getMetaID());
		} else if (contentType == ContentType.WORKSHOPS) {
			workshopKafkaProcessor.accept(contentType.getMetaID());
		} else if (contentType == ContentType.PLAYBOOKS) {
			playbookKafkaProcessor.accept(contentType.getMetaID());
		} else if (contentType == ContentType.SURVEYS) {
			surveyKafkaProcessor.accept(contentType.getMetaID());
		} else if (contentType == ContentType.ANALYST) {
			analystKafkaProcessor.accept(contentType.getMetaID());
		} else {
			LOGGER.info("Content Id {} is of type {}", contentType.getMetaID(), contentType);
		}
	}
}

package com.forrester.index.activemq.listeners;

import com.forrester.index.utils.ContentType;
import com.forrester.index.utils.CustomSpringSession;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import static com.forrester.index.utils.Constants.ENDICA_ID;

@Component
public class CacheInvalidateConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheInvalidateConsumer.class);

    @Autowired
    private WebinarProcessor webinarProcessor;

    @Autowired
    private ForumProcessor forumProcessor;

    @Autowired
    private WorkshopProcessor workshopProcessor;

    @Autowired
    private PlaybookProcessor playbookProcessor;

    @Autowired
    private SurveyProcessor surveyProcessor;

    @Autowired
    private AnalystBioProcessor analystBioProcessor;

    //Use custom session because JMS listener is not belong to web-context and @RequestScope IndexNameProvider is not available
    @CustomSpringSession
    @JmsListener(destination = "CacheInvalidate", containerFactory = "listenerFactory")
    public void receiveMessageFromTopic(ActiveMQMessage message) {
        try {
            if (message instanceof ActiveMQObjectMessage) {
                ObjectInputStream objectReader = new ObjectInputStream(new ByteArrayInputStream(message.getContent().getData()));
                Map<String, Object> messageObject = (HashMap<String, Object>) objectReader.readObject();
                if (messageObject.get(ENDICA_ID) != null) {
                    String endecaID = messageObject.get(ENDICA_ID).toString();
                    LOGGER.info("Message received : {} ", endecaID);
                    ContentType contentType = ContentType.fromMessage(endecaID);
                    if (contentType == ContentType.WEBINARS) {
                        webinarProcessor.accept(contentType.getMetaID());
                    } else if (contentType == ContentType.FORUMS) {
                        forumProcessor.accept(contentType.getMetaID());
                    } else if (contentType == ContentType.WORKSHOPS) {
                        workshopProcessor.accept(contentType.getMetaID());
                    } else if (contentType == ContentType.PLAYBOOKS) {
                        playbookProcessor.accept(contentType.getMetaID());
                    } else if (contentType == ContentType.SURVEYS) {
                        surveyProcessor.accept(contentType.getMetaID());
                    } else if (contentType == ContentType.ANALYST) {
                        analystBioProcessor.accept(contentType.getMetaID());
                    } else {
                        LOGGER.info("Content Id {} is of type {}", contentType.getMetaID(), contentType.getType());
                    }
                } else {
                    LOGGER.error("Received message with null/empty endica Id");
                }
            } else {
                LOGGER.info("Message received : {} ", message.getContent().getData());
            }
        } catch (IOException | IllegalArgumentException | ClassNotFoundException e) {
            LOGGER.error("Unable to process MQ message : {}. Please check logs.", message, e);
        }
    }
}

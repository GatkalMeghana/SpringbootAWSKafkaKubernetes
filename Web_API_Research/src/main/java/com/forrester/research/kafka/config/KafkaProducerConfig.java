/**
 *
 */
package com.forrester.research.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.forrester.research.clients.contentful.utils.LogThis;
import com.google.gson.Gson;

/**
 * @author sgopal
 *
 */
@Service
public class KafkaProducerConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerConfig.class);

	@Autowired
	private KafkaTemplate<Object, Object> kafkaTemplate;

	@LogThis
	public void sendMessage(Object message, String topicName) {
		LOGGER.info("Sending message: {} to topic: {}", message, topicName);
		Gson gson = new Gson();
		String jsonString =  gson.toJson(message);
		ListenableFuture<SendResult<Object, Object>> future =
				kafkaTemplate.send(topicName, jsonString);
		future.addCallback(new
								   ListenableFutureCallback<SendResult<Object, Object>>() {

									   @Override public void onSuccess(SendResult<Object, Object> result) {
										   LOGGER.debug("Message [{}] delivered with offset {} to topic {}", jsonString,
												   result.getRecordMetadata().offset(), topicName); }

									   @Override public void onFailure(Throwable ex) {
										   LOGGER.error("Unable to deliver message [{}]. {}", jsonString, ex.getMessage());
									   }
								   });

	}
}

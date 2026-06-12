package com.demo.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
	
	//@KafkaListener(topics="order-topic", groupId="my-new-group")
	public void accept1(String message) {
		System.out.println("Message received1:"+message);
	}
	
	//@KafkaListener(topics="order-topic", groupId="my-new-group")
	public void accept2(RiderLocation location) {
		System.out.println("Message received2:"+location.getName()+ " Message received2:"+location.getLangitude() +" Message received2:"+location.getLongitude());
	}

}

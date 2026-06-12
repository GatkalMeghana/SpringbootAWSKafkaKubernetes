package com.demo.producer;

//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api")
//public class KafkaProducer {
//
//	private final KafkaTemplate<String, RiderLocation> kafkaTemplate;
//
//	public KafkaProducer(KafkaTemplate<String, RiderLocation> kafkaTemplate) {
//		super();
//		this.kafkaTemplate = kafkaTemplate;
//	}
//
//	@PostMapping("/send")
//	public String sendMessgae(@RequestParam String message) {
//		RiderLocation riederLocation = new RiderLocation("rider123", 28.61, 77.23);
//		kafkaTemplate.send("order-topic", riederLocation);
//		return "Message Sent:" + message;
//
//	}
//
//}

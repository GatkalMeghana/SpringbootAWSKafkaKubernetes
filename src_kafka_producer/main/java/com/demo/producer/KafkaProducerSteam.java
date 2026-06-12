package com.demo.producer;

import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerSteam {

	@Bean
	public Supplier<RiderLocation> sendRiderLocation(){
		return () -> {
			RiderLocation location = new RiderLocation("Rider123",12.36, 78.96);
			System.out.println(location.getName());
			return location;
		};
	}

}

package com.forrester.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author vpevunov
 *
 */

@SpringBootApplication
@EnableConfigServer
public class ForrCloudConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForrCloudConfigServiceApplication.class, args);
	}
}

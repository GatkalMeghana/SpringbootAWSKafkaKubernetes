package com.forrester.research;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = "com.forrester.research")
public class ApplicationMain {

    //@Value("${forr.taskexecutor.core.poolsize}")
    private int corePoolSize=5;

    //@Value("${forr.taskexecutor.max.poolsize}")
    private int maxPoolSize=10;
    
    @Value("${forr.researchservice.restTemplate.timeout}")
    private long timeout;
    
	public static void main(String[] args) {
		SpringApplication.run(ApplicationMain.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.setConnectTimeout(Duration.ofSeconds(timeout)).setReadTimeout(Duration.ofSeconds(timeout))
				.build();
	}

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setThreadNamePrefix("task-Thread-");
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }
}

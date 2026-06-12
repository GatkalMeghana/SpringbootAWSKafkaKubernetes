package com.example.loginApp.jwtAuth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.loginApp.interceptor.RequestTimingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private final RequestTimingInterceptor interceptor;

	public WebConfig(RequestTimingInterceptor interceptor) {
		super();
		this.interceptor = interceptor;
	}
	
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor);
	}

}

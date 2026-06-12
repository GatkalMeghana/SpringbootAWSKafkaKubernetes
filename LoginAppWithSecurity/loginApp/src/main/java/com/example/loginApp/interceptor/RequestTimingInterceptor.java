package com.example.loginApp.interceptor;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestTimingInterceptor implements HandlerInterceptor  {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception{
			request.setAttribute("Starttime", System.currentTimeMillis());
				return true;
		
	}
	
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		
		Long startTime = (Long) request.getAttribute("Starttime");
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(duration);
		
	}
}

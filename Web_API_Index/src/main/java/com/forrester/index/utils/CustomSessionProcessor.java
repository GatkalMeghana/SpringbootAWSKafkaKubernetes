package com.forrester.index.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Aspect
@Component
public class CustomSessionProcessor {

    /**
     * Inject custom attributes before executing method that annotated with CustomSpringSession
     * @param joinPoint
     */
    @Before("@annotation(com.forrester.index.utils.CustomSpringSession)")
    public void injectAttributes(JoinPoint joinPoint) {
        RequestContextHolder.setRequestAttributes(new CustomRequestScopeAttr());
    }

    /**
     * Reset custom attributes after executing method that annotated with CustomSpringSession
     * @param joinPoint
     */
    @After("@annotation(com.forrester.index.utils.CustomSpringSession)")
    public void removeAttributes(JoinPoint joinPoint) {
        RequestContextHolder.resetRequestAttributes();
    }
}

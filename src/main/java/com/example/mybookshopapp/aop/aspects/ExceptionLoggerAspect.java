package com.example.mybookshopapp.aop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * @author karl
 */

@Aspect
@Component
public class ExceptionLoggerAspect {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "@annotation(com.example.mybookshopapp.aop.annotation.ExceptionLogging)")
    private void log() {}

    @Before("log()")
    private void logClassName(JoinPoint joinPoint) {
        logger.info("Exception on class " + joinPoint.getTarget().getClass().getSimpleName());
    }

    @AfterReturning(pointcut = "(args(e) || args(e, *)) && log()", argNames = "e")
    private void logExceptionInfo(Exception e) {
        logger.info("Exception " + e.getClass().getSimpleName()
                + " info: " + e.getMessage());
    }
}

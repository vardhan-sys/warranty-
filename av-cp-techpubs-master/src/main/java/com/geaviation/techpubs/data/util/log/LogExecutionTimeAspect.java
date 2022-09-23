package com.geaviation.techpubs.data.util.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Configuration
@ConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class LogExecutionTimeAspect {

    private static final Logger log = LogManager.getLogger(LogExecutionTimeAspect.class);

    @Value("${ENABLE.LOG_EXECUTION.TIME}")
    private boolean enableLogExecutionTime;

    @Value("${ENABLE.LOG_EXECUTION.TIME.WITHARGS}")
    private boolean enableLogExecutionTimeWithArgs;

    @Around("@annotation(LogExecutionTime)")
    @SuppressWarnings("squid:S00112")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (enableLogExecutionTime) {
            final long start = System.currentTimeMillis();

            log.info("Method Invoked " + joinPoint.getSignature());

            final Object proceed = joinPoint.proceed();

            final long executionTime = System.currentTimeMillis() - start;

            log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");

            return proceed;
        } else {
            return joinPoint.proceed();
        }

    }

    @Around("@annotation(LogExecutionTimeWithArgs)")
    @SuppressWarnings("squid:S00112")
    public Object logExecutionTimeWithArgs(ProceedingJoinPoint joinPoint) throws Throwable {
        if (enableLogExecutionTimeWithArgs) {
            final long start = System.currentTimeMillis();

            log.info("Method Invoked " + joinPoint.getSignature() + " Value Passed: ");
            for (Object args : joinPoint.getArgs()) {
                log.info("Args: " + args);
            }

            final Object proceed = joinPoint.proceed();

            final long executionTime = System.currentTimeMillis() - start;

            log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");

            return proceed;
        } else {
            return joinPoint.proceed();
        }
    }
}

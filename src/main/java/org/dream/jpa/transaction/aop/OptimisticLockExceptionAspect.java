package org.dream.jpa.transaction.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dream.jpa.transaction.configuration.OptimisticLockConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@Order(1) // 這個要設 1，必須在 @Transactional 之前發生
public class OptimisticLockExceptionAspect {

    private OptimisticLockConfigurationProperties ptimisticLockConfigurationProperties;
    
    @Autowired
    public void setPtimisticLockConfigurationProperties(
            OptimisticLockConfigurationProperties ptimisticLockConfigurationProperties) {
        this.ptimisticLockConfigurationProperties = ptimisticLockConfigurationProperties;
    }

    @Pointcut("within(org.dream.jpa.transaction.service..*)")
    public void retryPointCut() {}

    @Around("retryPointCut()")
    public Object retry(ProceedingJoinPoint pjp) throws Throwable {
        int retryCount = ptimisticLockConfigurationProperties.getRetryCount();
        for (int i= 0; i < retryCount; i++) {
            try {
                return pjp.proceed();
            }
            catch (OptimisticLockingFailureException ex) {
                log.info("OptimisticLockingFailureException caught, retrying");
                if (i >= retryCount) {
                    throw ex;
                }
            }
        }
        return null;
    }
}

package org.dream.jpa.transaction.aop;

import java.sql.BatchUpdateException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Aspect 會攔截 CannotAcquireLockException
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class DeadLockRetryAspect {

    @Around(value = "@annotation(deadLockRetry)", argNames = "deadLockRetry")
    public Object concurrencyRetry(final ProceedingJoinPoint pjp, final DeadLockRetry deadLockRetry) throws Throwable {
        final Integer retryCount = deadLockRetry.retryCount();
        Integer deadlockCounter = 0;
        Object result = null;
        while (deadlockCounter < retryCount) {
            try {
                // 正常的程式會在這裡執行交易，如果沒問題就 break
                result = pjp.proceed();
                break;
            } 
            catch (final CannotAcquireLockException exception) {
                // 發生無法取得交易所才會進這塊
                deadlockCounter = handleException(exception, deadlockCounter, retryCount);
                log.info("Retried {} time(s)", deadlockCounter);
            }
        }
        return result;
    }

    /**
     * @param exception           可能是 deadlock 的 Exception
     * @param deadlockCounter     發生第幾次了(因為在 while 迴圈內)
     * @param retryCount          總共的重試次數
     * @return                    deadlockCounter
     * @throws Exception          重試了 retryCount 後還是 deadlock 就只好拋出了
     */
    private Integer handleException(final CannotAcquireLockException exception, Integer deadlockCounter, final Integer retryCount) throws Exception {
        if (isDeadlock(exception)) {
            deadlockCounter++;
            log.info("Deadlocked: {} ", exception.getMessage());
            if (deadlockCounter == (retryCount - 1)) {
                throw exception;
            }
        } else {
            throw exception;
        }
        return deadlockCounter;
    }
    
    /**
     * 1. Deadlock 的時候拋出的 Exception 順序是:
     *    - SQLServerException
     *    - LockAcquisitionException
     *    - CannotAcquireLockException
     * 2. 所以要一層層解回去，在 SQL Server deadlock 的 code 是 1205
     * 3. 承上，因為 library 裡有轉成 40001，所以也要納入可能性之一
     */
    private Boolean isDeadlock(final CannotAcquireLockException exception) {
        Boolean isDeadlock = Boolean.FALSE;
        Throwable cause = exception.getCause();
        if(cause instanceof LockAcquisitionException) {
            Throwable cause2 = cause.getCause();
            // MySQL 處理邏輯
            if(cause2 instanceof BatchUpdateException) {
                String sqlState = ((BatchUpdateException)cause2).getSQLState();
                if("40001".equals(sqlState)) {
                    isDeadlock = Boolean.TRUE;
                }
            }
            // if (cause2 instanceof SQLServerException) {
            //     int errorCode = ((SQLServerException)(cause2)).getErrorCode();
            //     if(errorCode == 1205 || errorCode == 40001) {
            //         isDeadlock = Boolean.TRUE;
            //     }
            // }
            // TODO: MySQL 如果有出現的話補在這裡的 else
        }
        return isDeadlock;
    }

}


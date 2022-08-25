package org.dream.jpa.transaction.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeadLockRetry {

    /**
     * @return 重試次數，預設是 10
     * @apiNote 
     * 1. 經實測 50 次都有可能不夠
     * 2. 承上，時間不足，就設個 100 吧，反正只會針對交易 Deadlock 的作 while loop 重試 
     */
    
    int retryCount() default 10;
    
}

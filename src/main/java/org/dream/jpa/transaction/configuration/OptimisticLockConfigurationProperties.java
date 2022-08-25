package org.dream.jpa.transaction.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
@Data
@ConfigurationProperties(prefix = "optimistic-lock")
public class OptimisticLockConfigurationProperties {
    
    private int retryCount;

}

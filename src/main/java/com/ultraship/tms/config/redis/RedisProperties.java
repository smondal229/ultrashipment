package com.ultraship.tms.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private boolean ssl;
}
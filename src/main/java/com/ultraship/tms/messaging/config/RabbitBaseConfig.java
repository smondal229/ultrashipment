package com.ultraship.tms.messaging.config;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class RabbitBaseConfig {

    @Value("${cloudamqp.uri}")
    private String rabbitUri;

    @Bean
    public ConnectionFactory connectionFactory() {
        try {
            CachingConnectionFactory factory = new CachingConnectionFactory();
            factory.setUri(new URI(rabbitUri));
            factory.setConnectionTimeout(30000);
            factory.setRequestedHeartBeat(30);
            return factory;
        } catch (Exception e) {
            throw new RuntimeException("RabbitMQ config failed", e);
        }
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

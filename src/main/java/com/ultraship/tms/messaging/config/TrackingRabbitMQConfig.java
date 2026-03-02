package com.ultraship.tms.messaging.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.net.URI;

@Configuration
public class TrackingRabbitMQConfig {

    public static final String TRACKING_EXCHANGE = "tracking.exchange";
    public static final String TRACKING_QUEUE = "tracking.queue";
    public static final String TRACKING_ROUTING_KEY = "tracking.created";

    @Bean
    public TopicExchange trackingExchange() {
        return new TopicExchange(TRACKING_EXCHANGE);
    }

    @Bean
    public Queue trackingQueue() {
        return QueueBuilder.durable(TRACKING_QUEUE).build();
    }

    @Bean
    public Binding trackingBinding() {
        return BindingBuilder
                .bind(trackingQueue())
                .to(trackingExchange())
                .with(TRACKING_ROUTING_KEY);
    }
}

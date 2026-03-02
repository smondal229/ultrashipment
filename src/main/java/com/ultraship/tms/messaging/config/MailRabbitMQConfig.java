package com.ultraship.tms.messaging.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailRabbitMQConfig {

    public static final String MAIL_EXCHANGE = "mail.exchange";
    public static final String MAIL_QUEUE = "mail.queue";
    public static final String MAIL_ROUTING_KEY = "mail.send";

    public static final String MAIL_DLX = "mail.dlx";
    public static final String MAIL_DLQ = "mail.dlq";

    @Bean
    public TopicExchange mailExchange() {
        return new TopicExchange(MAIL_EXCHANGE);
    }

    @Bean
    public Queue mailQueue() {
        return QueueBuilder.durable(MAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", MAIL_DLX)
                .withArgument("x-dead-letter-routing-key", MAIL_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // retry after 60s
                .build();
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder
                .bind(mailQueue())
                .to(mailExchange())
                .with(MAIL_ROUTING_KEY);
    }
}
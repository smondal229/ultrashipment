package com.ultraship.tms.messaging.publisher;

import com.ultraship.tms.messaging.config.MailRabbitMQConfig;
import com.ultraship.tms.messaging.model.MailEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MailPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(MailEvent event) {
        rabbitTemplate.convertAndSend(
                MailRabbitMQConfig.MAIL_EXCHANGE,
                MailRabbitMQConfig.MAIL_ROUTING_KEY,
                event
        );
    }
}

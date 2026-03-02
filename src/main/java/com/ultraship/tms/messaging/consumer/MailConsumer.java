package com.ultraship.tms.messaging.consumer;

import com.ultraship.tms.messaging.config.MailRabbitMQConfig;
import com.ultraship.tms.messaging.model.MailEvent;
import com.ultraship.tms.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MailConsumer {

    private final EmailService emailService;

    public MailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = MailRabbitMQConfig.MAIL_QUEUE)
    public void handleMail(MailEvent event) {
        event.getType().process(emailService, event);
    }
}

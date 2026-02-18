package com.ultraship.tms.messaging.publishers;

import com.ultraship.tms.messaging.config.RabbitMQConfig;
import com.ultraship.tms.messaging.model.TrackingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(TrackingEvent event) {
        String routingKey = "tracking." + event.getStatus();
        event.setEventId(generateEventId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRACKING_EXCHANGE,
                routingKey,
                event
        );
    }

    private UUID generateEventId() {
        return UUID.randomUUID();
    }
}

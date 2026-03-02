package com.ultraship.tms.messaging.consumer;

import com.ultraship.tms.messaging.config.TrackingRabbitMQConfig;
import com.ultraship.tms.messaging.model.TrackingEvent;
import com.ultraship.tms.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventConsumer {
    private final TrackingService trackingService;

    @RabbitListener(queues = TrackingRabbitMQConfig.TRACKING_QUEUE)
    public void consume(TrackingEvent event) {
        log.info("Received shipment event: {}", event);

        trackingService.createTrackingFromEvent(event);
    }
}

package com.ultraship.tms.messaging.consumers;

import com.ultraship.tms.messaging.config.RabbitMQConfig;
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

    @RabbitListener(queues = RabbitMQConfig.TRACKING_QUEUE)
    public void consume(TrackingEvent event) {
        log.info("Received shipment event: {}", event);

        trackingService.createTrackingFromEvent(event);
    }
}

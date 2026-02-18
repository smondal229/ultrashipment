package com.ultraship.tms.mapper;

import com.ultraship.tms.domain.ShipmentTrackingEntity;
import com.ultraship.tms.graphql.model.ShipmentTracking;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ShipmentTrackingMapper {
    public ShipmentTracking toModel(ShipmentTrackingEntity shipmentTracking) {
        return new ShipmentTracking(
                shipmentTracking.getId(),
                shipmentTracking.getLocation(),
                shipmentTracking.getStatus(),
                shipmentTracking.getDescription(),
                shipmentTracking.getEventTime(),
                shipmentTracking.getCreatedAt()
        );
    }
}

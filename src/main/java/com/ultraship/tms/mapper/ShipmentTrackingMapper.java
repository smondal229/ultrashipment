package com.ultraship.tms.mapper;

import com.ultraship.tms.domain.entity.ShipmentTrackingEntity;
import com.ultraship.tms.graphql.model.output.ShipmentTracking;
import org.springframework.stereotype.Component;

@Component
public class ShipmentTrackingMapper {
    public ShipmentTracking toModel(ShipmentTrackingEntity shipmentTracking) {
        return new ShipmentTracking(
                shipmentTracking.getId(),
                shipmentTracking.getLocation(),
                shipmentTracking.getStatus(),
                shipmentTracking.getDescription(),
                shipmentTracking.getEventTime(),
                shipmentTracking.getCreatedAt(),
                shipmentTracking.getCreatedBy() != null ? shipmentTracking.getCreatedBy().getId() : null
        );
    }
}

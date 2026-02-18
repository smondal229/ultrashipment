package com.ultraship.tms.messaging.model;

import com.ultraship.tms.domain.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;


@Getter
@AllArgsConstructor
public class TrackingEvent {
    private Long shipmentId;
    private ShipmentStatus status;
    private String location;
    private String description;
    private Instant eventTime;
    @Setter
    private UUID eventId;
}

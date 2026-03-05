package com.ultraship.tms.graphql.model.output;

import com.ultraship.tms.domain.enums.ShipmentStatus;

import java.time.Instant;

public record ShipmentTracking(
    Long id,
    String location,
    ShipmentStatus status,
    String description,
    Instant eventTime,
    Instant createdAt,
    Long userId
) {}

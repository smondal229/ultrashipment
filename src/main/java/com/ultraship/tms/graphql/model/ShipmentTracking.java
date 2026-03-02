package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.ShipmentStatus;

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

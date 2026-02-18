package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.domain.PaymentMeta;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record ShipmentUpdateInput(
        String shipperName,
        Carrier carrierName,
        @NotBlank(message = "Current location is required")
        String currentLocation,
        String trackingNumber,
        @Positive(message = "Rate must be positive")
        BigDecimal rate,
        ShipmentStatus status,
        Instant pickedUpAt,
        Instant deliveredAt,
        @Positive(message = "Weight must be positive")
        Double weightGm,

        @Positive(message = "Length must be positive")
        Double lengthCm,

        @Positive(message = "Width must be positive")
        Double widthCm,

        @Positive(message = "Height must be positive")
        Double heightCm,
        PaymentMeta paymentMeta,
        ShipmentDeliveryType shipmentDeliveryType
) {}
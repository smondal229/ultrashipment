package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

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
        @Future
        Instant pickedUpAt,
        @Future
        Instant deliveredAt,
        @Positive(message = "Item value must be positive")
        BigDecimal itemValue,
        @Valid
        AddressInput pickupAddress,
        @Valid
        AddressInput deliveryAddress,
        @Valid
        Dimensions dimensions,

        PaymentMeta paymentMeta,
        ShipmentDeliveryType shipmentDeliveryType,

        Boolean isFlagged
) {}

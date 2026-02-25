package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.*;
import com.ultraship.tms.validator.FieldGreaterThan;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

@FieldGreaterThan(field = "itemValue", compareTo = "rate")
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

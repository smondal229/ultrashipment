package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record ShipmentCreateInput(
        @NotBlank(message = "Shipper name is required")
        String shipperName,
        @NotNull(message = "Carrier name is required")
        Carrier carrierName,
        @NotBlank(message = "Pickup location is required")
        String pickupLocation,
        @NotBlank(message = "Delivery location is required")
        String deliveryLocation,
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
        Dimensions dimensions,
        PaymentMeta paymentMeta,
        @NotNull(message = "Shipment delivery type is required")
        ShipmentDeliveryType shipmentDeliveryType
) {}
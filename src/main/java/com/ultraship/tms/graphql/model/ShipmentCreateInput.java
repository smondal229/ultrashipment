package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.domain.PaymentMeta;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.ShipmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
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
        @NotNull(message = "Pickup address is required")
        @Valid
        AddressInput pickupAddress,

        @NotNull(message = "Delivery address is required")
        @Valid
        AddressInput deliveryAddress,
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
        Dimensions dimensions,
        PaymentMeta paymentMeta,
        @NotNull(message = "Shipment delivery type is required")
        ShipmentDeliveryType shipmentDeliveryType
) {}
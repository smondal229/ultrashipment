package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.PaymentMeta;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.ShipmentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record Shipment(
        Long id,
        String shipperName,
        String carrierName,
        String pickupLocation,
        String deliveryLocation,
        String trackingNumber,
        BigDecimal rate,
        ShipmentStatus status,
        Instant pickedUpAt,
        Instant deliveredAt,
        Instant createdAt,
        Instant updatedAt,
        Double weightGm,
        Double lengthCm,
        Double heightCm,
        Double widthCm,
        PaymentMeta paymentMeta,
        ShipmentDeliveryType shipmentDeliveryType
) {}
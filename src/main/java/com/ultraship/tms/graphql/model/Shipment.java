package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.domain.PaymentMeta;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.ShipmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Shipment(
        Long id,
        String shipperName,
        Carrier carrierName,
        String pickupLocation,
        String deliveryLocation,
        String currentLocation,
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
        ShipmentDeliveryType shipmentDeliveryType,

        List<ShipmentTracking> tracking
) {}

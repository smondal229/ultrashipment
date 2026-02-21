package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.*;

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
        BigDecimal itemValue,
        BigDecimal itemWeight,
        BigDecimal itemLength,
        BigDecimal itemHeight,
        BigDecimal itemWidth,
        LengthUnit lengthUnit,
        WeightUnit weightUnit,
        PaymentMeta paymentMeta,
        ShipmentDeliveryType shipmentDeliveryType,

        List<ShipmentTracking> tracking
) {}

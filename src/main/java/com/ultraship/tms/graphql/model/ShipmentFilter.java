package com.ultraship.tms.graphql.model;


import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.ShipmentStatus;

import java.util.List;

public record ShipmentFilter(
        List<String> carrier,
        String trackingNumber,
        List<ShipmentStatus> status,
        List<ShipmentDeliveryType> shipmentDeliveryType,
        String shipperName,
        NumberRange rate,
        Boolean isFlagged
) {}

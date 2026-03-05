package com.ultraship.tms.graphql.model.filter;


import com.ultraship.tms.domain.enums.ShipmentDeliveryType;
import com.ultraship.tms.domain.enums.ShipmentStatus;

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

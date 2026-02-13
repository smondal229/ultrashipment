package com.ultraship.tms.graphql.model;

import java.util.List;

public record ShipmentOutput(
        List<Shipment> shipments,
        PageInfo pageInfo
) {}
package com.ultraship.tms.graphql.model.output;

import java.util.List;

public record ShipmentOutput(
        List<Shipment> shipments,
        PageInfo pageInfo
) {}
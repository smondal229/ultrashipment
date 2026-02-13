package com.ultraship.tms.graphql.controller;

import com.ultraship.tms.graphql.model.*;
import com.ultraship.tms.mapper.ShipmentMapper;
import com.ultraship.tms.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ShipmentGraphQLController {

    private final ShipmentService service;
    private final ShipmentMapper mapper;

    @QueryMapping
    public Shipment shipment(@Argument Long id) {
        return mapper.toModel(service.getById(id));
    }

    @QueryMapping
    public ShipmentOutput shipments(
            @Argument int pageSize,
            @Argument String after,
            @Argument ShipmentFilter filters,
            @Argument ShipmentSort sort
    ) {
        return service.fetchShipmentsAfterCursor(filters, pageSize, after, sort);
    }

    @MutationMapping
    public Shipment createShipment(@Argument @Valid ShipmentCreateInput input) {
        return service.create(input);
    }

    @MutationMapping
    public Shipment updateShipment(
            @Argument Long id,
            @Argument ShipmentUpdateInput input
    ) {
        return mapper.toModel(
                service.update(id, input)
        );
    }
}

package com.ultraship.tms.graphql.controller;

import com.ultraship.tms.graphql.model.*;
import com.ultraship.tms.service.ShipmentService;
import graphql.schema.DataFetchingFieldSelectionSet;
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

    @QueryMapping(name="getShipmentById")
    public Shipment getShipmentById(@Argument Long id, DataFetchingFieldSelectionSet selectionSet) {
        return service.getShipmentDetails(id, selectionSet.contains("tracking"));
    }

    @QueryMapping(name="getShipments")
    public ShipmentOutput getShipments(
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
        return service.update(id, input);
    }

    @MutationMapping
    public Boolean deleteShipment(@Argument Long id) {
        return service.deleteById(id);
    }
}

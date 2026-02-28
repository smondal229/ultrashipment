package com.ultraship.tms.graphql.controller;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.graphql.model.*;
import com.ultraship.tms.service.ShipmentService;
import graphql.schema.DataFetchingFieldSelectionSet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
            @Valid @Argument ShipmentUpdateInput input
    ) {
        return service.update(id, input);
    }

    @MutationMapping(name="flagShipmentById")
    public Boolean flagShipment(@Argument Long id, @Argument Boolean flagged) {
        return service.flagShipment(id, flagged);
    }

    @MutationMapping(name="deleteShipmentById")
    public Boolean deleteShipment(@Argument Long id) {
        return service.deleteById(id);
    }

    @QueryMapping(name="getAllFilterOptions")
    public Map<String, List<FilterOption>> getAllFilterOptions() {
        return service.getAllFilterOptions();
    }

    @QueryMapping
    public BigDecimal calculateRate(@Valid @Argument PricingRequest pricingRequest) {
        return service.calculateRate(pricingRequest);
    }

}

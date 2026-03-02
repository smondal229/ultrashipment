package com.ultraship.tms.graphql.controller;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.graphql.model.*;
import com.ultraship.tms.security.CustomUserPrincipal;
import com.ultraship.tms.service.ShipmentService;
import graphql.schema.DataFetchingFieldSelectionSet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Shipment createShipment(@AuthenticationPrincipal CustomUserPrincipal principal, @Argument @Valid ShipmentCreateInput input) {
        return service.create(principal, input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Shipment updateShipment(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Argument Long id,
            @Valid @Argument ShipmentUpdateInput input
    ) {
        return service.update(principal, id, input);
    }

    @MutationMapping(name="flagShipmentById")
    public Boolean flagShipment(@Argument Long id, @Argument Boolean flagged) {
        return service.flagShipment(id, flagged);
    }

    @MutationMapping(name="deleteShipmentById")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('DELETE_SHIPMENT')")
    public Boolean deleteShipment(@Argument Long id) {
        return service.deleteById(id);
    }

    @QueryMapping(name="getAllFilterOptions")
    public Map<String, List<FilterOption>> getAllFilterOptions() {
        return service.getAllFilterOptions();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BigDecimal calculateRate(@Valid @Argument PricingRequest pricingRequest) {
        return service.calculateRate(pricingRequest);
    }
}

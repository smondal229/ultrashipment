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

    @PreAuthorize("isAuthenticated()")
    @QueryMapping(name="getShipmentById")
    public Shipment getShipmentById(@Argument Long id, DataFetchingFieldSelectionSet selectionSet) {
        return service.getShipmentDetails(id, selectionSet.contains("tracking"));
    }

    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("hasAuthority('CREATE_SHIPMENT')")
    public Shipment createShipment(@AuthenticationPrincipal CustomUserPrincipal principal, @Argument @Valid ShipmentCreateInput input) {
        return service.create(principal, input);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('EDIT_SHIPMENT')")
    public Shipment updateShipment(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Argument Long id,
            @Valid @Argument ShipmentUpdateInput input
    ) {
        return service.update(principal, id, input);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping(name="flagShipmentById")
    public Boolean flagShipment(@Argument Long id, @Argument Boolean flagged) {
        return service.flagShipment(id, flagged);
    }

    @MutationMapping(name="deleteShipmentById")
    @PreAuthorize("hasAuthority('DELETE_SHIPMENT')")
    public Boolean deleteShipment(@Argument Long id) {
        return service.deleteById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping(name="getAllFilterOptions")
    public Map<String, List<FilterOption>> getAllFilterOptions() {
        return service.getAllFilterOptions();
    }

    @QueryMapping
//    @PreAuthorize("hasAuthority('FLAG_SHIPMENT')")
    public BigDecimal calculateRate(@Valid @Argument PricingRequest pricingRequest) {
        return service.calculateRate(pricingRequest);
    }
}

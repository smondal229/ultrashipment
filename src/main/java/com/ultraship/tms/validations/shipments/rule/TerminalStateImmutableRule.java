package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.domain.enums.ShipmentStatus;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TerminalStateImmutableRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        if (!context.isUpdate()) return;

        ShipmentEntity existing = context.existing();

        if (existing.getStatus() == ShipmentStatus.DELIVERED ||
                existing.getStatus() == ShipmentStatus.CANCELLED) {
            throw new InvalidShipmentStateException(
                    "Cannot update a shipment that is " +
                            existing.getStatus().name().toLowerCase()
            );
        }
    }
}

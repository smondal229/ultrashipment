package com.ultraship.tms.validations;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.domain.ShipmentStatus;
import com.ultraship.tms.exception.InvalidShipmentStateException;
import org.springframework.stereotype.Component;

@Component
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

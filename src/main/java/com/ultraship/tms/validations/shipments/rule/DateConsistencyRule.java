package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.input.ShipmentUpdateInput;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DateConsistencyRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        Instant pickedUp;
        Instant delivered;

        if (context.isCreate()) {
            pickedUp = context.create().pickedUpAt();
            delivered = context.create().deliveredAt();
        } else {
            ShipmentEntity existing = context.existing();
            ShipmentUpdateInput input = context.update();

            pickedUp = input.pickedUpAt() != null
                    ? input.pickedUpAt()
                    : existing.getPickedUpAt();

            delivered = input.deliveredAt() != null
                    ? input.deliveredAt()
                    : existing.getDeliveredAt();
        }

        if (delivered != null && pickedUp == null) {
            throw new InvalidShipmentStateException(
                    "Cannot set delivery date without pickup date"
            );
        }

        if (pickedUp != null && delivered != null &&
                delivered.isBefore(pickedUp)) {
            throw new InvalidShipmentStateException(
                    "Delivery date must be after pickup date"
            );
        }
    }
}

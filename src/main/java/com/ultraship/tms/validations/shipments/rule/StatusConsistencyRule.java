package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.domain.enums.ShipmentStatus;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.input.ShipmentCreateInput;
import com.ultraship.tms.graphql.model.input.ShipmentUpdateInput;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StatusConsistencyRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        ShipmentStatus effectiveStatus;
        Instant effectivePickedUpAt;
        Instant effectiveDeliveredAt;

        if (context.isCreate()) {

            ShipmentCreateInput input = context.create();

            effectiveStatus = input.status();
            effectivePickedUpAt = input.pickedUpAt();
            effectiveDeliveredAt = input.deliveredAt();

        } else {

            ShipmentEntity existing = context.existing();
            ShipmentUpdateInput input = context.update();

            effectiveStatus = input.status() != null
                    ? input.status()
                    : existing.getStatus();

            effectivePickedUpAt = input.pickedUpAt() != null
                    ? input.pickedUpAt()
                    : existing.getPickedUpAt();

            effectiveDeliveredAt = input.deliveredAt() != null
                    ? input.deliveredAt()
                    : existing.getDeliveredAt();
        }

        // PICKED_UP requires pickup time
        if (effectiveStatus == ShipmentStatus.PICKED_UP &&
                effectivePickedUpAt == null) {

            throw new InvalidShipmentStateException(
                    "Pickup date is required when status is PICKED_UP"
            );
        }

        // DELIVERED requires delivery time
        if (effectiveStatus == ShipmentStatus.DELIVERED &&
                effectiveDeliveredAt == null) {

            throw new InvalidShipmentStateException(
                    "Delivery date is required when status is DELIVERED"
            );
        }
    }
}

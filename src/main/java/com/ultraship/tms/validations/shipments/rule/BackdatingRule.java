package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.input.ShipmentUpdateInput;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.stereotype.Component;

@Component
public class BackdatingRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        if (!context.isUpdate()) return;

        ShipmentEntity existing = context.existing();
        ShipmentUpdateInput input = context.update();

        if (input.pickedUpAt() != null &&
                existing.getPickedUpAt() != null &&
                input.pickedUpAt().isBefore(existing.getPickedUpAt())) {
            throw new InvalidShipmentStateException("Cannot backdate pickup time");
        }

        if (input.deliveredAt() != null &&
                existing.getDeliveredAt() != null &&
                input.deliveredAt().isBefore(existing.getDeliveredAt())) {
            throw new InvalidShipmentStateException("Cannot backdate delivery time");
        }
    }
}

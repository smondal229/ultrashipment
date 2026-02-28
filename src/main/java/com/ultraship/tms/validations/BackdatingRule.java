package com.ultraship.tms.validations;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.ShipmentUpdateInput;
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

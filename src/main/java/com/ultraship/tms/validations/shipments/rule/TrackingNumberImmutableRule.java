package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.input.ShipmentUpdateInput;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class TrackingNumberImmutableRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        if (!context.isUpdate()) return;

        ShipmentEntity existing = context.existing();
        ShipmentUpdateInput input = context.update();

        if (existing.getTrackingNumber() != null &&
                input.trackingNumber() != null &&
                !input.trackingNumber().equals(existing.getTrackingNumber())) {

            throw new InvalidShipmentStateException(
                    "Tracking number cannot be updated once set"
            );
        }
    }
}

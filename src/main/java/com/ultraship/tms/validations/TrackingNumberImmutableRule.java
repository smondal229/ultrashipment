package com.ultraship.tms.validations;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.ShipmentUpdateInput;
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

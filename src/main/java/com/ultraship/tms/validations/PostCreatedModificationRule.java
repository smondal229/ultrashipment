package com.ultraship.tms.validations;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.domain.ShipmentStatus;
import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.ShipmentUpdateInput;
import org.springframework.stereotype.Component;

@Component
public class PostCreatedModificationRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        if (!context.isUpdate()) return;

        ShipmentEntity existing = context.existing();
        ShipmentUpdateInput input = context.update();

        if (existing.getStatus() == ShipmentStatus.CREATED) return;

        if (isChanged(input.shipperName(), existing.getShipperName()) ||
                isChanged(input.carrierName(), existing.getCarrierName()) ||
                isChanged(input.itemValue(), existing.getItemValue()) ||
                isChanged(input.rate(), existing.getRate())) {

            throw new InvalidShipmentStateException(
                    "Shipment details cannot be modified after it leaves CREATED state"
            );
        }
    }

    private boolean isChanged(Object newVal, Object oldVal) {
        return newVal != null && !newVal.equals(oldVal);
    }
}

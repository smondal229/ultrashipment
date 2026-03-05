package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.Address;
import com.ultraship.tms.domain.enums.ShipmentDeliveryType;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.input.AddressInput;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.stereotype.Component;

@Component
public class DeliveryTypeRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        ShipmentDeliveryType deliveryType;
        String pickupCountry;
        String deliveryCountry;

        if (context.isCreate()) {
            var input = context.create();
            deliveryType = input.shipmentDeliveryType();
            pickupCountry = input.pickupAddress() != null
                    ? input.pickupAddress().getCountry()
                    : null;
            deliveryCountry = input.deliveryAddress() != null
                    ? input.deliveryAddress().getCountry()
                    : null;

        } else {
            var existing = context.existing();
            var input = context.update();

            deliveryType = input.shipmentDeliveryType() != null
                    ? input.shipmentDeliveryType()
                    : existing.getShipmentDeliveryType();

            pickupCountry = resolveCountry(
                    input.pickupAddress(),
                    existing.getPickupAddress()
            );

            deliveryCountry = resolveCountry(
                    input.deliveryAddress(),
                    existing.getDeliveryAddress()
            );
        }

        if (deliveryType == ShipmentDeliveryType.SAME_DAY &&
                pickupCountry != null &&
                deliveryCountry != null &&
                !pickupCountry.equalsIgnoreCase(deliveryCountry)) {

            throw new InvalidShipmentStateException(
                    "Same day delivery is applicable only for domestic shipments"
            );
        }
    }

    private String resolveCountry(AddressInput input, Address existing) {
        if (input != null && input.getCountry() != null) {
            return input.getCountry();
        }
        return existing != null ? existing.getCountry() : null;
    }
}

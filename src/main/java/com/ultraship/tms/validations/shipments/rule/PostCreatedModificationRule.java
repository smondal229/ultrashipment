package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.domain.entity.Address;
import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.domain.enums.ShipmentStatus;
import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.input.AddressInput;
import com.ultraship.tms.graphql.model.input.ShipmentUpdateInput;
import com.ultraship.tms.mapper.ShipmentMapper;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(2)
public class PostCreatedModificationRule implements ShipmentValidationRule {

    private final ShipmentMapper mapper;

    public PostCreatedModificationRule(ShipmentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void validate(ShipmentValidationContext context) {

        if (!context.isUpdate()) return;

        ShipmentEntity existing = context.existing();
        ShipmentUpdateInput input = context.update();

        if (existing.getStatus() == ShipmentStatus.CREATED) return;

        if (isChanged(input.shipperName(), existing.getShipperName()) ||
                isChanged(input.carrierName(), existing.getCarrierName()) ||
                isChanged(input.itemValue(), existing.getItemValue()) ||
                isChanged(input.rate(), existing.getRate()) ||
                isAddressChanged(input.pickupAddress(), existing.getPickupAddress()) ||
                isAddressChanged(input.deliveryAddress(), existing.getDeliveryAddress()) ||
                isDimensionsChanged(input, existing)) {

            throw new InvalidShipmentStateException(
                    "Shipment details cannot be modified after it leaves CREATED state"
            );
        }
    }

    private <T> boolean isChanged(T newValue, T existingValue) {
        if (newValue == null) {
            return false;
        }

        if (existingValue == null) {
            return true;
        }

        if (newValue instanceof BigDecimal newBd && existingValue instanceof BigDecimal oldBd) {
            return newBd.compareTo(oldBd) != 0;
        }

        return !newValue.equals(existingValue);
    }

    private boolean isDimensionsChanged(ShipmentUpdateInput input, ShipmentEntity existing) {

        if (input.dimensions() == null) return false;

        return isChanged(input.dimensions().itemHeight(), existing.getItemHeight()) ||
                isChanged(input.dimensions().itemLength(), existing.getItemLength()) ||
                isChanged(input.dimensions().itemWeight(), existing.getItemWeight()) ||
                isChanged(input.dimensions().itemWidth(), existing.getItemWidth()) ||
                isChanged(input.dimensions().lengthUnit(), existing.getDimUnit()) ||
                isChanged(input.dimensions().weightUnit(), existing.getWeightUnit());
    }

    private boolean isAddressChanged(AddressInput addressInput, Address existingAddress) {

        if (addressInput == null) return false;

        Address newAddress = mapper.toAddressEntity(addressInput);

        return !newAddress.equals(existingAddress);
    }
}

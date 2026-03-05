package com.ultraship.tms.mapper;

import com.ultraship.tms.domain.entity.Address;
import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.graphql.model.input.AddressInput;
import com.ultraship.tms.graphql.model.input.Dimensions;
import com.ultraship.tms.graphql.model.input.ShipmentCreateInput;
import com.ultraship.tms.graphql.model.output.Shipment;
import com.ultraship.tms.graphql.model.output.ShipmentTracking;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShipmentMapper {

    public Shipment toModel(ShipmentEntity e, boolean includeTracking) {
        List<ShipmentTracking> tracking = null;

        if (includeTracking) {
             tracking = e.getTracking() == null
                    ? List.of()
                    : e.getTracking()
                    .stream()
                    .map(new ShipmentTrackingMapper()::toModel)
                    .toList();
        }

        return new Shipment(
            e.getId(),
            e.getShipperName(),
            e.getCarrierName(),
            this.toAddressInput(e.getPickupAddress()),
            this.toAddressInput(e.getDeliveryAddress()),
            e.getCurrentLocation(),
            e.getTrackingNumber(),
            e.getRate(),
            e.getStatus(),
            e.getPickedUpAt(),
            e.getDeliveredAt(),
            e.getCreatedAt(),
            e.getUpdatedAt(),
            e.getItemValue(),
            e.getItemWeight(),
            e.getItemLength(),
            e.getItemHeight(),
            e.getItemWidth(),
            e.getDimUnit(),
            e.getWeightUnit(),
            e.getPaymentMeta(),
            e.getShipmentDeliveryType(),
            e.getIsFlagged(),
            tracking
        );
    }

    public Shipment toModel(ShipmentEntity e) {
        return new Shipment(
                e.getId(),
                e.getShipperName(),
                e.getCarrierName(),
                this.toAddressInput(e.getPickupAddress()),
                this.toAddressInput(e.getDeliveryAddress()),
                e.getCurrentLocation(),
                e.getTrackingNumber(),
                e.getRate(),
                e.getStatus(),
                e.getPickedUpAt(),
                e.getDeliveredAt(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getItemValue(),
                e.getItemWeight(),
                e.getItemLength(),
                e.getItemHeight(),
                e.getItemWidth(),
                e.getDimUnit(),
                e.getWeightUnit(),
                e.getPaymentMeta(),
                e.getShipmentDeliveryType(),
                e.getIsFlagged(),
                null
        );
    }

    public ShipmentEntity toEntity(ShipmentCreateInput input) {
        ShipmentEntity e = new ShipmentEntity();
        e.setShipperName(input.shipperName());
        e.setCarrierName(input.carrierName());
        e.setPickupAddress(this.toAddressEntity(input.pickupAddress()));
        e.setDeliveryAddress(this.toAddressEntity(input.deliveryAddress()));
        e.setCurrentLocation(input.currentLocation());
        e.setTrackingNumber(input.trackingNumber());
        e.setRate(input.rate());
        e.setPickedUpAt(input.pickedUpAt());
        e.setDeliveredAt(input.deliveredAt());
        e.setStatus(input.status());
        e.setItemValue(input.itemValue());
        if (input.dimensions() != null) {
            Dimensions dimensions = input.dimensions();

            e.setItemWeight(dimensions.itemWeight());
            e.setItemLength(dimensions.itemLength());
            e.setItemHeight(dimensions.itemHeight());
            e.setItemWidth(dimensions.itemWidth());
            e.setDimUnit(dimensions.lengthUnit());
            e.setWeightUnit(dimensions.weightUnit());
        }
        e.setPaymentMeta(input.paymentMeta());
        e.setShipmentDeliveryType(input.shipmentDeliveryType());
        return e;
    }

    private AddressInput toAddressInput(Address addr) {
        return new AddressInput(
                addr.getCity(),
                addr.getPostalCode(),
                addr.getState(),
                addr.getCountry(),
                addr.getStreet(),
                addr.getContactNumber()
        );
    }

    public Address toAddressEntity(AddressInput addressInput) {
        return new Address(
                addressInput.getCity(),
                addressInput.getPostalCode(),
                addressInput.getState(),
                addressInput.getCountry(),
                addressInput.getStreet(),
                addressInput.getContactNumber()
        );
    }
}

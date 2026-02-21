package com.ultraship.tms.mapper;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.graphql.model.Dimensions;
import com.ultraship.tms.graphql.model.Shipment;
import com.ultraship.tms.graphql.model.ShipmentCreateInput;
import com.ultraship.tms.graphql.model.ShipmentTracking;
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
            e.getPickupLocation(),
            e.getDeliveryLocation(),
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
                e.getPickupLocation(),
                e.getDeliveryLocation(),
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
        e.setPickupLocation(input.pickupLocation());
        e.setDeliveryLocation(input.deliveryLocation());
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
        e.setIsFlagged(input.isFlagged() != null ? input.isFlagged() : false);
        e.setDeleted(false);
        return e;
    }
}

package com.ultraship.tms.mapper;

import com.ultraship.tms.domain.ShipmentEntity;
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
            e.getWeightGm(),
            e.getLengthCm(),
            e.getHeightCm(),
            e.getWidthCm(),
            e.getPaymentMeta(),
            e.getShipmentDeliveryType(),
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
                e.getWeightGm(),
                e.getLengthCm(),
                e.getHeightCm(),
                e.getWidthCm(),
                e.getPaymentMeta(),
                e.getShipmentDeliveryType(),
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
        e.setWeightGm(input.weightGm());
        e.setLengthCm(input.lengthCm());
        e.setHeightCm(input.heightCm());
        e.setWidthCm(input.widthCm());
        e.setPaymentMeta(input.paymentMeta());
        e.setShipmentDeliveryType(input.shipmentDeliveryType());
        return e;
    }
}

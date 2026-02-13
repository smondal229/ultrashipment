package com.ultraship.tms.mapper;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.graphql.model.Shipment;
import com.ultraship.tms.graphql.model.ShipmentCreateInput;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    public Shipment toModel(ShipmentEntity e) {
        return new Shipment(
            e.getId(),
            e.getShipperName(),
            e.getCarrierName(),
            e.getPickupLocation(),
            e.getDeliveryLocation(),
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
            e.getShipmentDeliveryType()
        );
    }

    public ShipmentEntity toEntity(ShipmentCreateInput input) {
        ShipmentEntity e = new ShipmentEntity();
        e.setShipperName(input.shipperName());
        e.setCarrierName(input.carrierName());
        e.setPickupLocation(input.pickupLocation());
        e.setDeliveryLocation(input.deliveryLocation());
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

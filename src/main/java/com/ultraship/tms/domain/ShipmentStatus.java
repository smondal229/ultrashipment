package com.ultraship.tms.domain;

import lombok.Getter;

@Getter
public enum ShipmentStatus {
    CREATED("Created"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    OUT_FOR_DELIVERY("Out For Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    ShipmentStatus(String label) {
        this.label = label;
    }

    private final String label;

}

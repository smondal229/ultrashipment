package com.ultraship.tms.domain.enums;

import lombok.Getter;

@Getter
public enum ShipmentDeliveryType {
    STANDARD("Standard"),
    EXPRESS("Express"),
    SAME_DAY("Same Day");

    ShipmentDeliveryType(String label) {
        this.label = label;
    }

    private final String label;

}

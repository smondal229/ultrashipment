package com.ultraship.tms.domain;

import lombok.Getter;

@Getter
public enum Carrier {
    DHL("01", "DHL"),
    FEDEX("02", "FedEx"),
    UPS("03", "UPS"),
    BLUEDART("04", "Blue Dart"),
    DELHIVERY("05", "Delhivery");

    Carrier(String code, String label) {
        this.code = code;
        this.label = label;
    }

    private final String code;
    private final String label;

    public String getLabel() {
        return label;
    }
}

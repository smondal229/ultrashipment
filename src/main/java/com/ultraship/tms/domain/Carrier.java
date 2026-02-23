package com.ultraship.tms.domain;

import lombok.Getter;

@Getter
public enum Carrier {
    DHL("01"),
    FEDEX("02"),
    UPS("03"),
    BLUEDART("04"),
    DELHIVERY("05");

    Carrier(String code) {
        this.code = code;
    }

    private final String code;

}

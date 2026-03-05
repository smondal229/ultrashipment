package com.ultraship.tms.rule.pricing.model;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class WeightSlab {
    private final BigDecimal min;
    private final BigDecimal max;
    private final BigDecimal ratePerKg;

    public boolean matches(BigDecimal weight) {
        return weight.compareTo(min) >= 0 &&
                weight.compareTo(max) <= 0;
    }

    public BigDecimal getRatePerKg() {
        return ratePerKg;
    }
}
package com.ultraship.tms.graphql.utils;

import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BaseRateCalculator {

    private final BigDecimal BASE;
    private final BigDecimal PER_KG_RATE;
    private final BigDecimal VOLUMETRIC_DIVISOR;

    public BaseRateCalculator(BigDecimal base,
                              BigDecimal perKgRate,
                              BigDecimal volumetricDivisor) {
        this.BASE = base;
        this.PER_KG_RATE = perKgRate;
        this.VOLUMETRIC_DIVISOR = volumetricDivisor;
    }

    public BigDecimal calculate(PricingRequest req) {

        BigDecimal weightKg = UnitConverter.toKg(req.getItemWeight(), req.getWeightUnit());

        BigDecimal l = UnitConverter.toCm(req.getItemLength(), req.getLengthUnit());
        BigDecimal w = UnitConverter.toCm(req.getItemWidth(), req.getLengthUnit());
        BigDecimal h = UnitConverter.toCm(req.getItemHeight(), req.getLengthUnit());

        BigDecimal volumetricWeight = l.multiply(w).multiply(h)
                .divide(VOLUMETRIC_DIVISOR, 3, RoundingMode.HALF_UP);

        BigDecimal chargeableWeight = weightKg.max(volumetricWeight);

        return BASE.add(chargeableWeight.multiply(PER_KG_RATE));
    }
}

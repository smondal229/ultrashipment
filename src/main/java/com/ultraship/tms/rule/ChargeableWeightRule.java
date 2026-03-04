package com.ultraship.tms.rule;

import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.PricingContext;
import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.graphql.utils.UnitConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ChargeableWeightRule implements CalculationRule {

    private final BigDecimal volumetricDivisor;
    private static final int WEIGHT_MULTIPLIER = 5;

    public ChargeableWeightRule(BigDecimal volumetricDivisor) {
        this.volumetricDivisor = volumetricDivisor;
    }

    @Override
    public void apply(PricingContext context, PricingRequest request) {

        BigDecimal weightKg = UnitConverter.toKg(
                request.getItemWeight(),
                request.getWeightUnit());

        BigDecimal l = UnitConverter.toCm(
                request.getItemLength(),
                request.getLengthUnit());

        BigDecimal w = UnitConverter.toCm(
                request.getItemWidth(),
                request.getLengthUnit());

        BigDecimal h = UnitConverter.toCm(
                request.getItemHeight(),
                request.getLengthUnit());

        BigDecimal volumetricWeight = l.multiply(w)
                .multiply(h)
                .divide(volumetricDivisor, 3, RoundingMode.HALF_UP);

        BigDecimal weightThreshold = weightKg.multiply(
                BigDecimal.valueOf(WEIGHT_MULTIPLIER)
        );

        BigDecimal chargeable;

        if (volumetricWeight.compareTo(weightThreshold) < 0) {
            chargeable = weightKg.max(volumetricWeight);
        } else {
            chargeable = weightKg;
        }

        chargeable = chargeable.setScale(0, RoundingMode.CEILING);

        context.setChargeableWeight(chargeable);
    }
}

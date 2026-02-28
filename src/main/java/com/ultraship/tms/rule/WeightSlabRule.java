package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.graphql.utils.UnitConverter;

import java.math.BigDecimal;

public class WeightSlabRule implements PricingRule {

    private final BigDecimal slabStartKg;
    private final BigDecimal perExtraKgCharge;

    public WeightSlabRule(BigDecimal slabStartKg,
                          BigDecimal perExtraKgCharge) {
        this.slabStartKg = slabStartKg;
        this.perExtraKgCharge = perExtraKgCharge;
    }

    @Override
    public BigDecimal apply(BigDecimal currentRate, PricingRequest request) {

        BigDecimal weightKg = UnitConverter.toKg(
                request.getItemWeight(),
                request.getWeightUnit()
        );

        if (weightKg.compareTo(slabStartKg) <= 0) {
            return currentRate;
        }

        BigDecimal extraWeight = weightKg.subtract(slabStartKg);

        BigDecimal extraCharge = extraWeight.multiply(perExtraKgCharge);

        return currentRate.add(extraCharge);
    }
}

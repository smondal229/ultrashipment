package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FuelSurchargeRule implements PricingRule {

    private final BigDecimal fuelPercentage; // e.g. 18 means 18%

    public FuelSurchargeRule(BigDecimal fuelPercentage) {
        this.fuelPercentage = fuelPercentage;
    }

    @Override
    public BigDecimal apply(BigDecimal currentRate, PricingRequest request) {

        BigDecimal fuelCharge = currentRate
                .multiply(fuelPercentage)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        return currentRate.add(fuelCharge);
    }
}

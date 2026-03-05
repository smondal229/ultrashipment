package com.ultraship.tms.rule.pricing;

import com.ultraship.tms.rule.pricing.context.PricingContext;
import com.ultraship.tms.graphql.model.input.PricingRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FuelSurchargeRule implements PricingRule {

    private final BigDecimal fuelPercentage; // e.g. 18 means 18%

    public FuelSurchargeRule(BigDecimal fuelPercentage) {
        this.fuelPercentage = fuelPercentage;
    }

    @Override
    public void apply(PricingContext pricingContext, PricingRequest request) {
        BigDecimal currentRate = pricingContext.getRate();

        BigDecimal fuelCharge = currentRate
                .multiply(fuelPercentage)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        pricingContext.setRate(currentRate.add(fuelCharge));
    }
}

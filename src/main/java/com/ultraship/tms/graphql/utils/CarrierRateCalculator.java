package com.ultraship.tms.graphql.utils;

import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.rule.PricingRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CarrierRateCalculator {

    private final BaseRateCalculator baseCalculator;
    private final List<PricingRule> rules;

    public CarrierRateCalculator(BaseRateCalculator baseCalculator,
                                 List<PricingRule> rules) {
        this.baseCalculator = baseCalculator;
        this.rules = rules;
    }

    public BigDecimal calculate(PricingRequest request) {

        BigDecimal rate = baseCalculator.calculate(request);

        for (PricingRule rule : rules) {
            rate = rule.apply(rate, request);
        }

        return rate.setScale(2, RoundingMode.HALF_UP);
    }
}

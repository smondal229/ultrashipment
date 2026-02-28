package com.ultraship.tms.service;
import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.graphql.utils.BaseRateCalculator;
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

        // 1️⃣ Base calculation (weight vs volumetric logic lives here)
        BigDecimal amount = baseCalculator.calculate(request);

        // 2️⃣ Apply all pricing rules in sequence
        for (PricingRule rule : rules) {
            amount = rule.apply(amount, request);
        }

        // 3️⃣ Final rounding
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}

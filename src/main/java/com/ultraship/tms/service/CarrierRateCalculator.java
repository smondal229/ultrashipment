package com.ultraship.tms.service;
import com.ultraship.tms.graphql.model.PricingContext;
import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.graphql.utils.BaseRateCalculator;
import com.ultraship.tms.rule.CalculationRule;
import com.ultraship.tms.rule.PricingRule;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@AllArgsConstructor
public class CarrierRateCalculator {
    private final List<CalculationRule> calculationRules;
    private final List<PricingRule> pricingRules;

    public BigDecimal calculate(PricingRequest request) {

        PricingContext context = new PricingContext();

        for (CalculationRule rule : calculationRules) {
            rule.apply(context, request);
        }

        for (PricingRule rule : pricingRules) {
            rule.apply(context, request);
        }

        return context.getRate().setScale(2, RoundingMode.HALF_UP);
    }
}

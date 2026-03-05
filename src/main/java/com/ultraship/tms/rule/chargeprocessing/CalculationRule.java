package com.ultraship.tms.rule.chargeprocessing;

import com.ultraship.tms.rule.pricing.context.PricingContext;
import com.ultraship.tms.graphql.model.input.PricingRequest;

public interface CalculationRule {
    void apply(PricingContext context, PricingRequest request);
}
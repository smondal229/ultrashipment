package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingContext;
import com.ultraship.tms.graphql.model.PricingRequest;

public interface CalculationRule {
    void apply(PricingContext context, PricingRequest request);
}
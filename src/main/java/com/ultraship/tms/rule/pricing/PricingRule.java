package com.ultraship.tms.rule.pricing;

import com.ultraship.tms.rule.pricing.context.PricingContext;
import com.ultraship.tms.graphql.model.input.PricingRequest;

public interface PricingRule {

    void apply(PricingContext pricingContext, PricingRequest request);
}
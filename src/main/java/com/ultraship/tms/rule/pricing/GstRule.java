package com.ultraship.tms.rule.pricing;

import com.ultraship.tms.rule.pricing.context.PricingContext;
import com.ultraship.tms.graphql.model.input.PricingRequest;

import java.math.BigDecimal;

public class GstRule implements PricingRule {

    private final BigDecimal gstPercent;

    public GstRule(BigDecimal gstPercent) {
        this.gstPercent = gstPercent;
    }

    @Override
    public void apply(PricingContext pricingContext, PricingRequest request) {
        BigDecimal currentRate = pricingContext.getRate();
        pricingContext.setRate(currentRate.add(currentRate.multiply(gstPercent)));
    }
}
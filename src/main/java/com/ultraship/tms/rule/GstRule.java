package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;

public class GstRule implements PricingRule {

    private final BigDecimal gstPercent;

    public GstRule(BigDecimal gstPercent) {
        this.gstPercent = gstPercent;
    }

    @Override
    public BigDecimal apply(BigDecimal currentRate, PricingRequest request) {
        return currentRate.add(currentRate.multiply(gstPercent));
    }
}
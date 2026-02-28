package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;

public interface PricingRule {

    BigDecimal apply(BigDecimal currentRate, PricingRequest request);
}
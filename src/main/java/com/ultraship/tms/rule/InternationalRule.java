package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;

public class InternationalRule implements PricingRule {

    private final BigDecimal surcharge;

    public InternationalRule(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    @Override
    public BigDecimal apply(BigDecimal currentRate, PricingRequest req) {

        if (!req.getPickupCountry().equalsIgnoreCase(req.getDeliveryCountry())) {
            return currentRate.add(surcharge);
        }

        return currentRate;
    }
}

package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingContext;
import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;

public class InternationalRule implements PricingRule {

    private final BigDecimal surcharge;

    public InternationalRule(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    @Override
    public void apply(PricingContext pricingContext, PricingRequest request) {
        BigDecimal currentRate = pricingContext.getRate();
        if (!request.getPickupCountry().equalsIgnoreCase(request.getDeliveryCountry())) {
            pricingContext.setRate(currentRate.add(surcharge));
            return;
        }

        pricingContext.setRate(currentRate);
    }
}

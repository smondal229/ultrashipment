package com.ultraship.tms.rule.pricing;

import com.ultraship.tms.domain.enums.ShipmentDeliveryType;
import com.ultraship.tms.rule.pricing.context.PricingContext;
import com.ultraship.tms.graphql.model.input.PricingRequest;

import java.math.BigDecimal;
import java.util.Map;

public class DeliveryPercentageRule implements PricingRule {

    private final Map<ShipmentDeliveryType, BigDecimal> percentages;

    public DeliveryPercentageRule(Map<ShipmentDeliveryType, BigDecimal> percentages) {
        this.percentages = percentages;
    }

    @Override
    public void apply(PricingContext pricingContext, PricingRequest request) {
        BigDecimal currentRate = pricingContext.getRate();

        BigDecimal percent = percentages.getOrDefault(
                request.getDeliveryType(),
                BigDecimal.ZERO
        );

        pricingContext.setRate(currentRate.add(currentRate.multiply(percent)));
    }
}

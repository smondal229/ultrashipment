package com.ultraship.tms.rule;

import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;
import java.util.Map;

public class DeliveryPercentageRule implements PricingRule {

    private final Map<ShipmentDeliveryType, BigDecimal> percentages;

    public DeliveryPercentageRule(Map<ShipmentDeliveryType, BigDecimal> percentages) {
        this.percentages = percentages;
    }

    @Override
    public BigDecimal apply(BigDecimal currentRate, PricingRequest request) {

        BigDecimal percent = percentages.getOrDefault(
                request.getDeliveryType(),
                BigDecimal.ZERO
        );

        return currentRate.add(currentRate.multiply(percent));
    }
}

package com.ultraship.tms.rule.pricing;

import com.ultraship.tms.exception.shipments.InvalidShipmentStateException;
import com.ultraship.tms.rule.pricing.context.PricingContext;
import com.ultraship.tms.graphql.model.input.PricingRequest;
import com.ultraship.tms.rule.pricing.model.WeightSlab;

import java.math.BigDecimal;
import java.util.List;

public class SlabPricingRule implements PricingRule {
    private final List<WeightSlab> slabs;

    public SlabPricingRule(List<WeightSlab> slabs) {
        this.slabs = slabs;
    }

    @Override
    public void apply(PricingContext pricingContext, PricingRequest pricingRequest) {
        BigDecimal chargeableWeight = pricingContext.getChargeableWeight();
        BigDecimal baseRate = pricingContext.getRate();

        for (WeightSlab slab : slabs) {
            if (slab.matches(chargeableWeight)) {
                pricingContext.setRate(baseRate.add(
                        chargeableWeight.multiply(slab.getRatePerKg())
                ));
                return;
            }
        }

        throw new InvalidShipmentStateException("No slab matched for weight: "
                + chargeableWeight);
    }
}

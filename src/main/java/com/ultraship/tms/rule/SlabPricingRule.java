package com.ultraship.tms.rule;

import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.graphql.model.PricingContext;
import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.graphql.model.WeightSlab;

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

package com.ultraship.tms.graphql.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PricingContext {

    private BigDecimal chargeableWeight;
    private BigDecimal rate;

    public PricingContext() {
        this.rate = BigDecimal.ZERO;
    }

}

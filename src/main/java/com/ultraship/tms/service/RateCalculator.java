package com.ultraship.tms.service;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.graphql.model.PricingRequest;

import java.math.BigDecimal;

public interface RateCalculator {

    Carrier getSupportedCarrier();

    BigDecimal calculateRate(PricingRequest request);
}

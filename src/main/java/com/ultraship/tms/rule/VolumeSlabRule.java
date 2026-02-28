package com.ultraship.tms.rule;

import com.ultraship.tms.graphql.model.PricingRequest;
import com.ultraship.tms.graphql.utils.UnitConverter;

import java.math.BigDecimal;

public class VolumeSlabRule implements PricingRule {

    private final BigDecimal slabStartVolumeInCC; // in cubic cm
    private final BigDecimal perExtraCubicCmCharge;

    public VolumeSlabRule(BigDecimal slabStartVolume, BigDecimal perExtraCubicCmCharge) {
        this.slabStartVolumeInCC = slabStartVolume;
        this.perExtraCubicCmCharge = perExtraCubicCmCharge;
    }

    @Override
    public BigDecimal apply(BigDecimal currentRate, PricingRequest request) {

        BigDecimal l = UnitConverter.toCm(request.getItemLength(), request.getLengthUnit());
        BigDecimal w = UnitConverter.toCm(request.getItemWidth(), request.getLengthUnit());
        BigDecimal h = UnitConverter.toCm(request.getItemHeight(), request.getLengthUnit());

        BigDecimal volume = l.multiply(w).multiply(h);

        if (volume.compareTo(slabStartVolumeInCC) <= 0) {
            return currentRate;
        }

        BigDecimal extraVolume = volume.subtract(slabStartVolumeInCC);

        BigDecimal extraCharge = extraVolume.multiply(perExtraCubicCmCharge);

        return currentRate.add(extraCharge);
    }
}
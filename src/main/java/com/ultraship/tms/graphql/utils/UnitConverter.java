package com.ultraship.tms.graphql.utils;

import com.ultraship.tms.domain.LengthUnit;
import com.ultraship.tms.domain.WeightUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;


public final class UnitConverter {

    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private static final BigDecimal MILLION = BigDecimal.valueOf(1_000_000);
    private static final BigDecimal LB_TO_KG = BigDecimal.valueOf(0.453592);

    private static final BigDecimal M_TO_CM = BigDecimal.valueOf(100);
    private static final BigDecimal FT_TO_CM = BigDecimal.valueOf(30.48);
    private static final BigDecimal IN_TO_CM = BigDecimal.valueOf(2.54);

    private UnitConverter() {}

    public static BigDecimal toKg(BigDecimal weight, WeightUnit unit) {

        return switch (unit) {
            case KG -> weight;
            case GM -> weight.divide(THOUSAND, 6, RoundingMode.HALF_UP);
            case MG -> weight.divide(MILLION, 6, RoundingMode.HALF_UP);
            case LB -> weight.multiply(LB_TO_KG);
        };
    }

    public static BigDecimal toCm(BigDecimal length, LengthUnit unit) {

        return switch (unit) {
            case CM -> length;
            case M -> length.multiply(M_TO_CM);
            case FT -> length.multiply(FT_TO_CM);
            case IN -> length.multiply(IN_TO_CM);
        };
    }
}

package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.LengthUnit;
import com.ultraship.tms.domain.WeightUnit;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record Dimensions (
    @Positive(message = "Length must be positive")
    BigDecimal itemLength,
    @Positive(message = "Width must be positive")
    BigDecimal itemWidth,
    @Positive(message = "Height must be positive")
    BigDecimal itemHeight,
    LengthUnit lengthUnit,

    @Positive(message = "Weight must be positive")
    BigDecimal itemWeight,
    WeightUnit weightUnit
) {};

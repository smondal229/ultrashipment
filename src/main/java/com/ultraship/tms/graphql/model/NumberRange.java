package com.ultraship.tms.graphql.model;

import java.math.BigDecimal;

public record NumberRange(
    BigDecimal min,
    BigDecimal max
) {
}

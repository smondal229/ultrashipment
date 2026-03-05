package com.ultraship.tms.graphql.model.filter;

import java.math.BigDecimal;

public record NumberRange(
    BigDecimal min,
    BigDecimal max
) {
}

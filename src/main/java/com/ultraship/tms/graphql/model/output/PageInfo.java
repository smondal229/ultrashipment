package com.ultraship.tms.graphql.model.output;

public record PageInfo(
        boolean hasNextPage,
        String endCursor
) {}

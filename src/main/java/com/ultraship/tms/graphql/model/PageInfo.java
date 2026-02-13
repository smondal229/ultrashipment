package com.ultraship.tms.graphql.model;

public record PageInfo(
        boolean hasNextPage,
        String endCursor
) {}

package com.ultraship.tms.graphql.utils;

public record CursorPayload(
        String sortValue,
        Long id
) {}
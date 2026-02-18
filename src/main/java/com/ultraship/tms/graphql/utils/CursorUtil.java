package com.ultraship.tms.graphql.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;

public final class CursorUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private CursorUtil() {}

    public static String encode(String sortValue, String fieldValue) {
        CursorPayload payload = new CursorPayload(sortValue, fieldValue);
        try {
            return Base64.getEncoder()
                    .encodeToString(OBJECT_MAPPER.writeValueAsBytes(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static CursorPayload decodePayload(String cursor) {
        byte[] decoded = Base64.getDecoder().decode(cursor);
        try {
            return OBJECT_MAPPER.readValue(decoded, CursorPayload.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

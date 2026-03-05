package com.ultraship.tms.exception.ratelimit;

public class RatelimitException extends RuntimeException {
    public RatelimitException(String message) {
        super(message);
    }
}

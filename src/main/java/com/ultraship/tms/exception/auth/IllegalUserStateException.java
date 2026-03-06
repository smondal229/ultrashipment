package com.ultraship.tms.exception.auth;

public class IllegalUserStateException extends UnauthorizedException {
    public IllegalUserStateException(String message) {
        super(message);
    }
}

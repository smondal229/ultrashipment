package com.ultraship.tms.exception.auth;

public class UserNotVerifiedException extends UnauthorizedException {
    public UserNotVerifiedException(String message) {
        super(message);
    }
}

package com.ultraship.tms.exception.auth;

public class UsernameAlreadyPresentException extends RuntimeException {
    public UsernameAlreadyPresentException(String message) {
        super(message);
    }
}

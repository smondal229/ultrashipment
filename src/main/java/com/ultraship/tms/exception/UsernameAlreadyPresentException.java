package com.ultraship.tms.exception;

public class UsernameAlreadyPresentException extends RuntimeException {
    public UsernameAlreadyPresentException(String message) {
        super(message);
    }
}

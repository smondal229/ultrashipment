package com.ultraship.tms.exception.auth;

public class InvalidCredentialException extends UnauthorizedException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}

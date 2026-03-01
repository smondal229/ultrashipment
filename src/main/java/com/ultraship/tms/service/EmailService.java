package com.ultraship.tms.service;

public interface EmailService {
    void sendVerificationEmail(String username, String token);
    void sendResetEmail(String username, String token);
}

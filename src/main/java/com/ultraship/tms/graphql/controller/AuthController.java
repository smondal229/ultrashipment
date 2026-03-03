package com.ultraship.tms.graphql.controller;

import com.ultraship.tms.graphql.model.AuthResponse;
import com.ultraship.tms.graphql.model.ResetPasswordResponse;
import com.ultraship.tms.graphql.model.SignupInput;
import com.ultraship.tms.graphql.model.VerifyEmailResponse;
import com.ultraship.tms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    public Boolean signup(@Argument SignupInput signupInput) {
        return authService.signup(signupInput);
    }

    @MutationMapping
    public AuthResponse login(
            @Argument String username,
            @Argument String password
    ) {
        return authService.login(username, password);
    }

    @MutationMapping
    public AuthResponse refreshToken(
            @Argument String refreshToken
    ) {
        return authService.refresh(refreshToken);
    }

    @MutationMapping
    public VerifyEmailResponse verifyEmail(@Argument String token) {
        return authService.verifyEmail(token);
    }

    @MutationMapping
    public boolean resend(@Argument String username) {
        return authService.resendVerificationEmail(username);
    }

    @MutationMapping
    public boolean requestPasswordReset(@Argument String username) {
        return authService.requestPasswordReset(username);
    }

    @MutationMapping
    public ResetPasswordResponse resetPassword(@Argument String refreshToken, @Argument String newPassword) {
        return authService.resetPassword(refreshToken, newPassword);
    }

    @MutationMapping
    public boolean logout(@Argument String refreshToken) {
        return authService.logout(refreshToken);
    }
}
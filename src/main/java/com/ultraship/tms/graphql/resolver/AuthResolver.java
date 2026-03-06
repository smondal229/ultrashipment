package com.ultraship.tms.graphql.resolver;

import com.ultraship.tms.graphql.model.output.AuthResponse;
import com.ultraship.tms.graphql.model.output.ResetPasswordResponse;
import com.ultraship.tms.graphql.model.input.SignupInput;
import com.ultraship.tms.graphql.model.output.VerifyEmailResponse;
import com.ultraship.tms.ratelimiter.RateLimit;
import com.ultraship.tms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthResolver {

    private final AuthService authService;

    @MutationMapping
    @RateLimit(limit = 3, duration = 60)
    public Boolean signup(@Argument @Valid SignupInput signupInput) {
        return authService.signup(signupInput);
    }

    @MutationMapping
    @RateLimit(limit = 5, duration = 60)
    public AuthResponse login(
            @Argument String username,
            @Argument String password
    ) {
        return authService.login(username, password);
    }

    @MutationMapping
    @RateLimit(limit = 3, duration = 60)
    public AuthResponse refreshToken(
            @Argument String refreshToken
    ) {
        return authService.refresh(refreshToken);
    }

    @MutationMapping
    @RateLimit(limit = 2, duration = 30)
    public VerifyEmailResponse verifyEmail(@Argument String token) {
        return authService.verifyEmail(token);
    }

    @MutationMapping
    @RateLimit(limit = 2, duration = 30)
    public boolean resendVerificationEmail(@Argument String username) {
        return authService.resendVerificationEmail(username);
    }

    @MutationMapping
    @RateLimit(limit = 2, duration = 60)
    public boolean requestPasswordReset(@Argument String username) {
        return authService.requestPasswordReset(username);
    }

    @MutationMapping
    @RateLimit(limit = 2, duration = 60)
    public ResetPasswordResponse resetPassword(@Argument String token, @Argument String newPassword) {
        return authService.resetPassword(token, newPassword);
    }

    @MutationMapping
    @RateLimit(limit = 3, duration = 60)
    public boolean logout(@Argument String refreshToken) {
        return authService.logout(refreshToken);
    }
}
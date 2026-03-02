package com.ultraship.tms.service;

import com.ultraship.tms.domain.*;
import com.ultraship.tms.exception.InvalidCredentialException;
import com.ultraship.tms.exception.UserNotVerifiedException;
import com.ultraship.tms.exception.UsernameAlreadyPresentException;
import com.ultraship.tms.graphql.model.AuthResponse;
import com.ultraship.tms.graphql.model.SignupInput;
import com.ultraship.tms.messaging.model.MailEvent;
import com.ultraship.tms.messaging.publisher.MailPublisher;
import com.ultraship.tms.repository.EmailVerificationTokenRepository;
import com.ultraship.tms.repository.PasswordResetTokenRepository;
import com.ultraship.tms.repository.RefreshTokenRepository;
import com.ultraship.tms.repository.UserRepository;
import com.ultraship.tms.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final MailPublisher mailPublisher;


    public boolean signup(SignupInput signupInput) {
        if (userRepository.findByUsername(signupInput.getUsername()).isPresent()) {
            throw new UsernameAlreadyPresentException("User already exists");
        }

        User user = new User();
        user.setUsername(signupInput.getUsername());
        user.setPassword(passwordEncoder.encode(signupInput.getPassword()));
        user.setRole(signupInput.getRole());
        user.setVerified(false);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUsername(signupInput.getUsername());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(verificationToken);

        MailEvent event = new MailEvent(
                signupInput.getUsername(),
                token,
                MailEvent.MailType.VERIFICATION
        );

        mailPublisher.publish(event);

        return true;
    }

    public boolean verifyEmail(String token) {
        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findValidToken(token, LocalDateTime.now())
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = userRepository
                .findByUsername(verificationToken.getUsername())
                .orElseThrow();

        user.setVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);

        return true;
    }

    @Transactional
    public boolean resendVerificationEmail(String username) {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return true; // prevent user enumeration
        }

        User user = optionalUser.get();

        if (user.isVerified()) {
            return true;
        }

        // Remove old unused tokens
        emailVerificationTokenRepository
                .deleteUnusedTokensByUsername(username);

        // Generate new token
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUsername(username);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationToken.setUsed(false);

        emailVerificationTokenRepository.save(verificationToken);

        // Send email
        emailService.sendVerificationEmail(username, token);

        return true;
    }

    public boolean requestPasswordReset(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsername(username);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);

        emailService.sendResetEmail(username, token);
        return true;
    }

    public boolean resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = userRepository
                .findByUsername(resetToken.getUsername())
                .orElseThrow();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // IMPORTANT: revoke all refresh tokens
        refreshTokenRepository.revokeAllByUsername(user.getUsername());
        return true;
    }

    public AuthResponse login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialException("Invalid credentials");
        }

        if (!user.isVerified()) {
            throw new UserNotVerifiedException("User is not verified");
        }

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(String.valueOf(user.getRole()))
                        .build();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(username);

        saveRefreshToken(username, refreshToken);

        return new AuthResponse(accessToken, refreshToken, username);
    }

    public AuthResponse refresh(String refreshToken) {
        try {
            RefreshToken token = refreshTokenRepository.findActiveToken(refreshToken, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Refresh token expired");
            }

            User user = userRepository.findByUsername(token.getUsername())
                    .orElseThrow();

            UserDetails userDetails =
                    org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities(String.valueOf(user.getRole()))
                            .build();

            String newAccessToken = jwtService.generateAccessToken(userDetails);

            return new AuthResponse(newAccessToken, refreshToken, user.getUsername());
        } catch (RuntimeException e) {
            log.error("Token refresh error", e);
            throw e;
        }
    }

    @Transactional
    public boolean logout(String refreshToken) {
        try {
            RefreshToken token = refreshTokenRepository
                    .findActiveToken(refreshToken, LocalDateTime.now())
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            token.setRevoked(true);
            refreshTokenRepository.save(token);

            return true;
        } catch (RuntimeException e) {
            log.error("Logout error", e);
            return false;
        }
    }

    private void saveRefreshToken(String username, String tokenValue) {
        RefreshToken token = new RefreshToken();
        token.setUsername(username);
        token.setToken(tokenValue);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(token);
    }
}
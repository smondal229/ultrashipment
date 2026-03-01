package com.ultraship.tms.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String username;

    private LocalDateTime expiryDate;

    private boolean used = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}

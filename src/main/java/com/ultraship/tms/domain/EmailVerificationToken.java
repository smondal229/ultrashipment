package com.ultraship.tms.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
@Data
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    private String username;

    private LocalDateTime expiryDate;

    private boolean used = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}

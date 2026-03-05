package com.ultraship.tms.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private String username;

    private LocalDateTime expiryDate;

    private boolean revoked = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}

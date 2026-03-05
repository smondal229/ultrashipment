package com.ultraship.tms.repository;

import com.ultraship.tms.domain.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    @Query("""
       SELECT t FROM EmailVerificationToken t
       WHERE t.token = :token
       AND t.used = false
       AND t.expiryDate > :now
       """)
    Optional<EmailVerificationToken> findValidToken(
            @Param("token") String token,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken t WHERE t.username = :username AND t.used = false")
    void deleteUnusedTokensByUsername(@Param("username") String username);
}

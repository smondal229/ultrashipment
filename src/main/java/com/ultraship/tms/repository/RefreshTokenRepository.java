package com.ultraship.tms.repository;

import com.ultraship.tms.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("""
       SELECT r FROM RefreshToken r
       WHERE r.token = :token
       AND r.revoked = false
       AND r.expiryDate > :now
       """)
    Optional<RefreshToken> findActiveToken(
            @Param("token") String token,
            @Param("now") LocalDateTime now
    );
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.username = :username")
    void revokeAllByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.token = :token")
    void revokeByToken(@Param("token") String token);
}

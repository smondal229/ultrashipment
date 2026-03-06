package com.ultraship.tms.repository;

import com.ultraship.tms.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);

        @Modifying
        @Query("UPDATE User u SET u.active = :active WHERE u.id = :id AND u.active <> :active")
        int changeActiveStatus(@Param("id") Long id, @Param("active") Boolean activeStatus);
}

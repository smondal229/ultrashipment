package com.ultraship.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ultraship.tms.domain.ShipmentEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ShipmentRepository
        extends JpaRepository<ShipmentEntity, Long>, ShipmentRepositoryCustom {

        @Query("""
                SELECT DISTINCT s
                FROM ShipmentEntity s
                LEFT JOIN FETCH s.tracking t
                WHERE s.id = :id
                ORDER BY t.createdAt DESC
        """)
        Optional<ShipmentEntity> findByIdWithTracking(Long id);
}

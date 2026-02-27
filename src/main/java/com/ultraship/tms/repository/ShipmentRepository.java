package com.ultraship.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ultraship.tms.domain.ShipmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface ShipmentRepository
        extends JpaRepository<ShipmentEntity, Long>, ShipmentRepositoryCustom {

        @Query("""
                SELECT DISTINCT s
                FROM ShipmentEntity s
                LEFT JOIN FETCH s.tracking t
                WHERE s.id = :id AND s.deleted = false
                ORDER BY t.createdAt DESC
        """)
        Optional<ShipmentEntity> findByIdWithTracking(Long id);

        @Query("SELECT s FROM ShipmentEntity s WHERE s.id = :id AND s.deleted = false")
        Optional<ShipmentEntity> findActiveById(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query("UPDATE ShipmentEntity s SET s.deleted = true WHERE s.id = :id AND s.deleted = false")
        int softDeleteById(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query("UPDATE ShipmentEntity s SET s.isFlagged = :isFlagged WHERE s.id = :id AND s.deleted = false")
        int flagShipmentById(@Param("id") Long id, @Param("isFlagged") boolean isFlagged);
}

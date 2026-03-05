package com.ultraship.tms.repository;

import com.ultraship.tms.domain.entity.ShipmentTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTrackingEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO shipment_tracking (
            event_id,
            shipment_id,
            status,
            location,
            event_time,
            description,
            created_by_id
        )
        VALUES (:eventId, :shipmentId, :status, :location, :eventTime, :description, :userId)
        ON CONFLICT (event_id) DO NOTHING
        """, nativeQuery = true)
    void insertIfNotExists(
            UUID eventId,
            long shipmentId,
            String status,
            String location,
            Instant eventTime,
            String description,
            Long userId
    );
}

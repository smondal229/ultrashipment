package com.ultraship.tms.service;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.domain.ShipmentTrackingEntity;
import com.ultraship.tms.messaging.model.TrackingEvent;
import com.ultraship.tms.repository.ShipmentTrackingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final ShipmentTrackingRepository trackingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public void createTrackingFromEvent(TrackingEvent event) {
        try {
            trackingRepository.insertIfNotExists(
                    event.getEventId(),
                    event.getShipmentId(),
                    event.getStatus().name(),
                    event.getLocation(),
                    event.getEventTime(),
                    event.getDescription()
            );
        } catch (Exception e) {
            log.error("Create tracking event error: ", e);
        }
    }
}


package com.ultraship.tms.service;

import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.domain.ShipmentStatus;
import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.exception.ShipmentNotFoundException;
import com.ultraship.tms.graphql.model.*;
import com.ultraship.tms.graphql.utils.CursorPayload;
import com.ultraship.tms.graphql.utils.CursorUtil;
import com.ultraship.tms.mapper.ShipmentMapper;
import com.ultraship.tms.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper mapper;

    public ShipmentEntity getById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
    }

    /**
     * Cursor-based fetch.
     * Returns first + 1 records to detect next page.
     */
    public ShipmentOutput fetchShipmentsAfterCursor(
            ShipmentFilter filters,
            int pageSize,
            String after,
            ShipmentSort sort
    ) {        ShipmentSort effectiveSort = sort != null
            ? sort
            : new ShipmentSort(ShipmentSort.Field.ID, ShipmentSort.Direction.ASC);

        CursorPayload cursor = after != null ? CursorUtil.decodePayload(after) : null;

        List<ShipmentEntity> fetched = shipmentRepository.findAfterCursor(
            filters,
            cursor,
            pageSize + 1,
            sort
        );

        boolean hasNextPage = fetched.size() > pageSize;

        List<ShipmentEntity> shipmentEntities = hasNextPage
                ? fetched.subList(0, pageSize)
                : fetched;

        List<Shipment> shipmentList = shipmentEntities.stream()
                .map(mapper::toModel)
                .toList();

        String endCursor = shipmentEntities.isEmpty()
                ? null
                : CursorUtil.encode(
                effectiveSort.field().name(),
                shipmentEntities.get(shipmentEntities.size() - 1).getId()
        );

        return new ShipmentOutput(
                shipmentList,
                new PageInfo(
                        hasNextPage,
                        endCursor
                )
        );
    }

    public long countAll() {
        return shipmentRepository.count();
    }

    public Shipment create(ShipmentCreateInput input) {
        try {
            validateStatusConsistency(input);

            ShipmentEntity mappedEntity = mapper.toEntity(input);

            applyBusinessDefaults(mappedEntity);

            ShipmentEntity savedEntity = shipmentRepository.save(mappedEntity);

            return mapper.toModel(savedEntity);
        } catch (Exception error) {
            log.error("Shipment creation error: ", error);
            throw error;
        }
    }

    public ShipmentEntity update(Long id, ShipmentUpdateInput updateInput) {
        try {
            ShipmentEntity existing = getById(id);
            // Validate update operations
            validateUpdate(existing, updateInput);

            // Update fields (only if provided - partial update support)
            updateIfPresent(existing, updateInput);

            return shipmentRepository.save(existing);
        } catch (Exception error) {
            log.error("Shipment creation error: ", error);
            throw error;
        }
    }

    private void validateStatusConsistency(ShipmentCreateInput input) {
        if (input.status() == ShipmentStatus.PICKED_UP && input.pickedUpAt() == null) {
            throw new InvalidShipmentStateException(
                    "Pickup date is required when status is PICKED_UP"
            );
        }

        if (input.status() == ShipmentStatus.DELIVERED && input.deliveredAt() == null) {
            throw new InvalidShipmentStateException(
                    "Delivery date is required when status is DELIVERED"
            );
        }
    }

    private void applyBusinessDefaults(ShipmentEntity entity) {
        // Apply default status based on dates if not explicitly set
        if (entity.getStatus() == null) {
            entity.setStatus(deriveStatusFromDates(entity));
        }

        // Apply default delivery type
        if (entity.getShipmentDeliveryType() == null) {
            entity.setShipmentDeliveryType(ShipmentDeliveryType.STANDARD);
        }
    }

    private ShipmentStatus deriveStatusFromDates(ShipmentEntity entity) {
        if (entity.getDeliveredAt() != null) {
            return ShipmentStatus.DELIVERED;
        }
        if (entity.getPickedUpAt() != null) {
            return ShipmentStatus.PICKED_UP;
        }
        return ShipmentStatus.CREATED;
    }

    private void validateUpdate(ShipmentEntity existing, ShipmentUpdateInput input) {
        // 1. Prevent updating immutable fields based on status
        if (existing.getStatus() == ShipmentStatus.DELIVERED) {
            throw new InvalidShipmentStateException(
                    "Cannot update a delivered shipment"
            );
        }

        // 2. Date consistency validation
        Instant newPickedUpAt = input.pickedUpAt() != null ? input.pickedUpAt() : existing.getPickedUpAt();
        Instant newDeliveredAt = input.deliveredAt() != null ? input.deliveredAt() : existing.getDeliveredAt();

        if (newDeliveredAt != null && newPickedUpAt == null) {
            throw new InvalidShipmentStateException(
                    "Cannot set delivery date without pickup date"
            );
        }

        if (newPickedUpAt != null && newDeliveredAt != null && newDeliveredAt.isBefore(newPickedUpAt)) {
            throw new InvalidShipmentStateException(
                    "Delivery date must be after pickup date"
            );
        }

        // 3. Status transition validation
        if (input.status() != null) {
            validateStatusTransition(existing.getStatus(), input.status(), newPickedUpAt, newDeliveredAt);
        }

        // 4. Prevent backdating critical timestamps
        if (input.pickedUpAt() != null && existing.getPickedUpAt() != null) {
            if (input.pickedUpAt().isBefore(existing.getPickedUpAt())) {
                throw new InvalidShipmentStateException(
                        "Cannot backdate pickup time"
                );
            }
        }

        if (input.deliveredAt() != null && existing.getDeliveredAt() != null) {
            if (input.deliveredAt().isBefore(existing.getDeliveredAt())) {
                throw new InvalidShipmentStateException(
                        "Cannot backdate delivery time"
                );
            }
        }
    }

    private void validateStatusTransition(
            ShipmentStatus currentStatus,
            ShipmentStatus newStatus,
            Instant pickedUpAt,
            Instant deliveredAt
    ) {
        // Define valid state transitions
        Map<ShipmentStatus, Set<ShipmentStatus>> allowedTransitions = Map.of(
                ShipmentStatus.CREATED, Set.of(ShipmentStatus.PICKED_UP, ShipmentStatus.CANCELLED),
                ShipmentStatus.PICKED_UP, Set.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED),
                ShipmentStatus.IN_TRANSIT, Set.of(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED),
                ShipmentStatus.DELIVERED, Set.of(), // Terminal state
                ShipmentStatus.CANCELLED, Set.of()  // Terminal state
        );

        if (!allowedTransitions.get(currentStatus).contains(newStatus)) {
            throw new InvalidShipmentStateException(
                    String.format("Cannot transition from %s to %s", currentStatus, newStatus)
            );
        }

        // Status-specific validations
        if (newStatus == ShipmentStatus.PICKED_UP && pickedUpAt == null) {
            throw new InvalidShipmentStateException(
                    "Cannot set status to PICKED_UP without pickup date"
            );
        }

        if (newStatus == ShipmentStatus.DELIVERED && deliveredAt == null) {
            throw new InvalidShipmentStateException(
                    "Cannot set status to DELIVERED without delivery date"
            );
        }
    }

    private void updateIfPresent(ShipmentEntity existing, ShipmentUpdateInput input) {
        // Update string fields
        if (input.shipperName() != null) {
            existing.setShipperName(input.shipperName());
        }
        if (input.carrierName() != null) {
            existing.setCarrierName(input.carrierName());
        }
        if (input.pickupLocation() != null) {
            existing.setPickupLocation(input.pickupLocation());
        }
        if (input.deliveryLocation() != null) {
            existing.setDeliveryLocation(input.deliveryLocation());
        }
        if (input.trackingNumber() != null) {
            existing.setTrackingNumber(input.trackingNumber());
        }

        // Update numeric fields
        if (input.rate() != null) {
            existing.setRate(input.rate());
        }
        if (input.weightGm() != null) {
            existing.setWeightGm(input.weightGm());
        }
        if (input.lengthCm() != null) {
            existing.setLengthCm(input.lengthCm());
        }
        if (input.widthCm() != null) {
            existing.setWidthCm(input.widthCm());
        }
        if (input.heightCm() != null) {
            existing.setHeightCm(input.heightCm());
        }

        // Update enum fields
        if (input.status() != null) {
            existing.setStatus(input.status());
        }
        if (input.shipmentDeliveryType() != null) {
            existing.setShipmentDeliveryType(input.shipmentDeliveryType());
        }

        // Update timestamp fields
        if (input.pickedUpAt() != null) {
            existing.setPickedUpAt(input.pickedUpAt());
        }
        if (input.deliveredAt() != null) {
            existing.setDeliveredAt(input.deliveredAt());
        }

        // Update nested object
        if (input.paymentMeta() != null) {
            existing.setPaymentMeta(input.paymentMeta());
        }
    }
}

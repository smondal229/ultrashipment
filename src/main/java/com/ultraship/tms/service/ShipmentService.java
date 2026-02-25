package com.ultraship.tms.service;

import com.ultraship.tms.domain.*;
import com.ultraship.tms.exception.InvalidShipmentStateException;
import com.ultraship.tms.exception.ShipmentNotFoundException;
import com.ultraship.tms.graphql.model.*;
import com.ultraship.tms.graphql.utils.CursorPayload;
import com.ultraship.tms.graphql.utils.CursorUtil;
import com.ultraship.tms.mapper.ShipmentMapper;
import com.ultraship.tms.messaging.model.TrackingEvent;
import com.ultraship.tms.messaging.publishers.TrackingEventPublisher;
import com.ultraship.tms.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper mapper;
    private final TrackingEventPublisher trackingEventPublisher;


    @Transactional(readOnly = true)
    public Shipment getShipmentDetails(Long id, boolean includeTracking) {
        try {

            Supplier<ShipmentNotFoundException> notFoundExceptionSupplier = () -> new ShipmentNotFoundException(id);

            ShipmentEntity entity = includeTracking
                ? shipmentRepository.findByIdWithTracking(id)
                    .orElseThrow(notFoundExceptionSupplier)
                : shipmentRepository.findActiveById(id)
                    .orElseThrow(notFoundExceptionSupplier);

            return mapper.toModel(entity, includeTracking);
        } catch (Exception error) {
            log.error("Error fetching shipment details for fieldValue {}: ", id, error);
            throw error;
        }
    }

    public ShipmentEntity getById(Long id) {
        return shipmentRepository.findActiveById(id)
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
    ) {
        ShipmentSort effectiveSort = sort != null
            ? sort
            : new ShipmentSort(ShipmentSort.Field.ID, ShipmentSort.Direction.ASC);

        CursorPayload cursor = after != null ? CursorUtil.decodePayload(after) : null;

        List<ShipmentEntity> fetched = shipmentRepository.findAfterCursor(
            filters,
            cursor,
            pageSize + 1,
            effectiveSort
        );

        boolean hasNextPage = fetched.size() > pageSize;

        List<ShipmentEntity> shipmentEntities = hasNextPage
                ? fetched.subList(0, pageSize)
                : fetched;

        List<Shipment> shipmentList = shipmentEntities.stream()
                .map(mapper::toModel)
                .toList();

        ShipmentEntity last =
                shipmentEntities.isEmpty() ? null :
                        shipmentEntities.get(shipmentEntities.size() - 1);

        String endCursor = null;

        if (last != null) {
            String sortValue = extractSortValue(last, effectiveSort.field());

            endCursor = CursorUtil.encode(
                    effectiveSort.field().name(),
                    sortValue
            );
        }

        return new ShipmentOutput(
                shipmentList,
                new PageInfo(
                        hasNextPage,
                        endCursor
                )
        );
    }


    public Shipment create(ShipmentCreateInput input) {
        try {
            validateStatusConsistency(input);
            validatePhysicalAttributes(input.dimensions());

            ShipmentEntity mappedEntity = mapper.toEntity(input);

            applyBusinessDefaults(mappedEntity);
            mappedEntity.setTrackingNumber(new DefaultTrackingNumberGenerator().generate(input.carrierName()));
            ShipmentEntity savedEntity = shipmentRepository.save(mappedEntity);
            this.publishCreatedEvent(savedEntity);
            return mapper.toModel(savedEntity);
        } catch (Exception error) {
            log.error("Shipment creation error: ", error);
            throw error;
        }
    }

    public Shipment update(Long id, ShipmentUpdateInput updateInput) {
        try {
            ShipmentEntity existing = getById(id);
            // Validate update operations
            validateUpdate(existing, updateInput);
            if (updateInput.dimensions() != null) validatePhysicalAttributes(updateInput.dimensions());

            // Update fields (only if provided - partial update support)
            updateIfPresent(existing, updateInput);

            ShipmentEntity savedEntity = shipmentRepository.save(existing);
            this.publishCreatedEvent(savedEntity);
            return mapper.toModel(savedEntity);
        } catch (Exception error) {
            log.error("Shipment update error: ", error);
            throw error;
        }
    }

    public boolean deleteById(Long id) {
        int updated = shipmentRepository.softDeleteById(id);
        if (updated == 0) {
            throw new ShipmentNotFoundException(id);
        }
        return true;
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

    private void validatePhysicalAttributes(Dimensions input) {

        boolean dimensionPresent =
                input.itemLength() != null ||
                        input.itemWidth() != null ||
                        input.itemHeight() != null;

        if (dimensionPresent && input.lengthUnit() == null) {
            throw new IllegalArgumentException(
                    "lengthUnit is required when any dimension is provided"
            );
        }

        if (input.itemWeight() != null && input.weightUnit() == null) {
            throw new IllegalArgumentException(
                    "weightUnit is required when itemWeight is provided"
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

        if (entity.getCurrentLocation() == null) {
            entity.setCurrentLocation(entity.getPickupAddress().getCity());
        }

        entity.setPaymentMeta(new PaymentMeta(
                null,
                null,
                "USD",
                null,
                null,
                PaymentStatus.PENDING
        ));
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

    private <T> boolean isChanged(T newValue, T existingValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.equals(existingValue);
    }

    private boolean isDimensionsChanged(ShipmentUpdateInput input, ShipmentEntity existing) {
        if (input.dimensions() == null) {
            return false;
        }

        return isChanged(input.dimensions().itemHeight(), existing.getItemHeight()) ||
                isChanged(input.dimensions().itemLength(), existing.getItemLength()) ||
                isChanged(input.dimensions().itemWeight(), existing.getItemWeight()) ||
                isChanged(input.dimensions().itemWidth(), existing.getItemWidth()) ||
                isChanged(input.dimensions().lengthUnit(), existing.getDimUnit()) ||
                isChanged(input.dimensions().weightUnit(), existing.getWeightUnit());
    }

    private void validateUpdate(ShipmentEntity existing, ShipmentUpdateInput input) {
        // 1. Prevent updating immutable fields based on status
        if (existing.getStatus() == ShipmentStatus.DELIVERED) {
            throw new InvalidShipmentStateException(
                    "Cannot update a delivered shipment"
            );
        }

        // 2. Block updates after created status
        if (existing.getStatus() != ShipmentStatus.CREATED) {
            if (isChanged(input.shipperName(), existing.getShipperName()) ||
                    isChanged(input.carrierName(), existing.getCarrierName()) ||
                    isChanged(input.pickupAddress(), existing.getPickupAddress()) ||
                    isChanged(input.deliveryAddress(), existing.getDeliveryAddress()) ||
                    isDimensionsChanged(input, existing) ||
                    isChanged(input.itemValue(), existing.getItemValue()) ||
                    isChanged(input.rate(), existing.getRate())) {
                throw new InvalidShipmentStateException(
                        "Shipment details cannot be modified after it leaves CREATED state"
                );
            }
        }

        // 3. Date consistency validation
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
        if (input.status() != null && !input.status().equals(existing.getStatus())) {
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

        if (existing.getTrackingNumber() != null && input.trackingNumber() != null && !input.trackingNumber().equals(existing.getTrackingNumber())) {
            throw new InvalidShipmentStateException(
                    "Tracking number cannot be updated once set"
            );
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
                ShipmentStatus.OUT_FOR_DELIVERY, Set.of(ShipmentStatus.DELIVERED),
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

        if (existing.getTrackingNumber() == null && input.trackingNumber() != null) {
            existing.setTrackingNumber(input.trackingNumber());
        }

        // Update numeric fields
        if (isChanged(input.rate(), existing.getRate())) {
            existing.setRate(input.rate());
        }

        if (isChanged(input.itemValue(), existing.getItemValue())) {
            existing.setItemValue(input.itemValue());
        }

        if (input.dimensions() != null) {
            Dimensions dimensions = input.dimensions();

            if (dimensions.itemWeight() != null) {
                existing.setItemWeight(dimensions.itemWeight());
            }
            if (dimensions.itemLength() != null) {
                existing.setItemLength(dimensions.itemLength());
            }
            if (dimensions.itemWidth() != null) {
                existing.setItemWidth(dimensions.itemWidth());
            }
            if (dimensions.itemHeight() != null) {
                existing.setItemHeight(dimensions.itemHeight());
            }
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

        if (!input.currentLocation().isEmpty()) {
            existing.setCurrentLocation(input.currentLocation());
        }

        if (input.isFlagged() != null) {
            existing.setIsFlagged(input.isFlagged());
        }

        if (isChanged(input.pickupAddress(), existing.getPickupAddress())) {
            existing.setPickupAddress(existing.getPickupAddress());
        }

        if (isChanged(input.deliveryAddress(), existing.getDeliveryAddress())) {
            existing.setDeliveryAddress(existing.getDeliveryAddress());
        }
    }

    private void publishCreatedEvent(ShipmentEntity s) {
        trackingEventPublisher.publish(new TrackingEvent(
                s.getId(),
                s.getStatus(),
                s.getCurrentLocation(),
                "",
                Instant.now(),
                null
        ));
    }

    private String extractSortValue(
            ShipmentEntity entity,
            ShipmentSort.Field field
    ) {
        return switch (field) {
            case ID -> entity.getId().toString();
            case RATE -> entity.getRate().toString();
            case STATUS -> entity.getStatus().name();
            case CARRIER -> entity.getCarrierName().name();
            case SHIPPER -> entity.getShipperName();
            case ITEM_VALUE -> entity.getItemValue().toString();
            case UPDATED_AT -> entity.getUpdatedAt().toString();
        };
    }
}

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
import com.ultraship.tms.validations.ShipmentValidationContext;
import com.ultraship.tms.validations.ShipmentValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final Logger log = LoggerFactory.getLogger(ShipmentService.class);
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper mapper;
    private final TrackingEventPublisher trackingEventPublisher;
    private final CarrierRateFactory carrierRateFactory;
    private final ShipmentValidator shipmentValidator;


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
            ShipmentValidationContext context =
                    ShipmentValidationContext.forCreate(input);

            shipmentValidator.validate(context);

            ShipmentEntity entity = mapper.toEntity(input);
            applyBusinessDefaults(entity);

            entity.setTrackingNumber(
                    new DefaultTrackingNumberGenerator()
                            .generate(input.carrierName())
            );

            ShipmentEntity savedEntity = shipmentRepository.save(entity);
            this.publishTrackingEvent(savedEntity);
            return mapper.toModel(savedEntity);
        } catch (Exception error) {
            log.error("Shipment creation error: ", error);
            throw error;
        }
    }

    public Shipment update(Long id, ShipmentUpdateInput input) {

        ShipmentEntity existing = getById(id);

        ShipmentValidationContext context =
                ShipmentValidationContext.forUpdate(existing, input);

        shipmentValidator.validate(context);

        updateIfPresent(existing, input);

        ShipmentEntity saved = shipmentRepository.save(existing);
        this.publishTrackingEvent(saved);

        return mapper.toModel(saved);
    }

    public Boolean flagShipment(Long id, boolean flagged) {
        try {
            int updated = shipmentRepository.flagShipmentById(id, flagged);
            if (updated == 0) {
                throw new ShipmentNotFoundException(id);
            }
            return true;
        } catch (Exception e) {
            log.error("Shipment flagging error: ", e);
            throw e;
        }
    }

    public boolean deleteById(Long id) {
        try {
            int updated = shipmentRepository.softDeleteById(id);
            if (updated == 0) {
                throw new ShipmentNotFoundException(id);
            }
            return true;
        } catch (Exception error) {
            log.error("Shipment delete error: ", error);
            throw error;
        }
    }

    public Map<String, List<FilterOption>> getAllFilterOptions() {
        List<FilterOption> carrierOptions = Arrays.stream(Carrier.values())
                .map(c -> new FilterOption(c.name(), c.getLabel()))
                .toList();

        List<FilterOption> statusOptions = Arrays.stream(ShipmentStatus.values())
                .map(s -> new FilterOption(s.name(), s.getLabel()))
                .toList();

        List<FilterOption> deliveryTypeOptions = Arrays.stream(ShipmentDeliveryType.values())
                .map(s -> new FilterOption(s.name(), s.getLabel()))
                .toList();

        return Map.of(
                "carriers", carrierOptions,
                "statuses", statusOptions,
                "shipmentDeliveryTypes", deliveryTypeOptions
        );
    }

    public BigDecimal calculateRate(PricingRequest request) {
        CarrierRateCalculator calculator =
                carrierRateFactory.get(request.getCarrierName());

        return calculator.calculate(request);
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

        if (existingValue == null) {
            return true;
        }

        if (newValue instanceof BigDecimal newBd && existingValue instanceof BigDecimal oldBd) {
            return newBd.compareTo(oldBd) != 0;
        }

        return !newValue.equals(existingValue);
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


        if (input.pickupAddress() != null) {
            Address newPickupAddress = mapper.toAddressEntity(input.pickupAddress());
            if (!existing.getPickupAddress().equals(newPickupAddress)) {
                existing.setPickupAddress(newPickupAddress);
            }
        }

        if (input.deliveryAddress() != null) {
            Address newDeliveryAddress = mapper.toAddressEntity(input.deliveryAddress());
            if (!existing.getDeliveryAddress().equals(newDeliveryAddress)) {
                existing.setDeliveryAddress(newDeliveryAddress);
            }
        }
    }

    private String resolveCountry(AddressInput inputAddress, Address existingAddress) {

        if (inputAddress != null && inputAddress.getCountry() != null) {
            return inputAddress.getCountry();
        }

        if (existingAddress != null) {
            return existingAddress.getCountry();
        }

        return null;
    }

    private void publishTrackingEvent(ShipmentEntity s) {
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

package com.ultraship.tms.repository;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.graphql.model.ShipmentFilter;
import com.ultraship.tms.graphql.model.ShipmentSort;
import com.ultraship.tms.graphql.utils.CursorPayload;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryCustomImpl implements ShipmentRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<ShipmentEntity> findAfterCursor(
            ShipmentFilter filters,
            CursorPayload cursor,
            int pageSize,
            ShipmentSort sort
    ) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ShipmentEntity> cq = cb.createQuery(ShipmentEntity.class);
        Root<ShipmentEntity> root = cq.from(ShipmentEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        // filter
        if (filters != null) {

            // Carrier (IN filter)
            if (filters.carrier() != null && !filters.carrier().isEmpty()) {
                predicates.add(
                        root.get("carrierName").in(filters.carrier())
                );
            }

            // Tracking Number (contains, case-insensitive)
            if (filters.trackingNumber() != null && !filters.trackingNumber().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("trackingNumber")),
                                "%" + filters.trackingNumber().toLowerCase() + "%"
                        )
                );
            }

            // Status (IN filter)
            if (filters.status() != null && !filters.status().isEmpty()) {
                predicates.add(
                        root.get("status").in(filters.status())
                );
            }

            // Shipment Delivery Type (IN filter)
            if (filters.shipmentDeliveryType() != null && !filters.shipmentDeliveryType().isEmpty()) {
                predicates.add(
                        root.get("shipmentDeliveryType").in(filters.shipmentDeliveryType())
                );
            }

            // Shipper Name (contains, case-insensitive)
            if (filters.shipperName() != null && !filters.shipperName().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("shipperName")),
                                "%" + filters.shipperName().toLowerCase() + "%"
                        )
                );
            }

            Path<BigDecimal> ratePath = root.get("rate");

            if (filters.rate().min() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                ratePath,
                                filters.rate().min()
                        )
                );
            }

            if (filters.rate().max() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                ratePath,
                                filters.rate().max()
                        )
                );
            }
        }

        boolean asc = sort.direction() == ShipmentSort.Direction.ASC;

        Path<Long> idPath = root.get("id");

        Path<? extends Comparable<?>> sortPath =
                root.get(sort.field().dbField());

        // Cursor filtering
        if (cursor != null) {
            Comparable<?> sortValue = cursor.id();

            Predicate primaryComparison = buildComparison(cb, sortPath, sortValue, asc);

            if (sort.field() == ShipmentSort.Field.ID) {
                predicates.add(primaryComparison);
            } else {
                Predicate tieBreaker = asc
                        ? cb.and(
                        cb.equal(sortPath, sortValue),
                        cb.greaterThan(idPath, cursor.id())
                )
                        : cb.and(
                        cb.equal(sortPath, sortValue),
                        cb.lessThan(idPath, cursor.id())
                );

                predicates.add(cb.or(primaryComparison, tieBreaker));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // ORDER BY (must mirror cursor logic)
        if (sort.field() == ShipmentSort.Field.ID) {
            cq.orderBy(
                    asc ? cb.asc(idPath) : cb.desc(idPath)
            );

        } else {
            cq.orderBy(
                asc ? List.of(cb.asc(sortPath), cb.asc(idPath)) : List.of(cb.desc(sortPath), cb.desc(idPath))
            );
        }

        return em.createQuery(cq)
                .setMaxResults(pageSize + 1)
                .getResultList();
    }

    /**
     * Converts cursor string value to proper Java type.
//     */
//    private Comparable<?> convertCursorValue(
//            String value,
//            ShipmentSort.Field field
//    ) {
//        return switch (field) {
//            case RATE -> new BigDecimal(value);
//            case ID -> Long.valueOf(value);
//        };
//    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<? super T>> Predicate buildComparison(
            CriteriaBuilder cb,
            Path<?> path,
            Comparable<?> value,
            boolean asc
    ) {
        Path<T> typedPath = (Path<T>) path;
        T typedValue = (T) value;

        return asc
                ? cb.greaterThan(typedPath, typedValue)
                : cb.lessThan(typedPath, typedValue);
    }
}

package com.ultraship.tms.repository;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.graphql.model.NumberRange;
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

        // always exclude deleted
        predicates.add(cb.equal(root.get("deleted"), false));

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

            NumberRange rate = filters.rate();

            if (rate != null && rate.min() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                ratePath,
                                rate.min()
                        )
                );
            }

            if (rate != null && rate.max() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                ratePath,
                                rate.max()
                        )
                );
            }

            Boolean isFlagged = filters.isFlagged();

            if (isFlagged != null) {
                predicates.add(
                    cb.equal(root.get("isFlagged"), isFlagged)
                );
            }
        }

        boolean asc = sort.direction() == ShipmentSort.Direction.ASC;

        Path<Long> idPath = root.get("id");

        Path<? extends Comparable<?>> sortPath =
                root.get(sort.field().dbField());

        // Cursor filtering
        if (cursor != null) {
            Comparable<?> sortValue = cursor.fieldValue();

            Predicate primaryComparison = buildComparison(cb, sortPath, sortValue, asc);
            predicates.add(primaryComparison);
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

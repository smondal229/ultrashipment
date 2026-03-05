package com.ultraship.tms.repository;

import com.ultraship.tms.domain.entity.ShipmentEntity;
import com.ultraship.tms.graphql.model.filter.ShipmentFilter;
import com.ultraship.tms.graphql.model.filter.ShipmentSort;
import com.ultraship.tms.graphql.utils.CursorPayload;

import java.util.List;

public interface ShipmentRepositoryCustom {
    List<ShipmentEntity> findAfterCursor(
        ShipmentFilter filters,
        CursorPayload cursor,
        int pageSize,
        ShipmentSort sort
    );
}

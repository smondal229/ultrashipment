package com.ultraship.tms.validations;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.domain.ShipmentStatus;
import com.ultraship.tms.exception.InvalidShipmentStateException;

import java.util.Map;
import java.util.Set;

public class StatusTransitionRule  implements ShipmentValidationRule {
    @Override
    public void validate(ShipmentValidationContext context) {
        Map<ShipmentStatus, Set<ShipmentStatus>> allowedTransitions = Map.of(
              ShipmentStatus.CREATED, Set.of(ShipmentStatus.PICKED_UP, ShipmentStatus.CANCELLED),
              ShipmentStatus.PICKED_UP, Set.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.CANCELLED),
              ShipmentStatus.IN_TRANSIT, Set.of(ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.CANCELLED),
              ShipmentStatus.OUT_FOR_DELIVERY, Set.of(ShipmentStatus.DELIVERED),
              ShipmentStatus.DELIVERED, Set.of(),
              ShipmentStatus.CANCELLED, Set.of()
        );

        ShipmentStatus currentStatus;
        ShipmentStatus newStatus = null;

        if (context.isCreate()) {
            currentStatus = context.create().status();
        } else {
            currentStatus = context.update().status();
            newStatus = context.existing().getStatus();
        }

        if (!allowedTransitions.get(currentStatus).contains(newStatus)) {
          throw new InvalidShipmentStateException(
                  String.format("Cannot transition from %s to %s", currentStatus, newStatus)
          );
        }
    }
}

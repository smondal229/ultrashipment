package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;

public interface ShipmentValidationRule {
    void validate(ShipmentValidationContext context);
}

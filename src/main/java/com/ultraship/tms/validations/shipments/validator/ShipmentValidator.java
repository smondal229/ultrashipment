package com.ultraship.tms.validations.shipments.validator;

import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import com.ultraship.tms.validations.shipments.rule.ShipmentValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShipmentValidator {

    private final List<ShipmentValidationRule> rules;

    public ShipmentValidator(List<ShipmentValidationRule> rules) {
        this.rules = rules;
    }

    public void validate(ShipmentValidationContext context) {
        rules.forEach(rule -> rule.validate(context));
    }
}

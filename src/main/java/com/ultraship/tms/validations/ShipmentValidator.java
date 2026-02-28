package com.ultraship.tms.validations;

import java.util.List;

public class ShipmentValidator {

    private final List<ShipmentValidationRule> rules;

    public ShipmentValidator(List<ShipmentValidationRule> rules) {
        this.rules = rules;
    }

    public void validate(ShipmentValidationContext context) {
        rules.forEach(rule -> rule.validate(context));
    }
}

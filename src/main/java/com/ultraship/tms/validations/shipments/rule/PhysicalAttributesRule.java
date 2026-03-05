package com.ultraship.tms.validations.shipments.rule;

import com.ultraship.tms.graphql.model.input.Dimensions;
import com.ultraship.tms.validations.shipments.context.ShipmentValidationContext;
import org.springframework.stereotype.Component;

@Component
public class PhysicalAttributesRule implements ShipmentValidationRule {

    @Override
    public void validate(ShipmentValidationContext context) {

        Dimensions input = context.isCreate()
                ? context.create().dimensions()
                : context.update().dimensions();

        if (input == null) return;

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
}
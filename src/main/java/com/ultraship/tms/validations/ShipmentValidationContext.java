package com.ultraship.tms.validations;

import com.ultraship.tms.domain.ShipmentEntity;
import com.ultraship.tms.graphql.model.ShipmentCreateInput;
import com.ultraship.tms.graphql.model.ShipmentUpdateInput;

public class ShipmentValidationContext {

    private final ShipmentEntity existing;
    private final ShipmentCreateInput createInput;
    private final ShipmentUpdateInput updateInput;

    private ShipmentValidationContext(
            ShipmentEntity existing,
            ShipmentCreateInput createInput,
            ShipmentUpdateInput updateInput
    ) {
        this.existing = existing;
        this.createInput = createInput;
        this.updateInput = updateInput;
    }

    public static ShipmentValidationContext forCreate(ShipmentCreateInput input) {
        return new ShipmentValidationContext(null, input, null);
    }

    public static ShipmentValidationContext forUpdate(
            ShipmentEntity existing,
            ShipmentUpdateInput input
    ) {
        return new ShipmentValidationContext(existing, null, input);
    }

    public boolean isCreate() {
        return existing == null;
    }

    public boolean isUpdate() {
        return existing != null;
    }

    public ShipmentEntity existing() { return existing; }
    public ShipmentCreateInput create() { return createInput; }
    public ShipmentUpdateInput update() { return updateInput; }
}

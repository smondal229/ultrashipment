package com.ultraship.tms.domain.enums;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {

    ADMIN(Set.of(
            Permission.CREATE_SHIPMENT,
            Permission.EDIT_SHIPMENT,
            Permission.DELETE_SHIPMENT,
            Permission.FLAG_SHIPMENT,
            Permission.VIEW_USERS,
            Permission.VIEW_SHIPMENT
    )),

    EMPLOYEE(Set.of(
            Permission.CREATE_SHIPMENT,
            Permission.EDIT_SHIPMENT,
            Permission.VIEW_SHIPMENT,
            Permission.FLAG_SHIPMENT
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

}

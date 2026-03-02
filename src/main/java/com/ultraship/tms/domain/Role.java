package com.ultraship.tms.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {

    ADMIN(Set.of(
            Permission.CREATE_SHIPMENT,
            Permission.EDIT_SHIPMENT,
            Permission.DELETE_SHIPMENT,
            Permission.VIEW_USERS
    )),

    EMPLOYEE(Set.of(
            Permission.VIEW_SHIPMENT
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

}

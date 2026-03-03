package com.ultraship.tms.graphql.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResetPasswordResponse {
    private String username;
    private boolean success;
}

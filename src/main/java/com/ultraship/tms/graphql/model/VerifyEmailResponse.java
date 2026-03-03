package com.ultraship.tms.graphql.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VerifyEmailResponse {
    private String verifiedEmail;
    private boolean success;
}

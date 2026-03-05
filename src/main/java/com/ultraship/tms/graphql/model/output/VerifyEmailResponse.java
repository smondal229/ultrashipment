package com.ultraship.tms.graphql.model.output;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VerifyEmailResponse {
    private String verifiedEmail;
    private boolean success;
}

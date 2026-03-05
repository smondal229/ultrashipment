package com.ultraship.tms.graphql.model.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerifyEmailResponse {
    private String verifiedEmail;
    private boolean success;
}

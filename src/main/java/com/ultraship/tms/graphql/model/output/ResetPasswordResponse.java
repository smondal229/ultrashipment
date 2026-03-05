package com.ultraship.tms.graphql.model.output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResetPasswordResponse {
    private String username;
    private boolean success;
}

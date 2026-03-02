package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupInput {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    private String password;
    private Role role = Role.EMPLOYEE;
}

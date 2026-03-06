package com.ultraship.tms.graphql.model.input;

import com.ultraship.tms.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SignupInput {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 8 characters long")
    private String password;

    @Setter
    private Role role = Role.EMPLOYEE;

    public void setUsername(String username) {
        this.username = username.trim().toLowerCase();
    }

    public void setPassword(String password) {
        this.password = password.trim().toLowerCase();
    }
}

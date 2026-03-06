package com.ultraship.tms.graphql.model.output;

import com.ultraship.tms.domain.enums.Role;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private boolean verified;
    private boolean active;
    @Nullable
    private List<String> authorities;
}

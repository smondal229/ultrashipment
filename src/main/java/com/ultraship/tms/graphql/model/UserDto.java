package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.Role;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private boolean verified;
    @Nullable
    private List<String> authorities;
}

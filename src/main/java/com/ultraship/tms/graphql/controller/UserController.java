package com.ultraship.tms.graphql.controller;

import com.ultraship.tms.graphql.model.UserDto;
import com.ultraship.tms.security.CustomUserPrincipal;
import com.ultraship.tms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public UserDto me(
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return userService.getUserDetails(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @QueryMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<UserDto> getByUserIds(@Argument List<Long> userIds) {
        return userService.getByUserIds(userIds);
    }
}
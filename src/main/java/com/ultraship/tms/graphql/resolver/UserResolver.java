package com.ultraship.tms.graphql.resolver;

import com.ultraship.tms.graphql.model.output.UserDto;
import com.ultraship.tms.ratelimiter.RateLimit;
import com.ultraship.tms.security.CustomUserPrincipal;
import com.ultraship.tms.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public UserDto me(
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return userService.getUserDetails(user);
    }

    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @QueryMapping
    @RateLimit(limit = 10, duration = 30)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<UserDto> getByUserIds(@Argument List<Long> userIds) {
        return userService.getByUserIds(userIds);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('DELETE_USERS')")
    public boolean changeActiveStatus(@Argument Long userId, @Argument Boolean activeStatus) {
        return userService.changeActiveStatus(userId, activeStatus);
    }
}
package com.ultraship.tms.service;

import com.ultraship.tms.domain.User;
import com.ultraship.tms.graphql.model.UserDto;
import com.ultraship.tms.repository.UserRepository;
import com.ultraship.tms.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserDetails(CustomUserPrincipal user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.isVerified(),
            user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
        );
    }

    public List<UserDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(u -> new UserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getRole(),
                        u.isVerified(),
                        null
                ))
                .toList();
    }

    public List<UserDto> getByUserIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .map(u -> new UserDto(
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.isVerified(),
                    null
                ))
                .toList();
    }
}

package com.ultraship.tms.security;

import com.ultraship.tms.domain.entity.User;
import com.ultraship.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add permissions (scopes)
        user.getRole().getPermissions()
                .forEach(permission ->
                        authorities.add(
                                new SimpleGrantedAuthority(permission.name())
                        )
                );


        return new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isVerified(),
                user.getRole(),
                authorities
        );
    }
}

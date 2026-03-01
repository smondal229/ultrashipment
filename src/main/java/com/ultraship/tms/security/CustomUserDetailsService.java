package com.ultraship.tms.security;

import com.ultraship.tms.domain.Role;
import com.ultraship.tms.domain.User;
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

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Add permissions
        if (user.getRole() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("DELETE_SHIPMENT"));
            authorities.add(new SimpleGrantedAuthority("VIEW_REPORT"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}

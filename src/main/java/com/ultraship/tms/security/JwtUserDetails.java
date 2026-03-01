package com.ultraship.tms.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JwtUserDetails {

    private String username;
    private List<String> authorities;
}
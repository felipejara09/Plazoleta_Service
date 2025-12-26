package com.pragma.powerup.infrastructure.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal {
    private final Long userId;
    private final String email;
    private final String role;
    private final String token;
}
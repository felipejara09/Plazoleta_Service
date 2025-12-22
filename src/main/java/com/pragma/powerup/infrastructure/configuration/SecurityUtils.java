package com.pragma.powerup.infrastructure.configuration;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Long getUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();

        if (!(principal instanceof AuthPrincipal)) {
            throw new IllegalStateException("Invalid authentication principal");
        }

        return ((AuthPrincipal) principal).getUserId();
    }
}

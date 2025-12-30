package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.ISecurityPort;
import com.pragma.powerup.infrastructure.configuration.AuthPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityAdapter implements ISecurityPort {

    @Override
    public Long getAuthenticatedUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();

        return principal.getUserId();

    }

    @Override
    public String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();
        return principal.getToken();
    }
}

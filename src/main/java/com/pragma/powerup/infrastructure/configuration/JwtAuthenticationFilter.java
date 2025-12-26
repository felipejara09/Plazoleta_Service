package com.pragma.powerup.infrastructure.configuration;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtValidator.getClaims(token);

            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            Long userId = getUserIdFromClaims(claims);

            AuthPrincipal principal = new AuthPrincipal(userId, email, role, token);

            var authority = new SimpleGrantedAuthority("ROLE_" + role);

            var auth = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(authority)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Long getUserIdFromClaims(Claims claims) {
        Object id = claims.get("id");
        if (id instanceof Integer) return ((Integer) id).longValue();
        if (id instanceof Long) return (Long) id;
        if (id instanceof String) return Long.parseLong((String) id);
        throw new IllegalArgumentException("Token does not contain a valid 'id' claim");
    }
}

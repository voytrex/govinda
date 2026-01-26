/*
 * Govinda ERP - JWT Authentication Filter
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that extracts JWT token from Authorization header and sets authentication context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            var claims = jwtTokenService.validateToken(token);

            if (claims != null) {
                UUID userId = UUID.fromString(claims.getSubject());
                var permissions = jwtTokenService.getPermissionsFromToken(token);

                var authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
                var authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                UUID tenantId = jwtTokenService.getTenantIdFromToken(token);
                if (tenantId != null) {
                    request.setAttribute("tenantId", tenantId);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

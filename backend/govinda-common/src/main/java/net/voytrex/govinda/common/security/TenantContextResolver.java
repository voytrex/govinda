/*
 * Govinda ERP - Tenant Context Resolver
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.UnauthorizedTenantAccessException;
import net.voytrex.govinda.common.infrastructure.persistence.JpaUserTenantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Resolves and validates tenant context from request headers or JWT token.
 * Ensures that:
 * 1. Tenant ID is present (from header or token)
 * 2. Authenticated user has access to the tenant
 */
@Component
public class TenantContextResolver implements HandlerInterceptor {
    private final JpaUserTenantRepository userTenantRepository;

    public TenantContextResolver(JpaUserTenantRepository userTenantRepository) {
        this.userTenantRepository = userTenantRepository;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui")
            || path.startsWith("/api-docs")
            || path.startsWith("/actuator")
            || path.equals("/")
            || path.equals("/api/v1/auth/login")) {
            return true;
        }

        UUID tenantIdFromHeader = parseTenantIdHeader(request.getHeader("X-Tenant-Id"));
        Object tenantAttr = request.getAttribute("tenantId");
        UUID tenantIdFromToken = tenantAttr instanceof UUID ? (UUID) tenantAttr : null;
        UUID tenantId = tenantIdFromHeader != null ? tenantIdFromHeader : tenantIdFromToken;

        if (tenantId == null) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return writeMissingTenant(response, "X-Tenant-Id header is required");
            }
            return writeMissingTenant(response, "Tenant context is required");
        }

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (!(principal instanceof UUID userId)) {
                return false;
            }
            var userTenant = userTenantRepository.findUserTenantAccess(userId, tenantId);
            if (userTenant == null) {
                throw new UnauthorizedTenantAccessException(tenantId);
            }
        }

        request.setAttribute("tenantId", tenantId);
        return true;
    }

    private UUID parseTenantIdHeader(String header) {
        if (header == null) {
            return null;
        }
        try {
            return UUID.fromString(header);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean writeMissingTenant(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"errorCode\":\"MISSING_TENANT_ID\",\"message\":\"" + message + "\"}"
        );
        return false;
    }
}

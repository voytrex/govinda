/*
 * Govinda ERP - Authentication Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.AuthenticationException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundByFieldException;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.model.UserStatus;
import net.voytrex.govinda.common.domain.repository.UserRepository;
import net.voytrex.govinda.common.infrastructure.persistence.JpaUserTenantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user authentication.
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JpaUserTenantRepository userTenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(
        UserRepository userRepository,
        JpaUserTenantRepository userTenantRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.userTenantRepository = userTenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param username Username
     * @param password Plain text password
     * @param tenantId Optional tenant ID - if not provided, uses user's default tenant
     * @return JWT token
     */
    @Transactional
    public String authenticate(String username, String password, UUID tenantId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthenticationException("User account is " + user.getStatus().name().toLowerCase());
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        UUID targetTenantId = tenantId != null ? tenantId : resolveDefaultTenantId(user);

        var userTenant = userTenantRepository.findUserTenantAccess(user.getId(), targetTenantId);
        if (userTenant == null) {
            throw new AuthenticationException("User does not have access to tenant " + targetTenantId);
        }

        user.updateLastLogin();
        userRepository.save(user);

        List<String> permissions = userTenant.getRole().getPermissions().stream()
            .map(permission -> permission.getCode())
            .toList();

        return jwtTokenService.generateToken(
            user.getId(),
            user.getUsername(),
            targetTenantId,
            permissions
        );
    }

    /**
     * Gets all tenants a user can access.
     */
    public List<UserTenantInfo> getUserTenants(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundByFieldException("User", "id", userId.toString()));

        return userTenantRepository.findByUserId(userId).stream()
            .map(ut -> new UserTenantInfo(
                ut.getTenant().getId(),
                ut.getTenant().getCode(),
                ut.getTenant().getName(),
                ut.getRole().getCode(),
                ut.getRole().getName(),
                ut.isDefault()
            ))
            .toList();
    }

    private UUID resolveDefaultTenantId(User user) {
        var defaultTenant = userTenantRepository.findByUserIdAndIsDefaultTrue(user.getId());
        if (defaultTenant == null) {
            List<net.voytrex.govinda.common.domain.model.UserTenant> allTenants =
                userTenantRepository.findByUserId(user.getId());
            if (!allTenants.isEmpty()) {
                defaultTenant = allTenants.get(0);
            }
        }
        if (defaultTenant == null || defaultTenant.getTenant() == null) {
            throw new AuthenticationException("User has no tenant access");
        }
        return defaultTenant.getTenant().getId();
    }
}

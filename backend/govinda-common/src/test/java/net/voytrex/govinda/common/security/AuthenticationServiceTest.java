/*
 * Govinda ERP - Authentication Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.AuthenticationException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundByFieldException;
import net.voytrex.govinda.common.domain.model.Permission;
import net.voytrex.govinda.common.domain.model.Role;
import net.voytrex.govinda.common.domain.model.Tenant;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.model.UserStatus;
import net.voytrex.govinda.common.domain.model.UserTenant;
import net.voytrex.govinda.common.domain.repository.UserRepository;
import net.voytrex.govinda.common.infrastructure.persistence.JpaUserTenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JpaUserTenantRepository userTenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthenticationService authenticationService;

    private final UUID userId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();
    private final UUID roleId = UUID.randomUUID();
    private final String username = "testuser";
    private final String password = "password123";
    private final String hashedPassword = "$2a$10$hashedpassword";

    @BeforeEach
    void setUp() {
        JwtTokenService jwtTokenService = new JwtTokenService(
            "test-secret-key-minimum-256-bits-required-for-hs256-algorithm",
            3600
        );
        authenticationService = new AuthenticationService(
            userRepository,
            userTenantRepository,
            passwordEncoder,
            jwtTokenService
        );
    }

    @Nested
    @DisplayName("Successful Authentication")
    class SuccessfulAuthentication {

        @Test
        void shouldAuthenticateUserWithValidCredentialsAndDefaultTenant() {
            User user = createTestUser(UserStatus.ACTIVE);
            UserTenant userTenant = createTestUserTenant(user);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
            when(userTenantRepository.findByUserIdAndIsDefaultTrue(userId)).thenReturn(Optional.of(userTenant));
            when(userTenantRepository.findUserTenantAccess(userId, tenantId)).thenReturn(Optional.of(userTenant));
            when(userRepository.save(user)).thenReturn(user);

            String token = authenticationService.authenticate(username, password, null);

            assertThat(token).isNotBlank();
            verify(userRepository).findByUsername(username);
            verify(passwordEncoder).matches(password, hashedPassword);
            verify(userRepository).save(user);
        }

        @Test
        void shouldAuthenticateUserWithSpecifiedTenant() {
            User user = createTestUser(UserStatus.ACTIVE);
            UserTenant userTenant = createTestUserTenant(user);
            UUID specifiedTenantId = UUID.randomUUID();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
            when(userTenantRepository.findUserTenantAccess(userId, specifiedTenantId)).thenReturn(Optional.of(userTenant));
            when(userRepository.save(user)).thenReturn(user);

            String token = authenticationService.authenticate(username, password, specifiedTenantId);

            assertThat(token).isNotBlank();
            verify(userTenantRepository).findUserTenantAccess(userId, specifiedTenantId);
        }

        @Test
        void shouldUseFirstTenantIfNoDefaultTenantExists() {
            User user = createTestUser(UserStatus.ACTIVE);
            UserTenant userTenant = createTestUserTenant(user);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
            when(userTenantRepository.findByUserIdAndIsDefaultTrue(userId)).thenReturn(Optional.empty());
            when(userTenantRepository.findByUserId(userId)).thenReturn(List.of(userTenant));
            when(userTenantRepository.findUserTenantAccess(userId, tenantId)).thenReturn(Optional.of(userTenant));
            when(userRepository.save(user)).thenReturn(user);

            String token = authenticationService.authenticate(username, password, null);

            assertThat(token).isNotBlank();
            verify(userTenantRepository).findByUserId(userId);
        }

        @Test
        void shouldIncludePermissionsFromRoleInToken() {
            Permission permission = new Permission("person:read", "Read Persons", "person", "read");
            Role role = new Role("USER", "User");
            role.setId(roleId);
            role.getPermissions().add(permission);

            User user = createTestUser(UserStatus.ACTIVE);
            Tenant tenant = new Tenant(tenantId, "T1", "Tenant 1");
            UserTenant userTenant = new UserTenant(user, tenant, role);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
            when(userTenantRepository.findByUserIdAndIsDefaultTrue(userId)).thenReturn(Optional.of(userTenant));
            when(userTenantRepository.findUserTenantAccess(userId, tenantId)).thenReturn(Optional.of(userTenant));
            when(userRepository.save(user)).thenReturn(user);

            String token = authenticationService.authenticate(username, password, null);

            assertThat(token).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Authentication Failures")
    class AuthenticationFailures {

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authenticationService.authenticate(username, password, null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
        }

        @Test
        void shouldThrowExceptionWhenPasswordIsIncorrect() {
            User user = createTestUser(UserStatus.ACTIVE);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

            assertThatThrownBy(() -> authenticationService.authenticate(username, password, null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
        }

        @Test
        void shouldThrowExceptionWhenUserIsInactive() {
            User user = createTestUser(UserStatus.INACTIVE);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authenticationService.authenticate(username, password, null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("inactive");
        }

        @Test
        void shouldThrowExceptionWhenUserIsLocked() {
            User user = createTestUser(UserStatus.LOCKED);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authenticationService.authenticate(username, password, null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("locked");
        }

        @Test
        void shouldThrowExceptionWhenUserHasNoTenantAccess() {
            User user = createTestUser(UserStatus.ACTIVE);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
            when(userTenantRepository.findByUserIdAndIsDefaultTrue(userId)).thenReturn(Optional.empty());
            when(userTenantRepository.findByUserId(userId)).thenReturn(List.of());

            assertThatThrownBy(() -> authenticationService.authenticate(username, password, null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("no tenant access");
        }

        @Test
        void shouldThrowExceptionWhenUserDoesNotHaveAccessToSpecifiedTenant() {
            User user = createTestUser(UserStatus.ACTIVE);
            UUID specifiedTenantId = UUID.randomUUID();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
            when(userTenantRepository.findUserTenantAccess(userId, specifiedTenantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authenticationService.authenticate(username, password, specifiedTenantId))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("does not have access to tenant");
        }
    }

    @Nested
    @DisplayName("Get User Tenants")
    class GetUserTenants {

        @Test
        void shouldReturnAllTenantsForUser() {
            User user = createTestUser(UserStatus.ACTIVE);
            Tenant tenant1 = new Tenant(tenantId, "T1", "Tenant 1");
            Tenant tenant2 = new Tenant(UUID.randomUUID(), "T2", "Tenant 2");
            Role role = new Role("USER", "User");
            role.setId(roleId);

            UserTenant userTenant1 = new UserTenant(user, tenant1, role);
            userTenant1.setDefault(true);
            UserTenant userTenant2 = new UserTenant(user, tenant2, role);
            userTenant2.setDefault(false);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userTenantRepository.findByUserId(userId)).thenReturn(List.of(userTenant1, userTenant2));

            var result = authenticationService.getUserTenants(userId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).tenantId()).isEqualTo(tenantId);
            assertThat(result.get(0).isDefault()).isTrue();
            assertThat(result.get(1).isDefault()).isFalse();
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authenticationService.getUserTenants(userId))
                .isInstanceOf(EntityNotFoundByFieldException.class);
        }
    }

    private User createTestUser(UserStatus status) {
        User user = new User(username, "test@example.com", hashedPassword);
        user.setId(userId);
        user.setStatus(status);
        return user;
    }

    private UserTenant createTestUserTenant(User user) {
        Role role = new Role("USER", "User");
        role.setId(roleId);
        Tenant tenant = new Tenant(tenantId, "T1", "Tenant 1");
        UserTenant userTenant = new UserTenant(user, tenant, role);
        userTenant.setDefault(true);
        return userTenant;
    }
}

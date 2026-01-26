/*
 * Govinda ERP - UserTenant Domain Model Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTenantTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();
    private final UUID roleId = UUID.randomUUID();

    @Nested
    @DisplayName("UserTenant Creation")
    class UserTenantCreation {

        @Test
        void shouldCreateUserTenantRelationship() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setId(userId);

            Tenant tenant = new Tenant(tenantId, "TENANT1", "Test Tenant");

            Role role = new Role("USER", "User");
            role.setId(roleId);

            UserTenant userTenant = new UserTenant(user, tenant, role);
            userTenant.setDefault(true);

            assertThat(userTenant.getUser().getId()).isEqualTo(userId);
            assertThat(userTenant.getTenant().getId()).isEqualTo(tenantId);
            assertThat(userTenant.getRole().getId()).isEqualTo(roleId);
            assertThat(userTenant.isDefault()).isTrue();
        }
    }

    @Nested
    @DisplayName("Permission Checks")
    class PermissionChecks {

        @Test
        void shouldDelegatePermissionCheckToRole() {
            Permission permission = new Permission("person:read", "Read Persons", "person", "read");

            Role role = new Role("USER", "User");
            role.getPermissions().add(permission);

            User user = new User("testuser", "test@example.com", "hashed_password");
            Tenant tenant = new Tenant(tenantId, "TENANT1", "Test Tenant");

            UserTenant userTenant = new UserTenant(user, tenant, role);

            assertThat(userTenant.hasPermission("person:read")).isTrue();
            assertThat(userTenant.hasPermission("person", "read")).isTrue();
            assertThat(userTenant.hasPermission("person", "write")).isFalse();
        }
    }
}

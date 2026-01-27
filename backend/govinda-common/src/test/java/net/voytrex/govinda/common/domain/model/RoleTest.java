/*
 * Govinda ERP - Role Domain Model Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RoleTest {

    private final UUID roleId = UUID.randomUUID();

    @Nested
    @DisplayName("Role Creation")
    class RoleCreation {

        @Test
        void shouldCreateRoleWithValidData() {
            Role role = new Role("ADMIN", "Administrator");
            role.setId(roleId);
            role.setDescription("Full system access");

            assertThat(role.getId()).isEqualTo(roleId);
            assertThat(role.getCode()).isEqualTo("ADMIN");
            assertThat(role.getName()).isEqualTo("Administrator");
            assertThat(role.getDescription()).isEqualTo("Full system access");
        }

        @Test
        void shouldAllowUpdatingCoreFields() {
            Role role = new Role("USER", "User");
            Instant createdAt = Instant.now().minusSeconds(120);
            Instant updatedAt = Instant.now();

            role.setCode("MANAGER");
            role.setName("Manager");
            role.setDescription("Manages users");
            role.setCreatedAt(createdAt);
            role.setUpdatedAt(updatedAt);

            assertThat(role.getCode()).isEqualTo("MANAGER");
            assertThat(role.getName()).isEqualTo("Manager");
            assertThat(role.getDescription()).isEqualTo("Manages users");
            assertThat(role.getCreatedAt()).isEqualTo(createdAt);
            assertThat(role.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("Permission Checks")
    class PermissionChecks {

        @Test
        void shouldReturnTrueWhenRoleHasPermissionByCode() {
            Permission permission = new Permission("person:read", "Read Persons", "person", "read");

            Role role = new Role("USER", "User");
            role.getPermissions().add(permission);

            assertThat(role.hasPermission("person:read")).isTrue();
        }

        @Test
        void shouldReturnFalseWhenRoleDoesNotHavePermission() {
            Role role = new Role("READONLY", "Read Only");

            assertThat(role.hasPermission("person:write")).isFalse();
        }

        @Test
        void shouldReturnTrueWhenRoleHasPermissionByResourceAndAction() {
            Permission permission = new Permission("person:read", "Read Persons", "person", "read");

            Role role = new Role("USER", "User");
            role.getPermissions().add(permission);

            assertThat(role.hasPermission("person", "read")).isTrue();
        }

        @Test
        void shouldReturnFalseWhenResourceMatchesButActionDoesNot() {
            Permission permission = new Permission("person:read", "Read Persons", "person", "read");

            Role role = new Role("READONLY", "Read Only");
            role.getPermissions().add(permission);

            assertThat(role.hasPermission("person", "write")).isFalse();
        }

        @Test
        void shouldReturnFalseWhenActionMatchesButResourceDoesNot() {
            Permission permission = new Permission("person:read", "Read Persons", "person", "read");

            Role role = new Role("USER", "User");
            role.getPermissions().add(permission);

            assertThat(role.hasPermission("contract", "read")).isFalse();
        }

        @Test
        void shouldAllowReplacingPermissionsSet() {
            Role role = new Role("USER", "User");
            role.setPermissions(Set.of());

            assertThat(role.getPermissions()).isEmpty();
        }
    }
}

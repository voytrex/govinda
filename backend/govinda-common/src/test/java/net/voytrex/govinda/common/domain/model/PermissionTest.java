/*
 * Govinda ERP - Permission Domain Model Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class PermissionTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        void shouldCreatePermissionWithRequiredFields() {
            Permission permission = new Permission("USER_READ", "Read Users", "USER", "READ");

            assertThat(permission.getId()).isNotNull();
            assertThat(permission.getCode()).isEqualTo("USER_READ");
            assertThat(permission.getName()).isEqualTo("Read Users");
            assertThat(permission.getResource()).isEqualTo("USER");
            assertThat(permission.getAction()).isEqualTo("READ");
            assertThat(permission.getDescription()).isNull();
            assertThat(permission.getCreatedAt()).isNotNull();
        }

        @Test
        void shouldAllowSettingOptionalDescription() {
            Permission permission = new Permission("USER_WRITE", "Write Users", "USER", "WRITE");
            permission.setDescription("Allows editing user profiles");

            assertThat(permission.getDescription()).isEqualTo("Allows editing user profiles");
        }

        @Test
        void shouldAllowUpdatingFields() {
            Permission permission = new Permission("CODE", "Name", "RESOURCE", "ACTION");
            UUID id = UUID.randomUUID();
            Instant createdAt = Instant.now();

            permission.setId(id);
            permission.setCode("UPDATED");
            permission.setName("Updated Name");
            permission.setResource("UPDATED_RESOURCE");
            permission.setAction("UPDATED_ACTION");
            permission.setCreatedAt(createdAt);

            assertThat(permission.getId()).isEqualTo(id);
            assertThat(permission.getCode()).isEqualTo("UPDATED");
            assertThat(permission.getName()).isEqualTo("Updated Name");
            assertThat(permission.getResource()).isEqualTo("UPDATED_RESOURCE");
            assertThat(permission.getAction()).isEqualTo("UPDATED_ACTION");
            assertThat(permission.getCreatedAt()).isEqualTo(createdAt);
        }
    }
}

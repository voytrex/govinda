/*
 * Govinda ERP - Tenant Domain Model Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class TenantTest {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        void shouldCreateTenantWithDefaults() {
            UUID tenantId = UUID.randomUUID();

            Tenant tenant = new Tenant(tenantId, "TENANT", "Tenant Name");

            assertThat(tenant.getId()).isEqualTo(tenantId);
            assertThat(tenant.getCode()).isEqualTo("TENANT");
            assertThat(tenant.getName()).isEqualTo("Tenant Name");
            assertThat(tenant.getStatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("Mutators")
    class Mutators {

        @Test
        void shouldUpdateTenantFields() {
            Tenant tenant = new Tenant(UUID.randomUUID(), "OLD", "Old Name");

            tenant.setCode("NEW");
            tenant.setName("New Name");
            tenant.setStatus("INACTIVE");

            assertThat(tenant.getCode()).isEqualTo("NEW");
            assertThat(tenant.getName()).isEqualTo("New Name");
            assertThat(tenant.getStatus()).isEqualTo("INACTIVE");
        }
    }
}

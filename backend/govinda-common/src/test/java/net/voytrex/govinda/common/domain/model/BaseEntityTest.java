/*
 * Govinda ERP - Base Entity Domain Model Tests
 * Copyright 2024 Voytrex
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
class BaseEntityTest {

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        void shouldInitializeWithDefaults() {
            UUID tenantId = UUID.randomUUID();
            TestEntity entity = new TestEntity(tenantId);

            assertThat(entity.getId()).isNotNull();
            assertThat(entity.getTenantId()).isEqualTo(tenantId);
            assertThat(entity.getCreatedAt()).isNotNull();
            assertThat(entity.getUpdatedAt()).isNotNull();
            assertThat(entity.getVersion()).isZero();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        void shouldCompareEntitiesById() {
            UUID tenantId = UUID.randomUUID();
            UUID sharedId = UUID.randomUUID();

            TestEntity first = new TestEntity(tenantId);
            TestEntity second = new TestEntity(tenantId);
            first.setIdPublic(sharedId);
            second.setIdPublic(sharedId);

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void shouldNotEqualDifferentIds() {
            UUID tenantId = UUID.randomUUID();
            TestEntity first = new TestEntity(tenantId);
            TestEntity second = new TestEntity(tenantId);

            assertThat(first).isNotEqualTo(second);
        }
    }

    @Nested
    @DisplayName("Mutation")
    class Mutation {

        @Test
        void shouldAllowUpdatingTimestampsAndVersion() {
            TestEntity entity = new TestEntity(UUID.randomUUID());
            Instant updatedAt = Instant.now().plusSeconds(10);

            entity.setUpdatedAt(updatedAt);
            entity.setVersion(2L);

            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(entity.getVersion()).isEqualTo(2L);
        }
    }

    private static final class TestEntity extends BaseEntity {
        private TestEntity(UUID tenantId) {
            super(tenantId);
        }

        private void setIdPublic(UUID id) {
            setId(id);
        }
    }
}

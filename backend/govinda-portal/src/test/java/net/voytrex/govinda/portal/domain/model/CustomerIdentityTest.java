/*
 * Govinda ERP - Customer Identity Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerIdentityTest {

    @Test
    @DisplayName("should initialize defaults when created")
    void should_initializeDefaults_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var subject = "portal-subject-123";

        var identity = new CustomerIdentity(tenantId, personId, subject);

        assertThat(identity.getId()).isNotNull();
        assertThat(identity.getTenantId()).isEqualTo(tenantId);
        assertThat(identity.getPersonId()).isEqualTo(personId);
        assertThat(identity.getSubject()).isEqualTo(subject);
        assertThat(identity.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should reject null tenant id")
    void should_rejectNullTenantId_when_created() {
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new CustomerIdentity(null, personId, "portal-subject-123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tenant ID must not be null");
    }

    @Test
    @DisplayName("should reject null person id")
    void should_rejectNullPersonId_when_created() {
        var tenantId = UUID.randomUUID();

        assertThatThrownBy(() -> new CustomerIdentity(tenantId, null, "portal-subject-123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Person ID must not be null");
    }

    @Test
    @DisplayName("should reject blank subject")
    void should_rejectBlankSubject_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new CustomerIdentity(tenantId, personId, " "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Subject must not be blank");
    }
}

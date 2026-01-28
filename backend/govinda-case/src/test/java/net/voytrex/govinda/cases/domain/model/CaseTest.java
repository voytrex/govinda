/*
 * Govinda ERP - Case Entity Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseTest {

    @Test
    @DisplayName("should initialize defaults when created")
    void should_initializeDefaults_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        var caseRecord = new Case(
            tenantId,
            personId,
            CaseType.ADDRESS_CHANGE,
            "Address update",
            "New address effective 2026-03-01"
        );

        assertThat(caseRecord.getId()).isNotNull();
        assertThat(caseRecord.getTenantId()).isEqualTo(tenantId);
        assertThat(caseRecord.getPersonId()).isEqualTo(personId);
        assertThat(caseRecord.getType()).isEqualTo(CaseType.ADDRESS_CHANGE);
        assertThat(caseRecord.getStatus()).isEqualTo(CaseStatus.OPEN);
        assertThat(caseRecord.getCreatedAt()).isNotNull();
        assertThat(caseRecord.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should reject null tenant id")
    void should_rejectNullTenantId_when_created() {
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new Case(
            null,
            personId,
            CaseType.ADDRESS_CHANGE,
            "Address update",
            "New address effective 2026-03-01"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tenant ID must not be null");
    }

    @Test
    @DisplayName("should reject null person id")
    void should_rejectNullPersonId_when_created() {
        var tenantId = UUID.randomUUID();

        assertThatThrownBy(() -> new Case(
            tenantId,
            null,
            CaseType.ADDRESS_CHANGE,
            "Address update",
            "New address effective 2026-03-01"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Person ID must not be null");
    }

    @Test
    @DisplayName("should reject null case type")
    void should_rejectNullType_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new Case(
            tenantId,
            personId,
            null,
            "Address update",
            "New address effective 2026-03-01"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Case type must not be null");
    }

    @Test
    @DisplayName("should reject blank subject")
    void should_rejectBlankSubject_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new Case(
            tenantId,
            personId,
            CaseType.ADDRESS_CHANGE,
            " ",
            "New address effective 2026-03-01"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Subject must not be blank");
    }
}

/*
 * Govinda ERP - Portal Document Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PortalDocumentTest {

    @Test
    @DisplayName("should initialize defaults when created")
    void should_initializeDefaults_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        var document = new PortalDocument(
            tenantId,
            personId,
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        );

        assertThat(document.getId()).isNotNull();
        assertThat(document.getTenantId()).isEqualTo(tenantId);
        assertThat(document.getPersonId()).isEqualTo(personId);
        assertThat(document.getType()).isEqualTo(PortalDocumentType.CONTRACT);
        assertThat(document.getStatus()).isEqualTo(PortalDocumentStatus.AVAILABLE);
        assertThat(document.getTitle()).isEqualTo("Policy Contract 2026");
        assertThat(document.getStorageKey()).isEqualTo("tenant/2026/contracts/policy.pdf");
        assertThat(document.getCreatedAt()).isNotNull();
        assertThat(document.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should reject null tenant id")
    void should_rejectNullTenantId_when_created() {
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new PortalDocument(
            null,
            personId,
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tenant ID must not be null");
    }

    @Test
    @DisplayName("should reject null person id")
    void should_rejectNullPersonId_when_created() {
        var tenantId = UUID.randomUUID();

        assertThatThrownBy(() -> new PortalDocument(
            tenantId,
            null,
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Person ID must not be null");
    }

    @Test
    @DisplayName("should reject null document type")
    void should_rejectNullType_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new PortalDocument(
            tenantId,
            personId,
            null,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document type must not be null");
    }

    @Test
    @DisplayName("should reject null document status")
    void should_rejectNullStatus_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new PortalDocument(
            tenantId,
            personId,
            PortalDocumentType.CONTRACT,
            null,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document status must not be null");
    }

    @Test
    @DisplayName("should reject blank title")
    void should_rejectBlankTitle_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new PortalDocument(
            tenantId,
            personId,
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            " ",
            "tenant/2026/contracts/policy.pdf"
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Title must not be blank");
    }

    @Test
    @DisplayName("should reject blank storage key")
    void should_rejectBlankStorageKey_when_created() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();

        assertThatThrownBy(() -> new PortalDocument(
            tenantId,
            personId,
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            " "
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Storage key must not be blank");
    }
}

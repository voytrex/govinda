/*
 * Govinda ERP - JpaPortalDocumentRepositoryAdapter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalDocument;
import net.voytrex.govinda.portal.domain.model.PortalDocumentStatus;
import net.voytrex.govinda.portal.domain.model.PortalDocumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JpaPortalDocumentRepositoryAdapterTest {

    @Test
    @DisplayName("should list documents by tenant and person")
    void should_listDocuments_when_tenantAndPersonProvided() {
        var repository = Mockito.mock(SpringDataPortalDocumentRepository.class);
        var adapter = new JpaPortalDocumentRepositoryAdapter(repository);
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

        when(repository.findByTenantIdAndPersonId(tenantId, personId))
            .thenReturn(List.of(document));

        var result = adapter.findByTenantIdAndPersonId(tenantId, personId);

        assertThat(result).containsExactly(document);
        verify(repository).findByTenantIdAndPersonId(tenantId, personId);
    }

    @Test
    @DisplayName("should find document by id, tenant, and person")
    void should_findDocument_when_idTenantPersonProvided() {
        var repository = Mockito.mock(SpringDataPortalDocumentRepository.class);
        var adapter = new JpaPortalDocumentRepositoryAdapter(repository);
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var document = new PortalDocument(
            tenantId,
            personId,
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        );
        document.setId(documentId);

        when(repository.findByIdAndTenantIdAndPersonId(documentId, tenantId, personId))
            .thenReturn(Optional.of(document));

        var result = adapter.findByIdAndTenantIdAndPersonId(documentId, tenantId, personId);

        assertThat(result).contains(document);
        verify(repository).findByIdAndTenantIdAndPersonId(documentId, tenantId, personId);
    }

    @Test
    @DisplayName("should save document via Spring Data repository")
    void should_saveDocument_when_called() {
        var repository = Mockito.mock(SpringDataPortalDocumentRepository.class);
        var adapter = new JpaPortalDocumentRepositoryAdapter(repository);
        var document = new PortalDocument(
            UUID.randomUUID(),
            UUID.randomUUID(),
            PortalDocumentType.CONTRACT,
            PortalDocumentStatus.AVAILABLE,
            "Policy Contract 2026",
            "tenant/2026/contracts/policy.pdf"
        );

        when(repository.save(document)).thenReturn(document);

        var result = adapter.save(document);

        assertThat(result).isSameAs(document);
        verify(repository).save(document);
    }
}

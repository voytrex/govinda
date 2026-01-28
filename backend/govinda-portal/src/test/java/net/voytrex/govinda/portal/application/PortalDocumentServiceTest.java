/*
 * Govinda ERP - Portal Document Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalDocument;
import net.voytrex.govinda.portal.domain.model.PortalDocumentStatus;
import net.voytrex.govinda.portal.domain.model.PortalDocumentType;
import net.voytrex.govinda.portal.domain.repository.PortalDocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PortalDocumentServiceTest {

    @Mock
    private PortalDocumentRepository portalDocumentRepository;

    @Test
    @DisplayName("should list documents for person")
    void should_listDocuments_when_personExists() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var documents = List.of(
            new PortalDocument(
                tenantId,
                personId,
                PortalDocumentType.CONTRACT,
                PortalDocumentStatus.AVAILABLE,
                "Policy Contract 2026",
                "storage/contract-2026.pdf"
            )
        );

        when(portalDocumentRepository.findByTenantIdAndPersonId(tenantId, personId))
            .thenReturn(documents);

        var service = new PortalDocumentService(portalDocumentRepository);

        var result = service.listDocuments(tenantId, personId);

        assertThat(result).hasSize(1);
        verify(portalDocumentRepository).findByTenantIdAndPersonId(tenantId, personId);
    }

    @Test
    @DisplayName("should return document when found")
    void should_returnDocument_when_found() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var documentId = UUID.randomUUID();
        var document = new PortalDocument(
            tenantId,
            personId,
            PortalDocumentType.INVOICE,
            PortalDocumentStatus.AVAILABLE,
            "Invoice 2026-01",
            "storage/invoice-2026-01.pdf"
        );
        document.setId(documentId);

        when(portalDocumentRepository.findByIdAndTenantIdAndPersonId(documentId, tenantId, personId))
            .thenReturn(Optional.of(document));

        var service = new PortalDocumentService(portalDocumentRepository);

        var result = service.getDocument(tenantId, personId, documentId);

        assertThat(result).isEqualTo(document);
        verify(portalDocumentRepository).findByIdAndTenantIdAndPersonId(documentId, tenantId, personId);
    }
}

/*
 * Govinda ERP - Portal Document Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.portal.domain.model.PortalDocument;
import net.voytrex.govinda.portal.domain.repository.PortalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortalDocumentService {
    private final PortalDocumentRepository portalDocumentRepository;

    public PortalDocumentService(PortalDocumentRepository portalDocumentRepository) {
        this.portalDocumentRepository = portalDocumentRepository;
    }

    public List<PortalDocument> listDocuments(UUID tenantId, UUID personId) {
        return portalDocumentRepository.findByTenantIdAndPersonId(tenantId, personId);
    }

    public PortalDocument getDocument(UUID tenantId, UUID personId, UUID documentId) {
        return portalDocumentRepository.findByIdAndTenantIdAndPersonId(documentId, tenantId, personId)
            .orElseThrow(() -> new EntityNotFoundException("PortalDocument", documentId));
    }
}

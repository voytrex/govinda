/*
 * Govinda ERP - JPA Portal Document Repository Implementation
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalDocument;
import net.voytrex.govinda.portal.domain.repository.PortalDocumentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPortalDocumentRepositoryAdapter implements PortalDocumentRepository {
    private final SpringDataPortalDocumentRepository portalDocumentRepository;

    public JpaPortalDocumentRepositoryAdapter(SpringDataPortalDocumentRepository portalDocumentRepository) {
        this.portalDocumentRepository = portalDocumentRepository;
    }

    @Override
    public List<PortalDocument> findByTenantIdAndPersonId(UUID tenantId, UUID personId) {
        return portalDocumentRepository.findByTenantIdAndPersonId(tenantId, personId);
    }

    @Override
    public Optional<PortalDocument> findByIdAndTenantIdAndPersonId(UUID id, UUID tenantId, UUID personId) {
        return portalDocumentRepository.findByIdAndTenantIdAndPersonId(id, tenantId, personId);
    }

    @Override
    public PortalDocument save(PortalDocument document) {
        return portalDocumentRepository.save(document);
    }
}

/*
 * Govinda ERP - Spring Data Portal Document Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPortalDocumentRepository extends JpaRepository<PortalDocument, UUID> {
    List<PortalDocument> findByTenantIdAndPersonId(UUID tenantId, UUID personId);
    Optional<PortalDocument> findByIdAndTenantIdAndPersonId(UUID id, UUID tenantId, UUID personId);
}

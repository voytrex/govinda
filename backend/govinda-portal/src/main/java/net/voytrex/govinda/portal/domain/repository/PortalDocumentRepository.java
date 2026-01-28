/*
 * Govinda ERP - Portal Document Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalDocument;

public interface PortalDocumentRepository {
    List<PortalDocument> findByTenantIdAndPersonId(UUID tenantId, UUID personId);
    Optional<PortalDocument> findByIdAndTenantIdAndPersonId(UUID id, UUID tenantId, UUID personId);
    PortalDocument save(PortalDocument document);
}

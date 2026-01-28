/*
 * Govinda ERP - Portal Case Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.domain.repository;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCase;

public interface PortalCaseRepository {
    PortalCase save(PortalCase portalCase);
    List<PortalCase> findByTenantIdAndPersonId(UUID tenantId, UUID personId);
}

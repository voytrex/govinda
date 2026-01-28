/*
 * Govinda ERP - Spring Data Portal Case Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPortalCaseRepository extends JpaRepository<PortalCase, UUID> {
    List<PortalCase> findByTenantIdAndPersonId(UUID tenantId, UUID personId);
}

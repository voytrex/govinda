/*
 * Govinda ERP - Spring Data Case Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCaseRepository extends JpaRepository<Case, UUID> {
    List<Case> findByTenantIdAndPersonId(UUID tenantId, UUID personId);
}

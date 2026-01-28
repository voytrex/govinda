/*
 * Govinda ERP - Case Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.domain.repository;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.Case;

public interface CaseRepository {
    Case save(Case caseEntity);
    List<Case> findByTenantIdAndPersonId(UUID tenantId, UUID personId);
}

/*
 * Govinda ERP - JPA Case Repository Implementation
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.Case;
import net.voytrex.govinda.cases.domain.repository.CaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCaseRepositoryAdapter implements CaseRepository {
    private final SpringDataCaseRepository caseRepository;

    public JpaCaseRepositoryAdapter(SpringDataCaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public Case save(Case caseEntity) {
        return caseRepository.save(caseEntity);
    }

    @Override
    public List<Case> findByTenantIdAndPersonId(UUID tenantId, UUID personId) {
        return caseRepository.findByTenantIdAndPersonId(tenantId, personId);
    }
}

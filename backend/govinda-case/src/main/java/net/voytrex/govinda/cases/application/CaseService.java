/*
 * Govinda ERP - Case Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.application;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.Case;
import net.voytrex.govinda.cases.domain.repository.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CaseService {
    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public Case createCase(CreateCaseCommand command) {
        var caseEntity = new Case(
            command.tenantId(),
            command.personId(),
            command.type(),
            command.subject(),
            command.description()
        );
        return caseRepository.save(caseEntity);
    }

    @Transactional(readOnly = true)
    public List<Case> listCases(UUID tenantId, UUID personId) {
        return caseRepository.findByTenantIdAndPersonId(tenantId, personId);
    }
}

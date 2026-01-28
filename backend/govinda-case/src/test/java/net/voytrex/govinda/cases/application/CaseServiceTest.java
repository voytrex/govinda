/*
 * Govinda ERP - Case Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.Case;
import net.voytrex.govinda.cases.domain.model.CaseType;
import net.voytrex.govinda.cases.domain.repository.CaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Test
    @DisplayName("should create case when request is valid")
    void should_createCase_when_requestIsValid() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var command = new CreateCaseCommand(
            tenantId,
            personId,
            CaseType.ADDRESS_CHANGE,
            "Moving to new address",
            "New address from 2026-03-01"
        );

        when(caseRepository.save(any(Case.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var service = new CaseService(caseRepository);

        var result = service.createCase(command);

        assertThat(result.getTenantId()).isEqualTo(tenantId);
        assertThat(result.getPersonId()).isEqualTo(personId);
        assertThat(result.getType()).isEqualTo(CaseType.ADDRESS_CHANGE);
        assertThat(result.getSubject()).isEqualTo("Moving to new address");
        assertThat(result.getDescription()).isEqualTo("New address from 2026-03-01");
        assertThat(result.getStatus().name()).isEqualTo("OPEN");
        verify(caseRepository).save(any(Case.class));
    }

    @Test
    @DisplayName("should list cases for person")
    void should_listCases_when_personExists() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var cases = List.of(
            new Case(
                tenantId,
                personId,
                CaseType.ADDRESS_CHANGE,
                "Address change",
                "Moving to new city"
            )
        );

        when(caseRepository.findByTenantIdAndPersonId(tenantId, personId))
            .thenReturn(cases);

        var service = new CaseService(caseRepository);

        var result = service.listCases(tenantId, personId);

        assertThat(result).hasSize(1);
        verify(caseRepository).findByTenantIdAndPersonId(tenantId, personId);
    }
}

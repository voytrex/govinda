/*
 * Govinda ERP - JpaCaseRepositoryAdapter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.Case;
import net.voytrex.govinda.cases.domain.model.CaseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JpaCaseRepositoryAdapterTest {

    @Test
    @DisplayName("should save case via Spring Data repository")
    void should_saveCase_when_called() {
        var repository = Mockito.mock(SpringDataCaseRepository.class);
        var adapter = new JpaCaseRepositoryAdapter(repository);
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var caseRecord = new Case(
            tenantId,
            personId,
            CaseType.ADDRESS_CHANGE,
            "Address update",
            "New address effective 2026-03-01"
        );

        when(repository.save(caseRecord)).thenReturn(caseRecord);

        var result = adapter.save(caseRecord);

        assertThat(result).isSameAs(caseRecord);
        verify(repository).save(caseRecord);
    }

    @Test
    @DisplayName("should find cases by tenant and person")
    void should_findCases_when_tenantAndPersonProvided() {
        var repository = Mockito.mock(SpringDataCaseRepository.class);
        var adapter = new JpaCaseRepositoryAdapter(repository);
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var caseRecord = new Case(
            tenantId,
            personId,
            CaseType.ADDRESS_CHANGE,
            "Address update",
            "New address effective 2026-03-01"
        );

        when(repository.findByTenantIdAndPersonId(tenantId, personId))
            .thenReturn(List.of(caseRecord));

        var result = adapter.findByTenantIdAndPersonId(tenantId, personId);

        assertThat(result).containsExactly(caseRecord);
        verify(repository).findByTenantIdAndPersonId(tenantId, personId);
    }
}

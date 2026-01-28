/*
 * Govinda ERP - Portal Case Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCase;
import net.voytrex.govinda.portal.domain.model.PortalCaseType;
import net.voytrex.govinda.portal.domain.repository.PortalCaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PortalCaseServiceTest {

    @Mock
    private PortalCaseRepository portalCaseRepository;

    @Test
    @DisplayName("should create case when request is valid")
    void should_createCase_when_requestIsValid() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var command = new CreatePortalCaseCommand(
            tenantId,
            personId,
            PortalCaseType.ADDRESS_CHANGE,
            "Moving to new address",
            "New address from 2026-03-01"
        );

        when(portalCaseRepository.save(any(PortalCase.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var service = new PortalCaseService(portalCaseRepository);

        var result = service.createCase(command);

        assertThat(result.getTenantId()).isEqualTo(tenantId);
        assertThat(result.getPersonId()).isEqualTo(personId);
        assertThat(result.getType()).isEqualTo(PortalCaseType.ADDRESS_CHANGE);
        assertThat(result.getSubject()).isEqualTo("Moving to new address");
        assertThat(result.getDescription()).isEqualTo("New address from 2026-03-01");
        assertThat(result.getStatus().name()).isEqualTo("OPEN");
        verify(portalCaseRepository).save(any(PortalCase.class));
    }

    @Test
    @DisplayName("should list cases for person")
    void should_listCases_when_personExists() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var cases = List.of(
            new PortalCase(
                tenantId,
                personId,
                PortalCaseType.ADDRESS_CHANGE,
                "Address change",
                "Moving to new city"
            )
        );

        when(portalCaseRepository.findByTenantIdAndPersonId(tenantId, personId))
            .thenReturn(cases);

        var service = new PortalCaseService(portalCaseRepository);

        var result = service.listCases(tenantId, personId);

        assertThat(result).hasSize(1);
        verify(portalCaseRepository).findByTenantIdAndPersonId(tenantId, personId);
    }
}

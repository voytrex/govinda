/*
 * Govinda ERP - Portal Identity Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundByFieldException;
import net.voytrex.govinda.portal.domain.model.CustomerIdentity;
import net.voytrex.govinda.portal.domain.repository.CustomerIdentityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PortalIdentityServiceTest {

    @Mock
    private CustomerIdentityRepository customerIdentityRepository;

    @Test
    @DisplayName("should resolve person id when subject exists")
    void should_resolvePersonId_when_subjectExists() {
        var tenantId = UUID.randomUUID();
        var personId = UUID.randomUUID();
        var subject = "sub-123";
        var identity = new CustomerIdentity(tenantId, personId, subject);

        when(customerIdentityRepository.findByTenantIdAndSubject(tenantId, subject))
            .thenReturn(Optional.of(identity));

        var service = new PortalIdentityService(customerIdentityRepository);

        var result = service.resolvePersonId(tenantId, subject);

        assertThat(result).isEqualTo(personId);
        verify(customerIdentityRepository).findByTenantIdAndSubject(tenantId, subject);
    }

    @Test
    @DisplayName("should throw when subject is unknown")
    void should_throw_when_subjectUnknown() {
        var tenantId = UUID.randomUUID();
        var subject = "missing-sub";

        when(customerIdentityRepository.findByTenantIdAndSubject(tenantId, subject))
            .thenReturn(Optional.empty());

        var service = new PortalIdentityService(customerIdentityRepository);

        assertThatThrownBy(() -> service.resolvePersonId(tenantId, subject))
            .isInstanceOf(EntityNotFoundByFieldException.class);
    }
}

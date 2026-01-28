/*
 * Govinda ERP - JpaCustomerIdentityRepositoryAdapter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.CustomerIdentity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JpaCustomerIdentityRepositoryAdapterTest {

    @Test
    @DisplayName("should find identity by tenant and subject")
    void should_findIdentity_when_tenantAndSubjectProvided() {
        var repository = Mockito.mock(SpringDataCustomerIdentityRepository.class);
        var adapter = new JpaCustomerIdentityRepositoryAdapter(repository);
        var tenantId = UUID.randomUUID();
        var subject = "portal-subject-123";
        var identity = new CustomerIdentity(tenantId, UUID.randomUUID(), subject);

        when(repository.findByTenantIdAndSubject(tenantId, subject))
            .thenReturn(Optional.of(identity));

        var result = adapter.findByTenantIdAndSubject(tenantId, subject);

        assertThat(result).contains(identity);
        verify(repository).findByTenantIdAndSubject(tenantId, subject);
    }

    @Test
    @DisplayName("should save identity via Spring Data repository")
    void should_saveIdentity_when_called() {
        var repository = Mockito.mock(SpringDataCustomerIdentityRepository.class);
        var adapter = new JpaCustomerIdentityRepositoryAdapter(repository);
        var identity = new CustomerIdentity(UUID.randomUUID(), UUID.randomUUID(), "portal-subject-123");

        when(repository.save(identity)).thenReturn(identity);

        var result = adapter.save(identity);

        assertThat(result).isSameAs(identity);
        verify(repository).save(identity);
    }
}

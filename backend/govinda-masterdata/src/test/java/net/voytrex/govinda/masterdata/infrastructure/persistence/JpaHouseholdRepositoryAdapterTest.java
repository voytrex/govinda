/*
 * Govinda ERP - JPA Household Repository Adapter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.masterdata.domain.model.Household;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class JpaHouseholdRepositoryAdapterTest {

    @Mock
    private SpringDataHouseholdRepository jpaHouseholdRepository;

    @InjectMocks
    private JpaHouseholdRepositoryAdapter adapter;

    @Test
    @DisplayName("should delegate CRUD operations to JPA repository")
    void should_delegateCrudOperations() {
        Household household = new Household(UUID.randomUUID(), "Familie Müller");
        UUID householdId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();

        when(jpaHouseholdRepository.save(household)).thenReturn(household);
        when(jpaHouseholdRepository.findById(householdId)).thenReturn(Optional.of(household));
        when(jpaHouseholdRepository.findByIdAndTenantId(householdId, tenantId)).thenReturn(household);
        when(jpaHouseholdRepository.findByTenantId(tenantId, PageRequest.of(0, 10)))
            .thenReturn(new PageImpl<>(java.util.List.of(household)));
        when(jpaHouseholdRepository.findByPersonId(personId, tenantId)).thenReturn(household);

        assertThat(adapter.save(household)).isEqualTo(household);
        assertThat(adapter.findById(householdId)).isEqualTo(household);
        assertThat(adapter.findByIdAndTenantId(householdId, tenantId)).isEqualTo(household);
        assertThat(adapter.findByTenantId(tenantId, PageRequest.of(0, 10)).getContent()).containsExactly(household);
        assertThat(adapter.findByPersonId(personId, tenantId)).isEqualTo(household);

        adapter.delete(household);

        verify(jpaHouseholdRepository).delete(household);
    }

    @Test
    @DisplayName("should return null when household not found")
    void should_returnNullWhenNotFound() {
        UUID householdId = UUID.randomUUID();
        when(jpaHouseholdRepository.findById(householdId)).thenReturn(Optional.empty());

        assertThat(adapter.findById(householdId)).isNull();
    }
}

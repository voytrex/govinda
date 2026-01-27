/*
 * Govinda ERP - JPA Person Repository Adapter Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class JpaPersonRepositoryAdapterTest {

    @Mock
    private SpringDataPersonRepository jpaPersonRepository;

    @Mock
    private SpringDataPersonHistoryRepository jpaPersonHistoryRepository;

    @InjectMocks
    private JpaPersonRepositoryAdapter adapter;

    @Test
    @DisplayName("should delegate CRUD operations to JPA repositories")
    void should_delegateCrudOperations() {
        Person person = new Person(
            UUID.randomUUID(),
            new AhvNumber("756.1234.5678.90"),
            "Müller",
            "Hans",
            LocalDate.of(1985, 3, 15),
            null,
            null,
            "CHE",
            null
        );
        UUID personId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(jpaPersonRepository.save(person)).thenReturn(person);
        when(jpaPersonRepository.findById(personId)).thenReturn(Optional.of(person));
        when(jpaPersonRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
        when(jpaPersonRepository.findByAhvNrAndTenantId(person.getAhvNr(), tenantId)).thenReturn(Optional.of(person));
        when(jpaPersonRepository.existsByAhvNrAndTenantId(person.getAhvNr(), tenantId)).thenReturn(true);

        assertThat(adapter.save(person)).isEqualTo(person);
        assertThat(adapter.findById(personId)).contains(person);
        assertThat(adapter.findByIdAndTenantId(personId, tenantId)).contains(person);
        assertThat(adapter.findByAhvNr(person.getAhvNr(), tenantId)).contains(person);
        assertThat(adapter.existsByAhvNr(person.getAhvNr(), tenantId)).isTrue();

        adapter.delete(person);

        verify(jpaPersonRepository).delete(person);
    }

    @Test
    @DisplayName("should return empty when person not found")
    void should_returnEmptyWhenNotFound() {
        UUID personId = UUID.randomUUID();
        when(jpaPersonRepository.findById(personId)).thenReturn(Optional.empty());

        assertThat(adapter.findById(personId)).isEmpty();
    }

    @Test
    @DisplayName("should lowercase names when searching")
    void should_lowercaseNamesWhenSearching() {
        UUID tenantId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> page = new PageImpl<>(List.of());
        when(jpaPersonRepository.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        adapter.search(
            tenantId,
            "MÜLLER",
            "HANS",
            "756",
            LocalDate.of(1985, 3, 15),
            "8001",
            pageable
        );

        ArgumentCaptor<String> lastNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> firstNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(jpaPersonRepository).search(
            eq(tenantId),
            lastNameCaptor.capture(),
            firstNameCaptor.capture(),
            eq("756"),
            eq(LocalDate.of(1985, 3, 15)),
            eq("8001"),
            eq(pageable)
        );

        assertThat(lastNameCaptor.getValue()).isEqualTo("müller");
        assertThat(firstNameCaptor.getValue()).isEqualTo("hans");
    }

    @Test
    @DisplayName("should delegate history queries")
    void should_delegateHistoryQueries() {
        UUID personId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2024, 1, 1);
        PersonHistoryEntry entry = new PersonHistoryEntry(
            personId,
            "Müller",
            "Hans",
            null,
            LocalDate.of(2024, 1, 1),
            null,
            null,
            "Created",
            UUID.randomUUID()
        );

        when(jpaPersonHistoryRepository.save(entry)).thenReturn(entry);
        when(jpaPersonHistoryRepository.findByPersonIdOrderByValidFromDesc(personId)).thenReturn(List.of(entry));
        when(jpaPersonHistoryRepository.findByPersonIdAndDate(personId, date)).thenReturn(Optional.of(entry));

        assertThat(adapter.saveHistory(entry)).isEqualTo(entry);
        assertThat(adapter.findHistoryByPersonId(personId)).containsExactly(entry);
        assertThat(adapter.findHistoryAt(personId, date)).contains(entry);
    }
}

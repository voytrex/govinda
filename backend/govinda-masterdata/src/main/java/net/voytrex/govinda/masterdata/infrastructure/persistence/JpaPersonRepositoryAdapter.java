/*
 * Govinda ERP - JPA Person Repository Implementation
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import net.voytrex.govinda.masterdata.domain.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonRepositoryAdapter implements PersonRepository {
    private final SpringDataPersonRepository jpaPersonRepository;
    private final SpringDataPersonHistoryRepository jpaPersonHistoryRepository;

    public JpaPersonRepositoryAdapter(
        SpringDataPersonRepository jpaPersonRepository,
        SpringDataPersonHistoryRepository jpaPersonHistoryRepository
    ) {
        this.jpaPersonRepository = jpaPersonRepository;
        this.jpaPersonHistoryRepository = jpaPersonHistoryRepository;
    }

    @Override
    public Person save(Person person) {
        return jpaPersonRepository.save(person);
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return jpaPersonRepository.findById(id);
    }

    @Override
    public Optional<Person> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaPersonRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Optional<Person> findByAhvNr(AhvNumber ahvNr, UUID tenantId) {
        return jpaPersonRepository.findByAhvNrAndTenantId(ahvNr, tenantId);
    }

    @Override
    public Page<Person> findByTenantId(UUID tenantId, Pageable pageable) {
        return jpaPersonRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    public Page<Person> search(
        UUID tenantId,
        @Nullable String lastName,
        @Nullable String firstName,
        @Nullable String ahvNr,
        @Nullable LocalDate dateOfBirth,
        @Nullable String postalCode,
        Pageable pageable
    ) {
        String lastNameLower = lastName != null ? lastName.toLowerCase() : null;
        String firstNameLower = firstName != null ? firstName.toLowerCase() : null;
        return jpaPersonRepository.search(
            tenantId,
            lastNameLower,
            firstNameLower,
            ahvNr,
            dateOfBirth,
            postalCode,
            pageable
        );
    }

    @Override
    public boolean existsByAhvNr(AhvNumber ahvNr, UUID tenantId) {
        return jpaPersonRepository.existsByAhvNrAndTenantId(ahvNr, tenantId);
    }

    @Override
    public void delete(Person person) {
        jpaPersonRepository.delete(person);
    }

    @Override
    public PersonHistoryEntry saveHistory(PersonHistoryEntry historyEntry) {
        return jpaPersonHistoryRepository.save(historyEntry);
    }

    @Override
    public List<PersonHistoryEntry> findHistoryByPersonId(UUID personId) {
        return jpaPersonHistoryRepository.findByPersonIdOrderByValidFromDesc(personId);
    }

    @Override
    public Optional<PersonHistoryEntry> findHistoryAt(UUID personId, LocalDate date) {
        return jpaPersonHistoryRepository.findByPersonIdAndDate(personId, date);
    }
}

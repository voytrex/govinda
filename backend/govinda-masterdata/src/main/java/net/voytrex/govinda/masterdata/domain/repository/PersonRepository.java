/*
 * Govinda ERP - Person Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository interface for Person aggregate.
 */
public interface PersonRepository {
    Person save(Person person);
    Optional<Person> findById(UUID id);
    Optional<Person> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<Person> findByAhvNr(AhvNumber ahvNr, UUID tenantId);
    Page<Person> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Person> search(
        UUID tenantId,
        String lastName,
        String firstName,
        String ahvNr,
        LocalDate dateOfBirth,
        String postalCode,
        Pageable pageable
    );
    boolean existsByAhvNr(AhvNumber ahvNr, UUID tenantId);
    void delete(Person person);
    PersonHistoryEntry saveHistory(PersonHistoryEntry historyEntry);
    List<PersonHistoryEntry> findHistoryByPersonId(UUID personId);
    Optional<PersonHistoryEntry> findHistoryAt(UUID personId, LocalDate date);
}

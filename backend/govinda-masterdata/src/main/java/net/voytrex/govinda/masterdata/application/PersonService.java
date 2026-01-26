/*
 * Govinda ERP - Person Application Service
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.DuplicateEntityException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import net.voytrex.govinda.masterdata.domain.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for Person use cases.
 */
@Service
@Transactional
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Creates a new person.
     */
    public Person createPerson(CreatePersonCommand command) {
        AhvNumber ahvNr = new AhvNumber(command.ahvNr());

        if (personRepository.existsByAhvNr(ahvNr, command.tenantId())) {
            throw new DuplicateEntityException("Person", "AHV number", command.ahvNr());
        }

        Person person = new Person(
            command.tenantId(),
            ahvNr,
            command.lastName(),
            command.firstName(),
            command.dateOfBirth(),
            command.gender(),
            command.maritalStatus(),
            command.nationality() != null ? command.nationality() : "CHE",
            command.preferredLanguage() != null ? command.preferredLanguage() : Language.DE
        );

        return personRepository.save(person);
    }

    /**
     * Retrieves a person by ID.
     */
    @Transactional(readOnly = true)
    public Person getPerson(UUID id, UUID tenantId) {
        Person person = personRepository.findByIdAndTenantId(id, tenantId);
        if (person == null) {
            throw new EntityNotFoundException("Person", id);
        }
        return person;
    }

    /**
     * Retrieves a person by AHV number.
     */
    @Transactional(readOnly = true)
    public Person getPersonByAhvNr(String ahvNr, UUID tenantId) {
        AhvNumber ahvNumber = new AhvNumber(ahvNr);
        Person person = personRepository.findByAhvNr(ahvNumber, tenantId);
        if (person == null) {
            throw new EntityNotFoundException("Person", UUID.randomUUID());
        }
        return person;
    }

    /**
     * Lists all persons for a tenant.
     */
    @Transactional(readOnly = true)
    public Page<Person> listPersons(UUID tenantId, Pageable pageable) {
        return personRepository.findByTenantId(tenantId, pageable);
    }

    /**
     * Searches for persons.
     */
    @Transactional(readOnly = true)
    public Page<Person> searchPersons(PersonSearchQuery query, Pageable pageable) {
        return personRepository.search(
            query.tenantId(),
            query.lastName(),
            query.firstName(),
            query.ahvNr(),
            query.dateOfBirth(),
            query.postalCode(),
            pageable
        );
    }

    /**
     * Updates basic person data (non-history fields).
     */
    public Person updatePerson(UpdatePersonCommand command) {
        Person person = getPerson(command.personId(), command.tenantId());

        if (command.nationality() != null) {
            person.setNationality(command.nationality());
        }
        if (command.preferredLanguage() != null) {
            person.setPreferredLanguage(command.preferredLanguage());
        }

        return personRepository.save(person);
    }

    /**
     * Changes the person's name (creates history).
     */
    public Person changeName(ChangeNameCommand command) {
        Person person = getPerson(command.personId(), command.tenantId());

        PersonHistoryEntry historyEntry = person.changeName(
            command.newLastName(),
            command.newFirstName() != null ? command.newFirstName() : person.getFirstName(),
            command.reason(),
            command.effectiveDate(),
            command.changedBy()
        );

        personRepository.saveHistory(historyEntry);
        return personRepository.save(person);
    }

    /**
     * Changes the person's marital status (creates history).
     */
    public Person changeMaritalStatus(ChangeMaritalStatusCommand command) {
        Person person = getPerson(command.personId(), command.tenantId());

        PersonHistoryEntry historyEntry = person.changeMaritalStatus(
            command.newStatus(),
            command.reason(),
            command.effectiveDate(),
            command.changedBy()
        );

        personRepository.saveHistory(historyEntry);
        return personRepository.save(person);
    }

    /**
     * Gets the history of a person.
     */
    @Transactional(readOnly = true)
    public List<PersonHistoryEntry> getPersonHistory(UUID personId, UUID tenantId) {
        getPerson(personId, tenantId);
        return personRepository.findHistoryByPersonId(personId);
    }

    /**
     * Gets the person state at a specific date.
     */
    @Transactional(readOnly = true)
    public PersonHistoryEntry getPersonStateAt(UUID personId, UUID tenantId, LocalDate date) {
        getPerson(personId, tenantId);
        return personRepository.findHistoryAt(personId, date);
    }
}

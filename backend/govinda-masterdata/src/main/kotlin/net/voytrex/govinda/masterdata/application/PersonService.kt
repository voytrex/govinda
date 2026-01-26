/*
 * Govinda ERP - Person Application Service
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application

import net.voytrex.govinda.common.domain.exception.DuplicateEntityException
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException
import net.voytrex.govinda.common.domain.model.AhvNumber
import net.voytrex.govinda.common.domain.model.Gender
import net.voytrex.govinda.common.domain.model.Language
import net.voytrex.govinda.common.domain.model.MaritalStatus
import net.voytrex.govinda.masterdata.domain.model.Person
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry
import net.voytrex.govinda.masterdata.domain.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

/**
 * Application service for Person use cases.
 */
@Service
@Transactional
class PersonService(
    private val personRepository: PersonRepository
) {

    /**
     * Creates a new person.
     */
    fun createPerson(command: CreatePersonCommand): Person {
        val ahvNr = AhvNumber(command.ahvNr)

        // Check for duplicate AHV number
        if (personRepository.existsByAhvNr(ahvNr, command.tenantId)) {
            throw DuplicateEntityException("Person", "AHV number", command.ahvNr)
        }

        val person = Person(
            tenantId = command.tenantId,
            ahvNr = ahvNr,
            lastName = command.lastName,
            firstName = command.firstName,
            dateOfBirth = command.dateOfBirth,
            gender = command.gender,
            maritalStatus = command.maritalStatus,
            nationality = command.nationality,
            preferredLanguage = command.preferredLanguage ?: Language.DE
        )

        return personRepository.save(person)
    }

    /**
     * Retrieves a person by ID.
     */
    @Transactional(readOnly = true)
    fun getPerson(id: UUID, tenantId: UUID): Person {
        return personRepository.findByIdAndTenantId(id, tenantId)
            ?: throw EntityNotFoundException("Person", id)
    }

    /**
     * Retrieves a person by AHV number.
     */
    @Transactional(readOnly = true)
    fun getPersonByAhvNr(ahvNr: String, tenantId: UUID): Person {
        val ahvNumber = AhvNumber(ahvNr)
        return personRepository.findByAhvNr(ahvNumber, tenantId)
            ?: throw EntityNotFoundException("Person", UUID.randomUUID()) // TODO: better exception
    }

    /**
     * Lists all persons for a tenant.
     */
    @Transactional(readOnly = true)
    fun listPersons(tenantId: UUID, pageable: Pageable): Page<Person> {
        return personRepository.findByTenantId(tenantId, pageable)
    }

    /**
     * Searches for persons.
     */
    @Transactional(readOnly = true)
    fun searchPersons(query: PersonSearchQuery, pageable: Pageable): Page<Person> {
        return personRepository.search(
            tenantId = query.tenantId,
            lastName = query.lastName,
            firstName = query.firstName,
            ahvNr = query.ahvNr,
            dateOfBirth = query.dateOfBirth,
            postalCode = query.postalCode,
            pageable = pageable
        )
    }

    /**
     * Updates basic person data (non-history fields).
     */
    fun updatePerson(command: UpdatePersonCommand): Person {
        val person = getPerson(command.personId, command.tenantId)

        command.nationality?.let { person.nationality = it }
        command.preferredLanguage?.let { person.preferredLanguage = it }

        return personRepository.save(person)
    }

    /**
     * Changes the person's name (creates history).
     */
    fun changeName(command: ChangeNameCommand): Person {
        val person = getPerson(command.personId, command.tenantId)

        val historyEntry = person.changeName(
            newLastName = command.newLastName,
            newFirstName = command.newFirstName ?: person.firstName,
            reason = command.reason,
            effectiveDate = command.effectiveDate,
            changedBy = command.changedBy
        )

        personRepository.saveHistory(historyEntry)
        return personRepository.save(person)
    }

    /**
     * Changes the person's marital status (creates history).
     */
    fun changeMaritalStatus(command: ChangeMaritalStatusCommand): Person {
        val person = getPerson(command.personId, command.tenantId)

        val historyEntry = person.changeMaritalStatus(
            newStatus = command.newStatus,
            reason = command.reason,
            effectiveDate = command.effectiveDate,
            changedBy = command.changedBy
        )

        personRepository.saveHistory(historyEntry)
        return personRepository.save(person)
    }

    /**
     * Gets the history of a person.
     */
    @Transactional(readOnly = true)
    fun getPersonHistory(personId: UUID, tenantId: UUID): List<PersonHistoryEntry> {
        // Verify person exists and belongs to tenant
        getPerson(personId, tenantId)
        return personRepository.findHistoryByPersonId(personId)
    }

    /**
     * Gets the person state at a specific date.
     */
    @Transactional(readOnly = true)
    fun getPersonStateAt(personId: UUID, tenantId: UUID, date: LocalDate): PersonHistoryEntry? {
        getPerson(personId, tenantId)
        return personRepository.findHistoryAt(personId, date)
    }
}

// ═══════════════════════════════════════════════════════════════
// Commands & Queries
// ═══════════════════════════════════════════════════════════════

data class CreatePersonCommand(
    val tenantId: UUID,
    val ahvNr: String,
    val lastName: String,
    val firstName: String,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val maritalStatus: MaritalStatus? = null,
    val nationality: String? = "CHE",
    val preferredLanguage: Language? = Language.DE
)

data class UpdatePersonCommand(
    val tenantId: UUID,
    val personId: UUID,
    val nationality: String? = null,
    val preferredLanguage: Language? = null
)

data class ChangeNameCommand(
    val tenantId: UUID,
    val personId: UUID,
    val newLastName: String,
    val newFirstName: String? = null,
    val reason: String,
    val effectiveDate: LocalDate,
    val changedBy: UUID
)

data class ChangeMaritalStatusCommand(
    val tenantId: UUID,
    val personId: UUID,
    val newStatus: MaritalStatus,
    val reason: String,
    val effectiveDate: LocalDate,
    val changedBy: UUID
)

data class PersonSearchQuery(
    val tenantId: UUID,
    val lastName: String? = null,
    val firstName: String? = null,
    val ahvNr: String? = null,
    val dateOfBirth: LocalDate? = null,
    val postalCode: String? = null
)

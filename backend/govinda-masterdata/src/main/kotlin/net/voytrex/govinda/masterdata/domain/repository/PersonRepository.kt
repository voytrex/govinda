/*
 * Govinda ERP - Person Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.repository

import net.voytrex.govinda.common.domain.model.AhvNumber
import net.voytrex.govinda.masterdata.domain.model.Person
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

/**
 * Repository interface for Person aggregate.
 */
interface PersonRepository {

    fun save(person: Person): Person

    fun findById(id: UUID): Person?

    fun findByIdAndTenantId(id: UUID, tenantId: UUID): Person?

    fun findByAhvNr(ahvNr: AhvNumber, tenantId: UUID): Person?

    fun findByTenantId(tenantId: UUID, pageable: Pageable): Page<Person>

    fun search(
        tenantId: UUID,
        lastName: String? = null,
        firstName: String? = null,
        ahvNr: String? = null,
        dateOfBirth: LocalDate? = null,
        postalCode: String? = null,
        pageable: Pageable
    ): Page<Person>

    fun existsByAhvNr(ahvNr: AhvNumber, tenantId: UUID): Boolean

    fun delete(person: Person)

    fun saveHistory(historyEntry: PersonHistoryEntry): PersonHistoryEntry

    fun findHistoryByPersonId(personId: UUID): List<PersonHistoryEntry>

    fun findHistoryAt(personId: UUID, date: LocalDate): PersonHistoryEntry?
}

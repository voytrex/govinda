/*
 * Govinda ERP - JPA Person Repository Implementation
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence

import net.voytrex.govinda.common.domain.model.AhvNumber
import net.voytrex.govinda.masterdata.domain.model.Person
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry
import net.voytrex.govinda.masterdata.domain.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class JpaPersonRepositoryAdapter(
    private val jpaPersonRepository: SpringDataPersonRepository,
    private val jpaPersonHistoryRepository: SpringDataPersonHistoryRepository
) : PersonRepository {

    override fun save(person: Person): Person =
        jpaPersonRepository.save(person)

    override fun findById(id: UUID): Person? =
        jpaPersonRepository.findById(id).orElse(null)

    override fun findByIdAndTenantId(id: UUID, tenantId: UUID): Person? =
        jpaPersonRepository.findByIdAndTenantId(id, tenantId)

    override fun findByAhvNr(ahvNr: AhvNumber, tenantId: UUID): Person? =
        jpaPersonRepository.findByAhvNrAndTenantId(ahvNr, tenantId)

    override fun findByTenantId(tenantId: UUID, pageable: Pageable): Page<Person> =
        jpaPersonRepository.findByTenantId(tenantId, pageable)

    override fun search(
        tenantId: UUID,
        lastName: String?,
        firstName: String?,
        ahvNr: String?,
        dateOfBirth: LocalDate?,
        postalCode: String?,
        pageable: Pageable
    ): Page<Person> = jpaPersonRepository.search(
        tenantId = tenantId,
        lastName = lastName?.lowercase(),
        firstName = firstName?.lowercase(),
        ahvNr = ahvNr,
        dateOfBirth = dateOfBirth,
        postalCode = postalCode,
        pageable = pageable
    )

    override fun existsByAhvNr(ahvNr: AhvNumber, tenantId: UUID): Boolean =
        jpaPersonRepository.existsByAhvNrAndTenantId(ahvNr, tenantId)

    override fun delete(person: Person) =
        jpaPersonRepository.delete(person)

    override fun saveHistory(historyEntry: PersonHistoryEntry): PersonHistoryEntry =
        jpaPersonHistoryRepository.save(historyEntry)

    override fun findHistoryByPersonId(personId: UUID): List<PersonHistoryEntry> =
        jpaPersonHistoryRepository.findByPersonIdOrderByValidFromDesc(personId)

    override fun findHistoryAt(personId: UUID, date: LocalDate): PersonHistoryEntry? =
        jpaPersonHistoryRepository.findByPersonIdAndDate(personId, date)
}

interface SpringDataPersonRepository : JpaRepository<Person, UUID> {

    fun findByIdAndTenantId(id: UUID, tenantId: UUID): Person?

    fun findByAhvNrAndTenantId(ahvNr: AhvNumber, tenantId: UUID): Person?

    fun findByTenantId(tenantId: UUID, pageable: Pageable): Page<Person>

    fun existsByAhvNrAndTenantId(ahvNr: AhvNumber, tenantId: UUID): Boolean

    @Query("""
        SELECT p FROM Person p
        WHERE p.tenantId = :tenantId
        AND (:lastName IS NULL OR LOWER(p.lastName) LIKE CONCAT('%', :lastName, '%'))
        AND (:firstName IS NULL OR LOWER(p.firstName) LIKE CONCAT('%', :firstName, '%'))
        AND (:ahvNr IS NULL OR p.ahvNr.value LIKE CONCAT('%', :ahvNr, '%'))
        AND (:dateOfBirth IS NULL OR p.dateOfBirth = :dateOfBirth)
        AND (:postalCode IS NULL OR EXISTS (
            SELECT a FROM Address a WHERE a.personId = p.id
            AND a.postalCode = :postalCode AND a.validTo IS NULL
        ))
    """)
    fun search(
        @Param("tenantId") tenantId: UUID,
        @Param("lastName") lastName: String?,
        @Param("firstName") firstName: String?,
        @Param("ahvNr") ahvNr: String?,
        @Param("dateOfBirth") dateOfBirth: LocalDate?,
        @Param("postalCode") postalCode: String?,
        pageable: Pageable
    ): Page<Person>
}

interface SpringDataPersonHistoryRepository : JpaRepository<PersonHistoryEntry, UUID> {

    fun findByPersonIdOrderByValidFromDesc(personId: UUID): List<PersonHistoryEntry>

    @Query("""
        SELECT h FROM PersonHistoryEntry h
        WHERE h.personId = :personId
        AND h.validFrom <= :date
        AND (h.validTo IS NULL OR h.validTo >= :date)
        AND h.supersededAt IS NULL
        ORDER BY h.validFrom DESC
    """)
    fun findByPersonIdAndDate(
        @Param("personId") personId: UUID,
        @Param("date") date: LocalDate
    ): PersonHistoryEntry?
}

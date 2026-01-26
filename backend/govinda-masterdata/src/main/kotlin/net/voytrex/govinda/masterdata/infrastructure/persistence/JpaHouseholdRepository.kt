/*
 * Govinda ERP - JPA Household Repository Implementation
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence

import net.voytrex.govinda.masterdata.domain.model.Household
import net.voytrex.govinda.masterdata.domain.repository.HouseholdRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaHouseholdRepositoryAdapter(
    private val jpaHouseholdRepository: SpringDataHouseholdRepository
) : HouseholdRepository {

    override fun save(household: Household): Household =
        jpaHouseholdRepository.save(household)

    override fun findById(id: UUID): Household? =
        jpaHouseholdRepository.findById(id).orElse(null)

    override fun findByIdAndTenantId(id: UUID, tenantId: UUID): Household? =
        jpaHouseholdRepository.findByIdAndTenantId(id, tenantId)

    override fun findByTenantId(tenantId: UUID, pageable: Pageable): Page<Household> =
        jpaHouseholdRepository.findByTenantId(tenantId, pageable)

    override fun findByPersonId(personId: UUID, tenantId: UUID): Household? =
        jpaHouseholdRepository.findByPersonId(personId, tenantId)

    override fun delete(household: Household) =
        jpaHouseholdRepository.delete(household)
}

interface SpringDataHouseholdRepository : JpaRepository<Household, UUID> {

    fun findByIdAndTenantId(id: UUID, tenantId: UUID): Household?

    fun findByTenantId(tenantId: UUID, pageable: Pageable): Page<Household>

    @Query("""
        SELECT h FROM Household h
        JOIN h._members m
        WHERE m.personId = :personId
        AND h.tenantId = :tenantId
        AND m.validTo IS NULL
    """)
    fun findByPersonId(
        @Param("personId") personId: UUID,
        @Param("tenantId") tenantId: UUID
    ): Household?
}

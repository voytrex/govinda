/*
 * Govinda ERP - Household Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.repository

import net.voytrex.govinda.masterdata.domain.model.Household
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

/**
 * Repository interface for Household aggregate.
 */
interface HouseholdRepository {

    fun save(household: Household): Household

    fun findById(id: UUID): Household?

    fun findByIdAndTenantId(id: UUID, tenantId: UUID): Household?

    fun findByTenantId(tenantId: UUID, pageable: Pageable): Page<Household>

    fun findByPersonId(personId: UUID, tenantId: UUID): Household?

    fun delete(household: Household)
}

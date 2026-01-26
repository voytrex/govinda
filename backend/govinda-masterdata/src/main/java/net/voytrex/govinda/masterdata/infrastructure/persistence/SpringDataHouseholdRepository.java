/*
 * Govinda ERP - Spring Data Household Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import java.util.UUID;
import net.voytrex.govinda.masterdata.domain.model.Household;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataHouseholdRepository extends JpaRepository<Household, UUID> {
    Household findByIdAndTenantId(UUID id, UUID tenantId);
    Page<Household> findByTenantId(UUID tenantId, Pageable pageable);

    @Query("""
        SELECT h FROM Household h
        JOIN h.members m
        WHERE m.personId = :personId
        AND h.tenantId = :tenantId
        AND m.validTo IS NULL
        """)
    Household findByPersonId(@Param("personId") UUID personId, @Param("tenantId") UUID tenantId);
}

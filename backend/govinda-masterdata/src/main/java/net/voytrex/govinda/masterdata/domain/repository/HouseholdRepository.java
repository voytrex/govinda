/*
 * Govinda ERP - Household Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.repository;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.masterdata.domain.model.Household;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository interface for Household aggregate.
 */
public interface HouseholdRepository {
    Household save(Household household);
    Optional<Household> findById(UUID id);
    Optional<Household> findByIdAndTenantId(UUID id, UUID tenantId);
    Page<Household> findByTenantId(UUID tenantId, Pageable pageable);
    Optional<Household> findByPersonId(UUID personId, UUID tenantId);
    void delete(Household household);
}

/*
 * Govinda ERP - Household Repository
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.repository;

import java.util.UUID;
import net.voytrex.govinda.masterdata.domain.model.Household;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository interface for Household aggregate.
 */
public interface HouseholdRepository {
    Household save(Household household);
    Household findById(UUID id);
    Household findByIdAndTenantId(UUID id, UUID tenantId);
    Page<Household> findByTenantId(UUID tenantId, Pageable pageable);
    Household findByPersonId(UUID personId, UUID tenantId);
    void delete(Household household);
}

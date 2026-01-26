/*
 * Govinda ERP - JPA Household Repository Implementation
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import java.util.UUID;
import net.voytrex.govinda.masterdata.domain.model.Household;
import net.voytrex.govinda.masterdata.domain.repository.HouseholdRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class JpaHouseholdRepositoryAdapter implements HouseholdRepository {
    private final SpringDataHouseholdRepository jpaHouseholdRepository;

    public JpaHouseholdRepositoryAdapter(SpringDataHouseholdRepository jpaHouseholdRepository) {
        this.jpaHouseholdRepository = jpaHouseholdRepository;
    }

    @Override
    public Household save(Household household) {
        return jpaHouseholdRepository.save(household);
    }

    @Override
    public Household findById(UUID id) {
        return jpaHouseholdRepository.findById(id).orElse(null);
    }

    @Override
    public Household findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaHouseholdRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Page<Household> findByTenantId(UUID tenantId, Pageable pageable) {
        return jpaHouseholdRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    public Household findByPersonId(UUID personId, UUID tenantId) {
        return jpaHouseholdRepository.findByPersonId(personId, tenantId);
    }

    @Override
    public void delete(Household household) {
        jpaHouseholdRepository.delete(household);
    }
}

/*
 * Govinda ERP - JPA Customer Identity Repository Implementation
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.CustomerIdentity;
import net.voytrex.govinda.portal.domain.repository.CustomerIdentityRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCustomerIdentityRepositoryAdapter implements CustomerIdentityRepository {
    private final SpringDataCustomerIdentityRepository customerIdentityRepository;

    public JpaCustomerIdentityRepositoryAdapter(SpringDataCustomerIdentityRepository customerIdentityRepository) {
        this.customerIdentityRepository = customerIdentityRepository;
    }

    @Override
    public Optional<CustomerIdentity> findByTenantIdAndSubject(UUID tenantId, String subject) {
        return customerIdentityRepository.findByTenantIdAndSubject(tenantId, subject);
    }

    @Override
    public CustomerIdentity save(CustomerIdentity identity) {
        return customerIdentityRepository.save(identity);
    }
}

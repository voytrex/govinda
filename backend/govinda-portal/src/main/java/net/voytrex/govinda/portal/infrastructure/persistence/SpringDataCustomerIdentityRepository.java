/*
 * Govinda ERP - Spring Data Customer Identity Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.CustomerIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerIdentityRepository extends JpaRepository<CustomerIdentity, UUID> {
    Optional<CustomerIdentity> findByTenantIdAndSubject(UUID tenantId, String subject);
}

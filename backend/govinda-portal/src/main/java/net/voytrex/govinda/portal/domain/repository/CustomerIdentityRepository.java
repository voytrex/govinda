/*
 * Govinda ERP - Customer Identity Repository
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.domain.repository;

import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.CustomerIdentity;

public interface CustomerIdentityRepository {
    Optional<CustomerIdentity> findByTenantIdAndSubject(UUID tenantId, String subject);
    CustomerIdentity save(CustomerIdentity identity);
}

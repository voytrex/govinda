/*
 * Govinda ERP - JPA Portal Case Repository Implementation
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCase;
import net.voytrex.govinda.portal.domain.repository.PortalCaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPortalCaseRepositoryAdapter implements PortalCaseRepository {
    private final SpringDataPortalCaseRepository portalCaseRepository;

    public JpaPortalCaseRepositoryAdapter(SpringDataPortalCaseRepository portalCaseRepository) {
        this.portalCaseRepository = portalCaseRepository;
    }

    @Override
    public PortalCase save(PortalCase portalCase) {
        return portalCaseRepository.save(portalCase);
    }

    @Override
    public List<PortalCase> findByTenantIdAndPersonId(UUID tenantId, UUID personId) {
        return portalCaseRepository.findByTenantIdAndPersonId(tenantId, personId);
    }
}

/*
 * Govinda ERP - Portal Case Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCase;
import net.voytrex.govinda.portal.domain.repository.PortalCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PortalCaseService {
    private final PortalCaseRepository portalCaseRepository;

    public PortalCaseService(PortalCaseRepository portalCaseRepository) {
        this.portalCaseRepository = portalCaseRepository;
    }

    public PortalCase createCase(CreatePortalCaseCommand command) {
        var portalCase = new PortalCase(
            command.tenantId(),
            command.personId(),
            command.type(),
            command.subject(),
            command.description()
        );
        return portalCaseRepository.save(portalCase);
    }

    @Transactional(readOnly = true)
    public List<PortalCase> listCases(UUID tenantId, UUID personId) {
        return portalCaseRepository.findByTenantIdAndPersonId(tenantId, personId);
    }
}

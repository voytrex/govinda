/*
 * Govinda ERP - Portal Identity Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundByFieldException;
import net.voytrex.govinda.portal.domain.model.CustomerIdentity;
import net.voytrex.govinda.portal.domain.repository.CustomerIdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortalIdentityService {
    private final CustomerIdentityRepository customerIdentityRepository;

    public PortalIdentityService(CustomerIdentityRepository customerIdentityRepository) {
        this.customerIdentityRepository = customerIdentityRepository;
    }

    public UUID resolvePersonId(UUID tenantId, String subject) {
        return customerIdentityRepository.findByTenantIdAndSubject(tenantId, subject)
            .map(CustomerIdentity::getPersonId)
            .orElseThrow(() -> new EntityNotFoundByFieldException("CustomerIdentity", "subject", subject));
    }
}

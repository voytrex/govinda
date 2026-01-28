/*
 * Govinda ERP - Portal Profile Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import java.util.UUID;
import net.voytrex.govinda.masterdata.application.PersonService;
import net.voytrex.govinda.masterdata.domain.model.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PortalProfileService {
    private final PersonService personService;

    public PortalProfileService(PersonService personService) {
        this.personService = personService;
    }

    public Person getProfile(UUID tenantId, UUID personId) {
        return personService.getPerson(personId, tenantId);
    }
}

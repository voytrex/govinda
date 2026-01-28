/*
 * Govinda ERP - Portal Profile API Mapper
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import net.voytrex.govinda.masterdata.domain.model.Person;

public final class PortalProfileMapper {
    private PortalProfileMapper() {
    }

    public static PortalProfileResponse toResponse(Person person) {
        return new PortalProfileResponse(
            person.getId(),
            person.getFirstName(),
            person.getLastName(),
            person.fullName(),
            person.getDateOfBirth(),
            person.getGender(),
            person.getPreferredLanguage(),
            person.getNationality(),
            person.getStatus().name()
        );
    }
}

/*
 * Govinda ERP - Person API Mapper
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import java.time.LocalDate;
import net.voytrex.govinda.common.domain.model.AgeGroup;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;

public final class PersonMapper {
    private PersonMapper() {
    }

    public static PersonResponse toResponse(Person person) {
        LocalDate today = LocalDate.now();
        return new PersonResponse(
            person.getId(),
            person.getAhvNr().getValue(),
            person.getLastName(),
            person.getFirstName(),
            person.fullName(),
            person.getDateOfBirth(),
            person.getGender(),
            person.ageAt(today),
            AgeGroup.forAge(person.ageAt(today)),
            person.getMaritalStatus(),
            person.getNationality(),
            person.getPreferredLanguage(),
            person.getStatus().name()
        );
    }

    public static PersonHistoryResponse toResponse(PersonHistoryEntry entry) {
        return new PersonHistoryResponse(
            entry.getHistoryId(),
            entry.getLastName(),
            entry.getFirstName(),
            entry.getMaritalStatus(),
            entry.getValidFrom(),
            entry.getValidTo(),
            entry.getMutationType().name(),
            entry.getMutationReason(),
            entry.getChangedBy(),
            entry.getRecordedAt().toString()
        );
    }
}

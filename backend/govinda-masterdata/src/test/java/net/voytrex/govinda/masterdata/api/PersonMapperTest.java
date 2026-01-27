/*
 * Govinda ERP - Person Mapper Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AgeGroup;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.common.domain.model.MutationType;
import net.voytrex.govinda.common.domain.model.PersonStatus;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PersonMapperTest {

    @Test
    @DisplayName("should map person to response with derived fields")
    void should_mapPersonToResponse() {
        Person person = new Person(
            UUID.randomUUID(),
            new AhvNumber("756.1234.5678.90"),
            "Müller",
            "Hans",
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            MaritalStatus.SINGLE,
            "CHE",
            Language.DE
        );
        person.setStatus(PersonStatus.ACTIVE);

        PersonResponse response = PersonMapper.toResponse(person);
        int expectedAge = person.ageAt(LocalDate.now());

        assertThat(response.ahvNr()).isEqualTo("756.1234.5678.90");
        assertThat(response.fullName()).isEqualTo("Hans Müller");
        assertThat(response.age()).isEqualTo(expectedAge);
        assertThat(response.ageGroup()).isEqualTo(AgeGroup.forAge(expectedAge));
        assertThat(response.status()).isEqualTo(PersonStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("should map history entry to response")
    void should_mapHistoryEntryToResponse() {
        PersonHistoryEntry entry = new PersonHistoryEntry(
            UUID.randomUUID(),
            "Müller",
            "Hans",
            MaritalStatus.SINGLE,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            MutationType.UPDATE,
            "Correction",
            UUID.randomUUID()
        );

        PersonHistoryResponse response = PersonMapper.toResponse(entry);

        assertThat(response.lastName()).isEqualTo("Müller");
        assertThat(response.mutationType()).isEqualTo(MutationType.UPDATE.name());
        assertThat(response.recordedAt()).isEqualTo(entry.getRecordedAt().toString());
    }
}

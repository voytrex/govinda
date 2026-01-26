/*
 * Govinda ERP - Person Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AgeGroup;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.common.domain.model.MutationType;
import net.voytrex.govinda.common.domain.model.PersonStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PersonTest {

    private final UUID tenantId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Nested
    @DisplayName("Person Creation")
    class PersonCreation {

        @Test
        void shouldCreatePersonWithValidData() {
            Person person = new Person(
                tenantId,
                new AhvNumber("756.1234.5678.90"),
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                null,
                "CHE",
                null
            );

            assertThat(person.getId()).isNotNull();
            assertThat(person.getLastName()).isEqualTo("Müller");
            assertThat(person.getFirstName()).isEqualTo("Hans");
            assertThat(person.fullName()).isEqualTo("Hans Müller");
            assertThat(person.getStatus()).isEqualTo(PersonStatus.ACTIVE);
        }

        @Test
        void shouldRejectEmptyLastName() {
            assertThatThrownBy(() -> new Person(
                tenantId,
                new AhvNumber("756.1234.5678.90"),
                "",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                null,
                "CHE",
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Last name");
        }

        @Test
        void shouldRejectEmptyFirstName() {
            assertThatThrownBy(() -> new Person(
                tenantId,
                new AhvNumber("756.1234.5678.90"),
                "Müller",
                "",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                null,
                "CHE",
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name");
        }

        @Test
        void shouldRejectFutureBirthDate() {
            assertThatThrownBy(() -> new Person(
                tenantId,
                new AhvNumber("756.1234.5678.90"),
                "Müller",
                "Hans",
                LocalDate.now().plusDays(1),
                Gender.MALE,
                null,
                "CHE",
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date of birth");
        }
    }

    @Nested
    @DisplayName("Age Calculation")
    class AgeCalculation {

        @Test
        void shouldCalculateAgeCorrectly() {
            Person person = createTestPerson(LocalDate.of(1985, 3, 15));

            int ageOn2024 = person.ageAt(LocalDate.of(2024, 3, 15));
            assertThat(ageOn2024).isEqualTo(39);

            int ageBeforeBirthday = person.ageAt(LocalDate.of(2024, 3, 14));
            assertThat(ageBeforeBirthday).isEqualTo(38);
        }

        @Test
        void shouldDetermineAgeGroupForChild() {
            Person child = createTestPerson(LocalDate.of(2010, 6, 1));

            AgeGroup ageGroup = child.ageGroupAt(LocalDate.of(2024, 1, 1));
            assertThat(ageGroup).isEqualTo(AgeGroup.CHILD);
        }

        @Test
        void shouldDetermineAgeGroupForYoungAdult() {
            Person youngAdult = createTestPerson(LocalDate.of(2002, 6, 1));

            AgeGroup ageGroup = youngAdult.ageGroupAt(LocalDate.of(2024, 1, 1));
            assertThat(ageGroup).isEqualTo(AgeGroup.YOUNG_ADULT);
        }

        @Test
        void shouldDetermineAgeGroupForAdult() {
            Person adult = createTestPerson(LocalDate.of(1985, 3, 15));

            AgeGroup ageGroup = adult.ageGroupAt(LocalDate.of(2024, 1, 1));
            assertThat(ageGroup).isEqualTo(AgeGroup.ADULT);
        }
    }

    @Nested
    @DisplayName("Name Change with History")
    class NameChangeWithHistory {

        @Test
        void shouldChangeNameAndCreateHistoryEntry() {
            Person person = createTestPerson("Müller", "Anna");

            PersonHistoryEntry historyEntry = person.changeName(
                "Schmidt-Müller",
                "Anna",
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            assertThat(person.getLastName()).isEqualTo("Schmidt-Müller");
            assertThat(person.getFirstName()).isEqualTo("Anna");

            assertThat(historyEntry.getLastName()).isEqualTo("Müller");
            assertThat(historyEntry.getFirstName()).isEqualTo("Anna");
            assertThat(historyEntry.getMutationType()).isEqualTo(MutationType.UPDATE);
            assertThat(historyEntry.getMutationReason()).isEqualTo("Marriage");
            assertThat(historyEntry.getValidTo()).isEqualTo(LocalDate.of(2024, 8, 31));
        }
    }

    @Nested
    @DisplayName("Marital Status Change")
    class MaritalStatusChange {

        @Test
        void shouldChangeMaritalStatusAndCreateHistoryEntry() {
            Person person = createTestPerson("Müller", "Hans");
            person.setMaritalStatus(MaritalStatus.SINGLE);

            PersonHistoryEntry historyEntry = person.changeMaritalStatus(
                MaritalStatus.MARRIED,
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            assertThat(person.getMaritalStatus()).isEqualTo(MaritalStatus.MARRIED);
            assertThat(historyEntry.getMaritalStatus()).isEqualTo(MaritalStatus.SINGLE);
        }
    }

    private Person createTestPerson(LocalDate dateOfBirth) {
        return new Person(
            tenantId,
            new AhvNumber("756.1234.5678.90"),
            "Müller",
            "Hans",
            dateOfBirth,
            Gender.MALE,
            null,
            "CHE",
            null
        );
    }

    private Person createTestPerson(String lastName, String firstName) {
        return new Person(
            tenantId,
            new AhvNumber("756.1234.5678.90"),
            lastName,
            firstName,
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            null,
            "CHE",
            null
        );
    }
}

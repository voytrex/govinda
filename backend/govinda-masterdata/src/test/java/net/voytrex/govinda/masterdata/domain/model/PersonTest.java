/*
 * Govinda ERP - Person Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AddressType;
import net.voytrex.govinda.common.domain.model.AgeGroup;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Canton;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
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

        @Test
        void shouldUseDefaultsWhenNationalityOrLanguageMissing() {
            Person person = new Person(
                tenantId,
                new AhvNumber("756.1234.5678.90"),
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                null,
                null,
                null
            );

            assertThat(person.getNationality()).isEqualTo("CHE");
            assertThat(person.getPreferredLanguage()).isEqualTo(Language.DE);
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

    @Nested
    @DisplayName("Address Management")
    class AddressManagement {

        @Test
        void shouldReturnCurrentAndHistoricMainAddresses() {
            Person person = createTestPerson("Müller", "Hans");
            Address oldAddress = createAddress(
                person.getId(),
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2023, 12, 31)
            );
            Address currentAddress = createAddress(
                person.getId(),
                LocalDate.of(2024, 1, 1),
                null
            );
            person.setAddresses(List.of(oldAddress, currentAddress));

            assertThat(person.currentAddress()).isEqualTo(currentAddress);
            assertThat(person.addressAt(LocalDate.of(2022, 6, 1))).isEqualTo(oldAddress);
        }

        @Test
        void shouldCloseExistingAddressWhenAddingNewOne() {
            Person person = createTestPerson("Müller", "Hans");
            Address currentAddress = createAddress(person.getId(), LocalDate.of(2023, 1, 1), null);
            person.addAddress(currentAddress, null);

            LocalDate closeOn = LocalDate.of(2024, 1, 31);
            Address newAddress = createAddress(person.getId(), LocalDate.of(2024, 2, 1), null);
            person.addAddress(newAddress, closeOn);

            assertThat(currentAddress.getValidTo()).isEqualTo(closeOn);
            assertThat(person.currentAddress()).isEqualTo(newAddress);
        }
    }

    @Nested
    @DisplayName("Setters and Equality")
    class SettersAndEquality {

        @Test
        void shouldUpdateFieldsUsingSetters() {
            Person person = createTestPerson("Müller", "Hans");
            UUID newTenantId = UUID.randomUUID();
            AhvNumber newAhv = new AhvNumber("756.9876.5432.10");

            person.setId(UUID.randomUUID());
            person.setTenantId(newTenantId);
            person.setAhvNr(newAhv);
            person.setLastName("Schmidt");
            person.setFirstName("Anna");
            person.setDateOfBirth(LocalDate.of(1990, 4, 10));
            person.setGender(Gender.FEMALE);
            person.setMaritalStatus(MaritalStatus.MARRIED);
            person.setNationality("DEU");
            person.setPreferredLanguage(Language.FR);
            person.setStatus(PersonStatus.DECEASED);
            person.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
            person.setUpdatedAt(Instant.parse("2024-02-01T00:00:00Z"));
            person.setVersion(2L);

            assertThat(person.getTenantId()).isEqualTo(newTenantId);
            assertThat(person.getAhvNr()).isEqualTo(newAhv);
            assertThat(person.getLastName()).isEqualTo("Schmidt");
            assertThat(person.getFirstName()).isEqualTo("Anna");
            assertThat(person.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 4, 10));
            assertThat(person.getGender()).isEqualTo(Gender.FEMALE);
            assertThat(person.getMaritalStatus()).isEqualTo(MaritalStatus.MARRIED);
            assertThat(person.getNationality()).isEqualTo("DEU");
            assertThat(person.getPreferredLanguage()).isEqualTo(Language.FR);
            assertThat(person.getStatus()).isEqualTo(PersonStatus.DECEASED);
            assertThat(person.getCreatedAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
            assertThat(person.getUpdatedAt()).isEqualTo(Instant.parse("2024-02-01T00:00:00Z"));
            assertThat(person.getVersion()).isEqualTo(2L);
        }

        @Test
        void shouldRejectBlankNamesInSetters() {
            Person person = createTestPerson("Müller", "Hans");

            assertThatThrownBy(() -> person.setLastName(" "))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> person.setFirstName(" "))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldCompareByIdAndProvideString() {
            Person first = createTestPerson("Müller", "Hans");
            Person second = createTestPerson("Schmidt", "Anna");
            UUID sharedId = UUID.randomUUID();

            first.setId(sharedId);
            second.setId(sharedId);

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
            assertThat(first.toString()).contains(sharedId.toString());
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

    private Address createAddress(UUID personId, LocalDate validFrom, LocalDate validTo) {
        return new Address(
            personId,
            AddressType.MAIN,
            "Bahnhofstrasse",
            "42",
            null,
            "8001",
            "Zürich",
            Canton.ZH,
            "CHE",
            UUID.randomUUID(),
            validFrom,
            validTo,
            null
        );
    }
}

/*
 * Govinda ERP - Address Entity Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AddressType;
import net.voytrex.govinda.common.domain.model.Canton;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AddressTest {

    private final UUID personId = UUID.randomUUID();
    private final UUID regionId = UUID.randomUUID();

    @Nested
    @DisplayName("Address Creation")
    class AddressCreation {

        @Test
        void shouldCreateAddressWithValidData() {
            Address address = new Address(
                personId,
                AddressType.MAIN,
                "Bahnhofstrasse",
                "42",
                null,
                "8001",
                "Zürich",
                Canton.ZH,
                "CHE",
                regionId,
                LocalDate.of(2024, 1, 1),
                null,
                null
            );

            assertThat(address.getId()).isNotNull();
            assertThat(address.getStreet()).isEqualTo("Bahnhofstrasse");
            assertThat(address.getPostalCode()).isEqualTo("8001");
            assertThat(address.getCity()).isEqualTo("Zürich");
            assertThat(address.getCanton()).isEqualTo(Canton.ZH);
            assertThat(address.getValidTo()).isNull();
            assertThat(address.isCurrent()).isTrue();
        }

        @Test
        void shouldRejectEmptyStreet() {
            assertThatThrownBy(() -> new Address(
                personId,
                AddressType.MAIN,
                "",
                null,
                null,
                "8001",
                "Zürich",
                Canton.ZH,
                "CHE",
                regionId,
                LocalDate.of(2024, 1, 1),
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Street");
        }

        @Test
        void shouldRejectEmptyPostalCode() {
            assertThatThrownBy(() -> new Address(
                personId,
                AddressType.MAIN,
                "Bahnhofstrasse",
                null,
                null,
                "",
                "Zürich",
                Canton.ZH,
                "CHE",
                regionId,
                LocalDate.of(2024, 1, 1),
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Postal code");
        }

        @Test
        void shouldRejectEmptyCity() {
            assertThatThrownBy(() -> new Address(
                personId,
                AddressType.MAIN,
                "Bahnhofstrasse",
                null,
                null,
                "8001",
                "",
                Canton.ZH,
                "CHE",
                regionId,
                LocalDate.of(2024, 1, 1),
                null,
                null
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("City");
        }
    }

    @Nested
    @DisplayName("Address Validity")
    class AddressValidity {

        @Test
        void shouldBeCurrentWhenValidToIsNull() {
            Address address = createTestAddress(null);
            assertThat(address.isCurrent()).isTrue();
        }

        @Test
        void shouldNotBeCurrentWhenValidToIsInThePast() {
            Address address = createTestAddress(LocalDate.now().minusDays(1));
            assertThat(address.isCurrent()).isFalse();
        }

        @Test
        void shouldBeValidOnSpecificDateWithinRange() {
            Address address = createTestAddress(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
            );

            assertThat(address.isValidOn(LocalDate.of(2024, 6, 15))).isTrue();
            assertThat(address.isValidOn(LocalDate.of(2024, 1, 1))).isTrue();
            assertThat(address.isValidOn(LocalDate.of(2024, 12, 31))).isTrue();
        }

        @Test
        void shouldNotBeValidOnDateOutsideRange() {
            Address address = createTestAddress(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
            );

            assertThat(address.isValidOn(LocalDate.of(2023, 12, 31))).isFalse();
            assertThat(address.isValidOn(LocalDate.of(2025, 1, 1))).isFalse();
        }
    }

    @Nested
    @DisplayName("Address Closing")
    class AddressClosing {

        @Test
        void shouldCloseAddressWithEndDate() {
            Address address = createTestAddress(null);
            assertThat(address.isCurrent()).isTrue();

            address.close(LocalDate.of(2024, 8, 31));

            assertThat(address.getValidTo()).isEqualTo(LocalDate.of(2024, 8, 31));
            assertThat(address.isCurrent()).isFalse();
        }

        @Test
        void shouldRejectClosingWithDateBeforeValidFrom() {
            Address address = createTestAddress(LocalDate.of(2024, 1, 1), null);

            assertThatThrownBy(() -> address.close(LocalDate.of(2023, 12, 31)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("before");
        }
    }

    @Nested
    @DisplayName("Formatted Address")
    class FormattedAddress {

        @Test
        void shouldFormatAddressWithHouseNumber() {
            Address address = createTestAddress("Bahnhofstrasse", "42", "8001", "Zürich");

            assertThat(address.formattedStreet()).isEqualTo("Bahnhofstrasse 42");
            assertThat(address.formattedCity()).isEqualTo("8001 Zürich");
        }

        @Test
        void shouldFormatAddressWithoutHouseNumber() {
            Address address = createTestAddress("Bahnhofstrasse", null, "8001", "Zürich");

            assertThat(address.formattedStreet()).isEqualTo("Bahnhofstrasse");
        }

        @Test
        void shouldIncludeAdditionalLineInFormattedLines() {
            Address address = new Address(
                personId,
                AddressType.MAIN,
                "Bahnhofstrasse",
                "42",
                "c/o Muster AG",
                "8001",
                "Zürich",
                Canton.ZH,
                "CHE",
                regionId,
                LocalDate.of(2024, 1, 1),
                null,
                null
            );

            assertThat(address.formattedLines())
                .containsExactly("Bahnhofstrasse 42", "c/o Muster AG", "8001 Zürich");
        }
    }

    @Nested
    @DisplayName("Equality and String")
    class EqualityAndString {

        @Test
        void shouldCompareByIdAndProvideString() {
            Address first = createTestAddress(LocalDate.of(2024, 1, 1), null);
            Address second = createTestAddress(LocalDate.of(2024, 1, 1), null);
            UUID sharedId = UUID.randomUUID();

            first.setId(sharedId);
            second.setId(sharedId);

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
            assertThat(first.toString()).contains(sharedId.toString());
        }
    }

    @Nested
    @DisplayName("Setters and Getters")
    class SettersAndGetters {

        @Test
        void shouldUpdateFieldsUsingSetters() {
            Address address = createTestAddress(LocalDate.of(2024, 1, 1), null);
            UUID newPersonId = UUID.randomUUID();
            UUID premiumRegionId = UUID.randomUUID();
            UUID createdBy = UUID.randomUUID();

            address.setPersonId(newPersonId);
            address.setAddressType(AddressType.BILLING);
            address.setStreet("Seestrasse");
            address.setHouseNumber("10a");
            address.setAdditionalLine("Postfach 123");
            address.setPostalCode("8002");
            address.setCity("Zürich");
            address.setCanton(Canton.ZH);
            address.setCountry("DEU");
            address.setPremiumRegionId(premiumRegionId);
            address.setValidFrom(LocalDate.of(2024, 2, 1));
            address.setValidTo(LocalDate.of(2024, 12, 31));
            address.setRecordedAt(java.time.Instant.parse("2024-02-01T00:00:00Z"));
            address.setSupersededAt(java.time.Instant.parse("2024-03-01T00:00:00Z"));
            address.setCreatedBy(createdBy);

            assertThat(address.getPersonId()).isEqualTo(newPersonId);
            assertThat(address.getAddressType()).isEqualTo(AddressType.BILLING);
            assertThat(address.getStreet()).isEqualTo("Seestrasse");
            assertThat(address.getHouseNumber()).isEqualTo("10a");
            assertThat(address.getAdditionalLine()).isEqualTo("Postfach 123");
            assertThat(address.getPostalCode()).isEqualTo("8002");
            assertThat(address.getCity()).isEqualTo("Zürich");
            assertThat(address.getCanton()).isEqualTo(Canton.ZH);
            assertThat(address.getCountry()).isEqualTo("DEU");
            assertThat(address.getPremiumRegionId()).isEqualTo(premiumRegionId);
            assertThat(address.getValidFrom()).isEqualTo(LocalDate.of(2024, 2, 1));
            assertThat(address.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(address.getRecordedAt()).isEqualTo(java.time.Instant.parse("2024-02-01T00:00:00Z"));
            assertThat(address.getSupersededAt()).isEqualTo(java.time.Instant.parse("2024-03-01T00:00:00Z"));
            assertThat(address.getCreatedBy()).isEqualTo(createdBy);
        }

        @Test
        void shouldRejectBlankValuesInSetters() {
            Address address = createTestAddress(LocalDate.of(2024, 1, 1), null);

            assertThatThrownBy(() -> address.setStreet(" "))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> address.setPostalCode(" "))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> address.setCity(" "))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private Address createTestAddress(LocalDate validTo) {
        return createTestAddress(LocalDate.of(2024, 1, 1), validTo);
    }

    private Address createTestAddress(LocalDate validFrom, LocalDate validTo) {
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
            regionId,
            validFrom,
            validTo,
            null
        );
    }

    private Address createTestAddress(String street, String houseNumber, String postalCode, String city) {
        return new Address(
            personId,
            AddressType.MAIN,
            street,
            houseNumber,
            null,
            postalCode,
            city,
            Canton.ZH,
            "CHE",
            regionId,
            LocalDate.of(2024, 1, 1),
            null,
            null
        );
    }
}

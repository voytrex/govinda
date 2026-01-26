/*
 * Govinda ERP - Address Entity Tests
 * Copyright 2024 Voytrex
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

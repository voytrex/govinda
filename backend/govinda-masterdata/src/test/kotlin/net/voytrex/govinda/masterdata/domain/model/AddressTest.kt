/*
 * Govinda ERP - Address Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model

import net.voytrex.govinda.common.domain.model.AddressType
import net.voytrex.govinda.common.domain.model.Canton
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class AddressTest {

    private val personId = UUID.randomUUID()
    private val regionId = UUID.randomUUID()

    @Nested
    inner class `Address Creation` {

        @Test
        fun `should create address with valid data`() {
            val address = Address(
                personId = personId,
                addressType = AddressType.MAIN,
                street = "Bahnhofstrasse",
                houseNumber = "42",
                postalCode = "8001",
                city = "Zürich",
                canton = Canton.ZH,
                premiumRegionId = regionId,
                validFrom = LocalDate.of(2024, 1, 1)
            )

            assertThat(address.id).isNotNull()
            assertThat(address.street).isEqualTo("Bahnhofstrasse")
            assertThat(address.postalCode).isEqualTo("8001")
            assertThat(address.city).isEqualTo("Zürich")
            assertThat(address.canton).isEqualTo(Canton.ZH)
            assertThat(address.validTo).isNull()
            assertThat(address.isCurrent()).isTrue()
        }

        @Test
        fun `should reject empty street`() {
            assertThatThrownBy {
                Address(
                    personId = personId,
                    addressType = AddressType.MAIN,
                    street = "",
                    postalCode = "8001",
                    city = "Zürich",
                    canton = Canton.ZH,
                    premiumRegionId = regionId,
                    validFrom = LocalDate.of(2024, 1, 1)
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Street")
        }

        @Test
        fun `should reject empty postal code`() {
            assertThatThrownBy {
                Address(
                    personId = personId,
                    addressType = AddressType.MAIN,
                    street = "Bahnhofstrasse",
                    postalCode = "",
                    city = "Zürich",
                    canton = Canton.ZH,
                    premiumRegionId = regionId,
                    validFrom = LocalDate.of(2024, 1, 1)
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Postal code")
        }

        @Test
        fun `should reject empty city`() {
            assertThatThrownBy {
                Address(
                    personId = personId,
                    addressType = AddressType.MAIN,
                    street = "Bahnhofstrasse",
                    postalCode = "8001",
                    city = "",
                    canton = Canton.ZH,
                    premiumRegionId = regionId,
                    validFrom = LocalDate.of(2024, 1, 1)
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("City")
        }
    }

    @Nested
    inner class `Address Validity` {

        @Test
        fun `should be current when validTo is null`() {
            val address = createTestAddress(validTo = null)
            assertThat(address.isCurrent()).isTrue()
        }

        @Test
        fun `should not be current when validTo is in the past`() {
            val address = createTestAddress(validTo = LocalDate.now().minusDays(1))
            assertThat(address.isCurrent()).isFalse()
        }

        @Test
        fun `should be valid on a specific date within range`() {
            val address = createTestAddress(
                validFrom = LocalDate.of(2024, 1, 1),
                validTo = LocalDate.of(2024, 12, 31)
            )

            assertThat(address.isValidOn(LocalDate.of(2024, 6, 15))).isTrue()
            assertThat(address.isValidOn(LocalDate.of(2024, 1, 1))).isTrue()
            assertThat(address.isValidOn(LocalDate.of(2024, 12, 31))).isTrue()
        }

        @Test
        fun `should not be valid on a date outside range`() {
            val address = createTestAddress(
                validFrom = LocalDate.of(2024, 1, 1),
                validTo = LocalDate.of(2024, 12, 31)
            )

            assertThat(address.isValidOn(LocalDate.of(2023, 12, 31))).isFalse()
            assertThat(address.isValidOn(LocalDate.of(2025, 1, 1))).isFalse()
        }
    }

    @Nested
    inner class `Address Closing` {

        @Test
        fun `should close address with end date`() {
            val address = createTestAddress()
            assertThat(address.isCurrent()).isTrue()

            address.close(LocalDate.of(2024, 8, 31))

            assertThat(address.validTo).isEqualTo(LocalDate.of(2024, 8, 31))
            assertThat(address.isCurrent()).isFalse()
        }

        @Test
        fun `should reject closing with date before validFrom`() {
            val address = createTestAddress(validFrom = LocalDate.of(2024, 1, 1))

            assertThatThrownBy {
                address.close(LocalDate.of(2023, 12, 31))
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("before")
        }
    }

    @Nested
    inner class `Formatted Address` {

        @Test
        fun `should format address with house number`() {
            val address = createTestAddress(
                street = "Bahnhofstrasse",
                houseNumber = "42",
                postalCode = "8001",
                city = "Zürich"
            )

            assertThat(address.formattedStreet()).isEqualTo("Bahnhofstrasse 42")
            assertThat(address.formattedCity()).isEqualTo("8001 Zürich")
        }

        @Test
        fun `should format address without house number`() {
            val address = createTestAddress(
                street = "Bahnhofstrasse",
                houseNumber = null,
                postalCode = "8001",
                city = "Zürich"
            )

            assertThat(address.formattedStreet()).isEqualTo("Bahnhofstrasse")
        }
    }

    private fun createTestAddress(
        street: String = "Bahnhofstrasse",
        houseNumber: String? = "42",
        postalCode: String = "8001",
        city: String = "Zürich",
        validFrom: LocalDate = LocalDate.of(2024, 1, 1),
        validTo: LocalDate? = null
    ) = Address(
        personId = personId,
        addressType = AddressType.MAIN,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        city = city,
        canton = Canton.ZH,
        premiumRegionId = regionId,
        validFrom = validFrom,
        validTo = validTo
    )
}

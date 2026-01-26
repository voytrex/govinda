/*
 * Govinda ERP - AHV Number Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AhvNumberTest {

    @Nested
    inner class `Valid AHV Numbers` {

        @Test
        fun `should accept valid AHV number format`() {
            val ahv = AhvNumber("756.1234.5678.90")

            assertThat(ahv.value).isEqualTo("756.1234.5678.90")
        }

        @Test
        fun `should return unformatted number`() {
            val ahv = AhvNumber("756.1234.5678.90")

            assertThat(ahv.toUnformatted()).isEqualTo("7561234567890")
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "756.0000.0000.00",
            "756.9999.9999.99",
            "756.1234.5678.90"
        ])
        fun `should accept various valid formats`(value: String) {
            val ahv = AhvNumber(value)
            assertThat(ahv.value).isEqualTo(value)
        }
    }

    @Nested
    inner class `Invalid AHV Numbers` {

        @Test
        fun `should reject AHV not starting with 756`() {
            assertThatThrownBy { AhvNumber("757.1234.5678.90") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Invalid AHV number format")
        }

        @Test
        fun `should reject AHV without dots`() {
            assertThatThrownBy { AhvNumber("7561234567890") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Invalid AHV number format")
        }

        @Test
        fun `should reject AHV with wrong segment lengths`() {
            assertThatThrownBy { AhvNumber("756.123.5678.90") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should reject AHV with letters`() {
            assertThatThrownBy { AhvNumber("756.123A.5678.90") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @ParameterizedTest
        @ValueSource(strings = [
            "",
            "756",
            "756.1234.5678",
            "756.1234.5678.901",
            "abc.defg.hijk.lm"
        ])
        fun `should reject various invalid formats`(value: String) {
            assertThatThrownBy { AhvNumber(value) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class `Creating from Unformatted` {

        @Test
        fun `should create from 13 digit string`() {
            val ahv = AhvNumber.fromUnformatted("7561234567890")

            assertThat(ahv.value).isEqualTo("756.1234.5678.90")
        }

        @Test
        fun `should reject non-13 digit string`() {
            assertThatThrownBy { AhvNumber.fromUnformatted("756123456789") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("13 digits")
        }

        @Test
        fun `should reject string with non-digits`() {
            assertThatThrownBy { AhvNumber.fromUnformatted("756123456789A") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class `Format Validation` {

        @Test
        fun `isValidFormat should return true for valid format`() {
            assertThat(AhvNumber.isValidFormat("756.1234.5678.90")).isTrue()
        }

        @Test
        fun `isValidFormat should return false for invalid format`() {
            assertThat(AhvNumber.isValidFormat("invalid")).isFalse()
        }
    }

    @Nested
    inner class `Equality` {

        @Test
        fun `should be equal for same value`() {
            val ahv1 = AhvNumber("756.1234.5678.90")
            val ahv2 = AhvNumber("756.1234.5678.90")

            assertThat(ahv1).isEqualTo(ahv2)
            assertThat(ahv1.hashCode()).isEqualTo(ahv2.hashCode())
        }

        @Test
        fun `should not be equal for different values`() {
            val ahv1 = AhvNumber("756.1234.5678.90")
            val ahv2 = AhvNumber("756.1234.5678.91")

            assertThat(ahv1).isNotEqualTo(ahv2)
        }
    }
}

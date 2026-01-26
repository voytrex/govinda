/*
 * Govinda ERP - AHV Number Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AhvNumberTest {

    @Nested
    @DisplayName("Valid AHV Numbers")
    class ValidAhvNumbers {

        @Test
        void shouldAcceptValidAhvNumberFormat() {
            AhvNumber ahv = new AhvNumber("756.1234.5678.90");

            assertThat(ahv.getValue()).isEqualTo("756.1234.5678.90");
        }

        @Test
        void shouldReturnUnformattedNumber() {
            AhvNumber ahv = new AhvNumber("756.1234.5678.90");

            assertThat(ahv.toUnformatted()).isEqualTo("7561234567890");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "756.0000.0000.00",
            "756.9999.9999.99",
            "756.1234.5678.90"
        })
        void shouldAcceptVariousValidFormats(String value) {
            AhvNumber ahv = new AhvNumber(value);
            assertThat(ahv.getValue()).isEqualTo(value);
        }
    }

    @Nested
    @DisplayName("Invalid AHV Numbers")
    class InvalidAhvNumbers {

        @Test
        void shouldRejectAhvNotStartingWith756() {
            assertThatThrownBy(() -> new AhvNumber("757.1234.5678.90"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid AHV number format");
        }

        @Test
        void shouldRejectAhvWithoutDots() {
            assertThatThrownBy(() -> new AhvNumber("7561234567890"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid AHV number format");
        }

        @Test
        void shouldRejectAhvWithWrongSegmentLengths() {
            assertThatThrownBy(() -> new AhvNumber("756.123.5678.90"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldRejectAhvWithLetters() {
            assertThatThrownBy(() -> new AhvNumber("756.123A.5678.90"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "",
            "756",
            "756.1234.5678",
            "756.1234.5678.901",
            "abc.defg.hijk.lm"
        })
        void shouldRejectVariousInvalidFormats(String value) {
            assertThatThrownBy(() -> new AhvNumber(value))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Creating from Unformatted")
    class CreatingFromUnformatted {

        @Test
        void shouldCreateFrom13DigitString() {
            AhvNumber ahv = AhvNumber.fromUnformatted("7561234567890");

            assertThat(ahv.getValue()).isEqualTo("756.1234.5678.90");
        }

        @Test
        void shouldRejectNon13DigitString() {
            assertThatThrownBy(() -> AhvNumber.fromUnformatted("756123456789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("13 digits");
        }

        @Test
        void shouldRejectStringWithNonDigits() {
            assertThatThrownBy(() -> AhvNumber.fromUnformatted("756123456789A"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Format Validation")
    class FormatValidation {

        @Test
        void isValidFormatShouldReturnTrueForValidFormat() {
            assertThat(AhvNumber.isValidFormat("756.1234.5678.90")).isTrue();
        }

        @Test
        void isValidFormatShouldReturnFalseForInvalidFormat() {
            assertThat(AhvNumber.isValidFormat("invalid")).isFalse();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        void shouldBeEqualForSameValue() {
            AhvNumber ahv1 = new AhvNumber("756.1234.5678.90");
            AhvNumber ahv2 = new AhvNumber("756.1234.5678.90");

            assertThat(ahv1).isEqualTo(ahv2);
            assertThat(ahv1.hashCode()).isEqualTo(ahv2.hashCode());
        }

        @Test
        void shouldNotBeEqualForDifferentValues() {
            AhvNumber ahv1 = new AhvNumber("756.1234.5678.90");
            AhvNumber ahv2 = new AhvNumber("756.1234.5678.91");

            assertThat(ahv1).isNotEqualTo(ahv2);
        }
    }
}

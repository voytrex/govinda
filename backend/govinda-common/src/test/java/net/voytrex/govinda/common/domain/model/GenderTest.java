/*
 * Govinda ERP - Gender Domain Model Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class GenderTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeGenderCode() {
            assertThat(Gender.MALE.getCode()).isEqualTo("M");
            assertThat(Gender.FEMALE.getCode()).isEqualTo("F");
            assertThat(Gender.OTHER.getCode()).isEqualTo("O");
        }
    }

    @Nested
    @DisplayName("Lookup")
    class Lookup {

        @Test
        void shouldResolveGenderFromCodeIgnoringCase() {
            for (Gender gender : Gender.values()) {
                assertThat(Gender.fromCode(gender.getCode().toLowerCase())).isEqualTo(gender);
            }
        }

        @Test
        void shouldThrowWhenGenderCodeUnknown() {
            assertThatThrownBy(() -> Gender.fromCode("X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown gender code");
        }
    }
}

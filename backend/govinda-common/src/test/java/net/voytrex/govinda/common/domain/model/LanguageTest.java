/*
 * Govinda ERP - Language Domain Model Tests
 * Copyright 2024 Voytrex
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
class LanguageTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeLanguageCode() {
            assertThat(Language.DE.getCode()).isEqualTo("de");
            assertThat(Language.FR.getCode()).isEqualTo("fr");
            assertThat(Language.IT.getCode()).isEqualTo("it");
            assertThat(Language.EN.getCode()).isEqualTo("en");
        }
    }

    @Nested
    @DisplayName("Lookup")
    class Lookup {

        @Test
        void shouldResolveLanguageFromCodeIgnoringCase() {
            for (Language language : Language.values()) {
                assertThat(Language.fromCode(language.getCode().toUpperCase())).isEqualTo(language);
            }
        }

        @Test
        void shouldThrowWhenLanguageCodeUnknown() {
            assertThatThrownBy(() -> Language.fromCode("xx"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown language code");
        }
    }
}

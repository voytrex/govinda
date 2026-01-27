/*
 * Govinda ERP - Localized Text Domain Model Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class LocalizedTextTest {

    @Nested
    @DisplayName("Factories")
    class Factories {

        @Test
        void shouldCreateLocalizedTextFromSingleValue() {
            LocalizedText text = LocalizedText.of("hello");

            assertThat(text.getDe()).isEqualTo("hello");
            assertThat(text.getFr()).isEqualTo("hello");
            assertThat(text.getIt()).isEqualTo("hello");
            assertThat(text.getEn()).isEqualTo("hello");
        }

        @Test
        void shouldCreateEmptyLocalizedText() {
            LocalizedText text = LocalizedText.empty();

            assertThat(text.getDe()).isEmpty();
            assertThat(text.getFr()).isEmpty();
            assertThat(text.getIt()).isEmpty();
            assertThat(text.getEn()).isEmpty();
            assertThat(text.isBlank()).isTrue();
        }
    }

    @Nested
    @DisplayName("Lookup")
    class Lookup {

        @Test
        void shouldReturnLanguageSpecificValue() {
            LocalizedText text = new LocalizedText("Hallo", "Bonjour", "Ciao", "Hello");

            assertThat(text.get(Language.DE)).isEqualTo("Hallo");
            assertThat(text.get(Language.FR)).isEqualTo("Bonjour");
            assertThat(text.get(Language.IT)).isEqualTo("Ciao");
            assertThat(text.get(Language.EN)).isEqualTo("Hello");
        }

        @Test
        void shouldFallbackToGermanWhenTranslationBlank() {
            LocalizedText text = new LocalizedText("Hallo", "", null, "Hello");

            assertThat(text.get(Language.FR)).isEqualTo("Hallo");
            assertThat(text.get(Language.IT)).isEqualTo("Hallo");
            assertThat(text.get(Language.EN)).isEqualTo("Hello");
        }
    }

    @Nested
    @DisplayName("Mutation")
    class Mutation {

        @Test
        void shouldAllowUpdatingLanguageValues() {
            LocalizedText text = new LocalizedText("a", "b", "c", "d");

            text.setDe("de");
            text.setFr("fr");
            text.setIt("it");
            text.setEn("en");

            assertThat(text.getDe()).isEqualTo("de");
            assertThat(text.getFr()).isEqualTo("fr");
            assertThat(text.getIt()).isEqualTo("it");
            assertThat(text.getEn()).isEqualTo("en");
            assertThat(text.isBlank()).isFalse();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        void shouldCompareByAllLanguageValues() {
            LocalizedText first = new LocalizedText("a", "b", "c", "d");
            LocalizedText second = new LocalizedText("a", "b", "c", "d");
            LocalizedText third = new LocalizedText("a", "b", "c", "x");

            assertThat(first).isEqualTo(second);
            assertThat(first).isNotEqualTo(third);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }
}

/*
 * Govinda ERP - Canton Domain Model Tests
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
class CantonTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeCantonCode() {
            for (Canton canton : Canton.values()) {
                assertThat(canton.getCode()).isEqualTo(canton.name());
            }
        }

        @Test
        void shouldResolveCantonFromCodeIgnoringCase() {
            for (Canton canton : Canton.values()) {
                assertThat(Canton.fromCode(canton.getCode().toLowerCase())).isEqualTo(canton);
            }
        }

        @Test
        void shouldThrowWhenCantonCodeUnknown() {
            assertThatThrownBy(() -> Canton.fromCode("XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown canton code");
        }
    }
}

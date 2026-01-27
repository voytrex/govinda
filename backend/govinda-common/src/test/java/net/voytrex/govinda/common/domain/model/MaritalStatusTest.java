/*
 * Govinda ERP - Marital Status Domain Model Tests
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
class MaritalStatusTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeMaritalStatusCode() {
            assertThat(MaritalStatus.SINGLE.getCode()).isEqualTo("S");
            assertThat(MaritalStatus.MARRIED.getCode()).isEqualTo("M");
            assertThat(MaritalStatus.DIVORCED.getCode()).isEqualTo("D");
            assertThat(MaritalStatus.WIDOWED.getCode()).isEqualTo("W");
            assertThat(MaritalStatus.REGISTERED_PARTNERSHIP.getCode()).isEqualTo("P");
            assertThat(MaritalStatus.DISSOLVED_PARTNERSHIP.getCode()).isEqualTo("DP");
        }

        @Test
        void shouldResolveMaritalStatusFromCodeIgnoringCase() {
            for (MaritalStatus status : MaritalStatus.values()) {
                assertThat(MaritalStatus.fromCode(status.getCode().toLowerCase())).isEqualTo(status);
            }
        }

        @Test
        void shouldThrowWhenMaritalStatusCodeUnknown() {
            assertThatThrownBy(() -> MaritalStatus.fromCode("XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown marital status code");
        }
    }
}

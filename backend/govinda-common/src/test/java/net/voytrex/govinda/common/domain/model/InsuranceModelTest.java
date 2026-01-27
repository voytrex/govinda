/*
 * Govinda ERP - Insurance Model Domain Model Tests
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
class InsuranceModelTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeInsuranceModelCode() {
            assertThat(InsuranceModel.STANDARD.getCode()).isEqualTo("STD");
            assertThat(InsuranceModel.HMO.getCode()).isEqualTo("HMO");
            assertThat(InsuranceModel.HAUSARZT.getCode()).isEqualTo("HAM");
            assertThat(InsuranceModel.TELMED.getCode()).isEqualTo("TLM");
        }

        @Test
        void shouldResolveInsuranceModelFromCodeIgnoringCase() {
            for (InsuranceModel model : InsuranceModel.values()) {
                assertThat(InsuranceModel.fromCode(model.getCode().toLowerCase())).isEqualTo(model);
            }
        }

        @Test
        void shouldThrowWhenInsuranceModelCodeUnknown() {
            assertThatThrownBy(() -> InsuranceModel.fromCode("XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown insurance model code");
        }
    }

    @Nested
    @DisplayName("Provider Restrictions")
    class ProviderRestrictions {

        @Test
        void shouldFlagModelsWithProviderRestrictions() {
            assertThat(InsuranceModel.STANDARD.hasProviderRestriction()).isFalse();
            assertThat(InsuranceModel.HMO.hasProviderRestriction()).isTrue();
            assertThat(InsuranceModel.HAUSARZT.hasProviderRestriction()).isTrue();
            assertThat(InsuranceModel.TELMED.hasProviderRestriction()).isTrue();
        }
    }
}

/*
 * Govinda ERP - Billing Frequency Domain Model Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class BillingFrequencyTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeBillingFrequencyCode() {
            assertThat(BillingFrequency.MONTHLY.getCode()).isEqualTo("M");
            assertThat(BillingFrequency.QUARTERLY.getCode()).isEqualTo("Q");
            assertThat(BillingFrequency.SEMI_ANNUAL.getCode()).isEqualTo("S");
            assertThat(BillingFrequency.ANNUAL.getCode()).isEqualTo("A");
        }

        @Test
        void shouldResolveBillingFrequencyFromCodeIgnoringCase() {
            for (BillingFrequency frequency : BillingFrequency.values()) {
                assertThat(BillingFrequency.fromCode(frequency.getCode().toLowerCase())).isEqualTo(frequency);
            }
        }

        @Test
        void shouldThrowWhenBillingFrequencyCodeUnknown() {
            assertThatThrownBy(() -> BillingFrequency.fromCode("XX"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown billing frequency code");
        }
    }

    @Nested
    @DisplayName("Periods Per Year")
    class PeriodsPerYear {

        @Test
        void shouldExposePeriodsPerYear() {
            assertThat(BillingFrequency.MONTHLY.getPeriodsPerYear()).isEqualTo(12);
            assertThat(BillingFrequency.QUARTERLY.getPeriodsPerYear()).isEqualTo(4);
            assertThat(BillingFrequency.SEMI_ANNUAL.getPeriodsPerYear()).isEqualTo(2);
            assertThat(BillingFrequency.ANNUAL.getPeriodsPerYear()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Discount")
    class Discount {

        @Test
        void shouldExposeDiscountPercent() {
            assertThat(BillingFrequency.MONTHLY.getDiscountPercent()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(BillingFrequency.QUARTERLY.getDiscountPercent()).isEqualByComparingTo(new BigDecimal("0.5"));
            assertThat(BillingFrequency.SEMI_ANNUAL.getDiscountPercent()).isEqualByComparingTo(new BigDecimal("1.0"));
            assertThat(BillingFrequency.ANNUAL.getDiscountPercent()).isEqualByComparingTo(new BigDecimal("2.0"));
        }
    }
}

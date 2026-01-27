/*
 * Govinda ERP - Product Category Domain Model Tests
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
class ProductCategoryTest {

    @Nested
    @DisplayName("Code")
    class Code {

        @Test
        void shouldExposeProductCategoryCodes() {
            assertThat(ProductCategory.BASIC.getCode()).isEqualTo("BASIC");
            assertThat(ProductCategory.HOSPITAL.getCode()).isEqualTo("HOSP");
            assertThat(ProductCategory.DENTAL.getCode()).isEqualTo("DENT");
            assertThat(ProductCategory.ALTERNATIVE.getCode()).isEqualTo("ALT");
            assertThat(ProductCategory.TRAVEL.getCode()).isEqualTo("TRAV");
            assertThat(ProductCategory.DAILY_ALLOWANCE.getCode()).isEqualTo("TAGG");
        }
    }
}

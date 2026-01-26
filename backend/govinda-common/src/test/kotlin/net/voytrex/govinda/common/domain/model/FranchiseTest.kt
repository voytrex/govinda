/*
 * Govinda ERP - Franchise Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FranchiseTest {

    @Nested
    inner class `Franchise Options for Age Groups` {

        @Test
        fun `children should have 0 to 600 CHF options`() {
            val childFranchises = Franchise.forAgeGroup(AgeGroup.CHILD)

            assertThat(childFranchises).containsExactlyInAnyOrder(
                Franchise.CHF_0,
                Franchise.CHF_100,
                Franchise.CHF_200,
                Franchise.CHF_300,
                Franchise.CHF_400,
                Franchise.CHF_600
            )
        }

        @Test
        fun `adults should have 300 to 2500 CHF options`() {
            val adultFranchises = Franchise.forAgeGroup(AgeGroup.ADULT)

            assertThat(adultFranchises).containsExactlyInAnyOrder(
                Franchise.CHF_300,
                Franchise.CHF_500,
                Franchise.CHF_1000,
                Franchise.CHF_1500,
                Franchise.CHF_2000,
                Franchise.CHF_2500
            )
        }

        @Test
        fun `young adults should have same options as adults`() {
            val youngAdultFranchises = Franchise.forAgeGroup(AgeGroup.YOUNG_ADULT)
            val adultFranchises = Franchise.forAgeGroup(AgeGroup.ADULT)

            assertThat(youngAdultFranchises).isEqualTo(adultFranchises)
        }
    }

    @Nested
    inner class `Default Franchise` {

        @Test
        fun `default for children is 0 CHF`() {
            assertThat(Franchise.defaultFor(AgeGroup.CHILD)).isEqualTo(Franchise.CHF_0)
        }

        @Test
        fun `default for young adults is 300 CHF`() {
            assertThat(Franchise.defaultFor(AgeGroup.YOUNG_ADULT)).isEqualTo(Franchise.CHF_300)
        }

        @Test
        fun `default for adults is 300 CHF`() {
            assertThat(Franchise.defaultFor(AgeGroup.ADULT)).isEqualTo(Franchise.CHF_300)
        }
    }

    @Nested
    inner class `Franchise Amounts` {

        @Test
        fun `should have correct amounts`() {
            assertThat(Franchise.CHF_0.amount).isEqualTo(0)
            assertThat(Franchise.CHF_300.amount).isEqualTo(300)
            assertThat(Franchise.CHF_2500.amount).isEqualTo(2500)
        }

        @Test
        fun `should create from amount`() {
            assertThat(Franchise.fromAmount(300)).isEqualTo(Franchise.CHF_300)
            assertThat(Franchise.fromAmount(2500)).isEqualTo(Franchise.CHF_2500)
        }
    }
}

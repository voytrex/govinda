/*
 * Govinda ERP - Age Group Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AgeGroupTest {

    @Nested
    inner class `Age Group Determination` {

        @ParameterizedTest
        @CsvSource(
            "0, CHILD",
            "1, CHILD",
            "10, CHILD",
            "17, CHILD",
            "18, CHILD"
        )
        fun `children are 0 to 18 years old`(age: Int, expected: AgeGroup) {
            assertThat(AgeGroup.forAge(age)).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            "19, YOUNG_ADULT",
            "20, YOUNG_ADULT",
            "24, YOUNG_ADULT",
            "25, YOUNG_ADULT"
        )
        fun `young adults are 19 to 25 years old`(age: Int, expected: AgeGroup) {
            assertThat(AgeGroup.forAge(age)).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            "26, ADULT",
            "30, ADULT",
            "50, ADULT",
            "100, ADULT"
        )
        fun `adults are 26 and older`(age: Int, expected: AgeGroup) {
            assertThat(AgeGroup.forAge(age)).isEqualTo(expected)
        }
    }

    @Nested
    inner class `Age Range Boundaries` {

        @Test
        fun `child age range is 0 to 18`() {
            assertThat(AgeGroup.CHILD.minAge).isEqualTo(0)
            assertThat(AgeGroup.CHILD.maxAge).isEqualTo(18)
        }

        @Test
        fun `young adult age range is 19 to 25`() {
            assertThat(AgeGroup.YOUNG_ADULT.minAge).isEqualTo(19)
            assertThat(AgeGroup.YOUNG_ADULT.maxAge).isEqualTo(25)
        }

        @Test
        fun `adult age range starts at 26 with no upper limit`() {
            assertThat(AgeGroup.ADULT.minAge).isEqualTo(26)
            assertThat(AgeGroup.ADULT.maxAge).isNull()
        }
    }

    @Nested
    inner class `Premium Relevance` {

        @Test
        fun `age group transitions affect premium calculation`() {
            // On 18th birthday: still CHILD
            assertThat(AgeGroup.forAge(18)).isEqualTo(AgeGroup.CHILD)

            // On 19th birthday: becomes YOUNG_ADULT (lower premium than ADULT)
            assertThat(AgeGroup.forAge(19)).isEqualTo(AgeGroup.YOUNG_ADULT)

            // On 26th birthday: becomes ADULT (full premium)
            assertThat(AgeGroup.forAge(26)).isEqualTo(AgeGroup.ADULT)
        }
    }
}

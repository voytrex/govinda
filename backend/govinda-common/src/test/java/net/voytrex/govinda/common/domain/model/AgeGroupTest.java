/*
 * Govinda ERP - Age Group Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AgeGroupTest {

    @Nested
    @DisplayName("Age Group Determination")
    class AgeGroupDetermination {

        @ParameterizedTest
        @CsvSource({
            "0, CHILD",
            "1, CHILD",
            "10, CHILD",
            "17, CHILD",
            "18, CHILD"
        })
        void childrenAreZeroTo18(int age, AgeGroup expected) {
            assertThat(AgeGroup.forAge(age)).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "19, YOUNG_ADULT",
            "20, YOUNG_ADULT",
            "24, YOUNG_ADULT",
            "25, YOUNG_ADULT"
        })
        void youngAdultsAre19To25(int age, AgeGroup expected) {
            assertThat(AgeGroup.forAge(age)).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "26, ADULT",
            "30, ADULT",
            "50, ADULT",
            "100, ADULT"
        })
        void adultsAre26AndOlder(int age, AgeGroup expected) {
            assertThat(AgeGroup.forAge(age)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Age Range Boundaries")
    class AgeRangeBoundaries {

        @Test
        void childAgeRangeIsZeroTo18() {
            assertThat(AgeGroup.CHILD.getMinAge()).isEqualTo(0);
            assertThat(AgeGroup.CHILD.getMaxAge()).isEqualTo(18);
        }

        @Test
        void youngAdultAgeRangeIs19To25() {
            assertThat(AgeGroup.YOUNG_ADULT.getMinAge()).isEqualTo(19);
            assertThat(AgeGroup.YOUNG_ADULT.getMaxAge()).isEqualTo(25);
        }

        @Test
        void adultAgeRangeStartsAt26WithNoUpperLimit() {
            assertThat(AgeGroup.ADULT.getMinAge()).isEqualTo(26);
            assertThat(AgeGroup.ADULT.getMaxAge()).isNull();
        }
    }

    @Nested
    @DisplayName("Premium Relevance")
    class PremiumRelevance {

        @Test
        void ageGroupTransitionsAffectPremiumCalculation() {
            assertThat(AgeGroup.forAge(18)).isEqualTo(AgeGroup.CHILD);
            assertThat(AgeGroup.forAge(19)).isEqualTo(AgeGroup.YOUNG_ADULT);
            assertThat(AgeGroup.forAge(26)).isEqualTo(AgeGroup.ADULT);
        }
    }
}

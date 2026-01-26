/*
 * Govinda ERP - Franchise Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FranchiseTest {

    @Nested
    @DisplayName("Franchise Options for Age Groups")
    class FranchiseOptionsForAgeGroups {

        @Test
        void childrenShouldHave0To600Options() {
            var childFranchises = Franchise.forAgeGroup(AgeGroup.CHILD);

            assertThat(childFranchises).containsExactlyInAnyOrder(
                Franchise.CHF_0,
                Franchise.CHF_100,
                Franchise.CHF_200,
                Franchise.CHF_300,
                Franchise.CHF_400,
                Franchise.CHF_600
            );
        }

        @Test
        void adultsShouldHave300To2500Options() {
            var adultFranchises = Franchise.forAgeGroup(AgeGroup.ADULT);

            assertThat(adultFranchises).containsExactlyInAnyOrder(
                Franchise.CHF_300,
                Franchise.CHF_500,
                Franchise.CHF_1000,
                Franchise.CHF_1500,
                Franchise.CHF_2000,
                Franchise.CHF_2500
            );
        }

        @Test
        void youngAdultsShouldHaveSameOptionsAsAdults() {
            var youngAdultFranchises = Franchise.forAgeGroup(AgeGroup.YOUNG_ADULT);
            var adultFranchises = Franchise.forAgeGroup(AgeGroup.ADULT);

            assertThat(youngAdultFranchises).isEqualTo(adultFranchises);
        }
    }

    @Nested
    @DisplayName("Default Franchise")
    class DefaultFranchise {

        @Test
        void defaultForChildrenIsZero() {
            assertThat(Franchise.defaultFor(AgeGroup.CHILD)).isEqualTo(Franchise.CHF_0);
        }

        @Test
        void defaultForYoungAdultsIs300() {
            assertThat(Franchise.defaultFor(AgeGroup.YOUNG_ADULT)).isEqualTo(Franchise.CHF_300);
        }

        @Test
        void defaultForAdultsIs300() {
            assertThat(Franchise.defaultFor(AgeGroup.ADULT)).isEqualTo(Franchise.CHF_300);
        }
    }

    @Nested
    @DisplayName("Franchise Amounts")
    class FranchiseAmounts {

        @Test
        void shouldHaveCorrectAmounts() {
            assertThat(Franchise.CHF_0.getAmount()).isEqualTo(0);
            assertThat(Franchise.CHF_300.getAmount()).isEqualTo(300);
            assertThat(Franchise.CHF_2500.getAmount()).isEqualTo(2500);
        }

        @Test
        void shouldCreateFromAmount() {
            assertThat(Franchise.fromAmount(300)).isEqualTo(Franchise.CHF_300);
            assertThat(Franchise.fromAmount(2500)).isEqualTo(Franchise.CHF_2500);
        }
    }
}

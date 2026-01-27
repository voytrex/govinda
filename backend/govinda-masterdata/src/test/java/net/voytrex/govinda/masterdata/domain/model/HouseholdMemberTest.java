/*
 * Govinda ERP - Household Member Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.HouseholdRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HouseholdMemberTest {

    @Nested
    @DisplayName("Current Membership")
    class CurrentMembership {

        @Test
        void shouldBeCurrentWhenValidToIsNull() {
            HouseholdMember member = createMember(null);
            assertThat(member.isCurrent()).isTrue();
        }

        @Test
        void shouldNotBeCurrentWhenValidToIsInPast() {
            HouseholdMember member = createMember(LocalDate.now().minusDays(1));
            assertThat(member.isCurrent()).isFalse();
        }

        @Test
        void shouldBeCurrentWhenValidToIsToday() {
            HouseholdMember member = createMember(LocalDate.now());
            assertThat(member.isCurrent()).isTrue();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        void shouldCompareById() {
            HouseholdMember first = createMember(null);
            HouseholdMember second = createMember(null);
            UUID sharedId = UUID.randomUUID();

            first.setId(sharedId);
            second.setId(sharedId);

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }
    }

    @Nested
    @DisplayName("Setters and Getters")
    class SettersAndGetters {

        @Test
        void shouldUpdateFieldsUsingSetters() {
            HouseholdMember member = createMember(null);
            UUID newHouseholdId = UUID.randomUUID();
            UUID newPersonId = UUID.randomUUID();

            member.setHouseholdId(newHouseholdId);
            member.setPersonId(newPersonId);
            member.setRole(HouseholdRole.PARTNER);
            member.setValidFrom(LocalDate.of(2024, 2, 1));
            member.setValidTo(LocalDate.of(2024, 12, 31));

            assertThat(member.getHouseholdId()).isEqualTo(newHouseholdId);
            assertThat(member.getPersonId()).isEqualTo(newPersonId);
            assertThat(member.getRole()).isEqualTo(HouseholdRole.PARTNER);
            assertThat(member.getValidFrom()).isEqualTo(LocalDate.of(2024, 2, 1));
            assertThat(member.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        }
    }

    private HouseholdMember createMember(LocalDate validTo) {
        HouseholdMember member = new HouseholdMember(
            UUID.randomUUID(),
            UUID.randomUUID(),
            HouseholdRole.PRIMARY,
            LocalDate.of(2024, 1, 1)
        );
        member.setValidTo(validTo);
        return member;
    }
}

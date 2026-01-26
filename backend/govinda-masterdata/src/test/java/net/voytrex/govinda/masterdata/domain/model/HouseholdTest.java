/*
 * Govinda ERP - Household Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.HouseholdRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HouseholdTest {

    private final UUID tenantId = UUID.randomUUID();

    @Nested
    @DisplayName("Household Creation")
    class HouseholdCreation {

        @Test
        void shouldCreateHouseholdWithName() {
            Household household = new Household(tenantId, "Familie Müller");

            assertThat(household.getId()).isNotNull();
            assertThat(household.getName()).isEqualTo("Familie Müller");
            assertThat(household.getMembers()).isEmpty();
        }

        @Test
        void shouldRejectEmptyName() {
            assertThatThrownBy(() -> new Household(tenantId, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
        }
    }

    @Nested
    @DisplayName("Member Management")
    class MemberManagement {

        @Test
        void shouldAddPrimaryMember() {
            Household household = createTestHousehold();
            UUID personId = UUID.randomUUID();

            household.addMember(personId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));

            assertThat(household.getMembers()).hasSize(1);
            assertThat(household.getMembers().get(0).getPersonId()).isEqualTo(personId);
            assertThat(household.getMembers().get(0).getRole()).isEqualTo(HouseholdRole.PRIMARY);
            assertThat(household.hasPrimary()).isTrue();
        }

        @Test
        void shouldAddPartnerMember() {
            Household household = createTestHousehold();
            UUID primaryId = UUID.randomUUID();
            UUID partnerId = UUID.randomUUID();

            household.addMember(primaryId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));
            household.addMember(partnerId, HouseholdRole.PARTNER, LocalDate.of(2024, 1, 1));

            assertThat(household.getMembers()).hasSize(2);
            assertThat(household.currentMembers()).hasSize(2);
        }

        @Test
        void shouldAddChildMember() {
            Household household = createTestHousehold();
            UUID primaryId = UUID.randomUUID();
            UUID childId = UUID.randomUUID();

            household.addMember(primaryId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));
            household.addMember(childId, HouseholdRole.CHILD, LocalDate.of(2024, 1, 1));

            assertThat(household.getMembers()).hasSize(2);
            assertThat(household.childCount()).isEqualTo(1);
        }

        @Test
        void shouldRejectAddingSecondPrimary() {
            Household household = createTestHousehold();
            UUID primary1 = UUID.randomUUID();
            UUID primary2 = UUID.randomUUID();

            household.addMember(primary1, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));

            assertThatThrownBy(() -> household.addMember(primary2, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("primary");
        }

        @Test
        void shouldRejectAddingSamePersonTwice() {
            Household household = createTestHousehold();
            UUID personId = UUID.randomUUID();

            household.addMember(personId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));

            assertThatThrownBy(() -> household.addMember(personId, HouseholdRole.PARTNER, LocalDate.of(2024, 1, 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already");
        }
    }

    @Nested
    @DisplayName("Member Removal")
    class MemberRemoval {

        @Test
        void shouldRemoveMemberWithEndDate() {
            Household household = createTestHousehold();
            UUID personId = UUID.randomUUID();

            household.addMember(personId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));
            household.removeMember(personId, LocalDate.of(2024, 12, 31));

            assertThat(household.getMembers()).hasSize(1);
            assertThat(household.currentMembers()).isEmpty();
            assertThat(household.getMembers().get(0).getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
        }

        @Test
        void shouldThrowWhenRemovingNonExistentMember() {
            Household household = createTestHousehold();

            assertThatThrownBy(() -> household.removeMember(UUID.randomUUID(), LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("Primary Member")
    class PrimaryMember {

        @Test
        void shouldReturnPrimaryMember() {
            Household household = createTestHousehold();
            UUID primaryId = UUID.randomUUID();
            UUID partnerId = UUID.randomUUID();

            household.addMember(primaryId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1));
            household.addMember(partnerId, HouseholdRole.PARTNER, LocalDate.of(2024, 1, 1));

            HouseholdMember primary = household.primaryMember();
            assertThat(primary).isNotNull();
            assertThat(primary.getPersonId()).isEqualTo(primaryId);
        }

        @Test
        void shouldReturnNullWhenNoPrimary() {
            Household household = createTestHousehold();

            assertThat(household.primaryMember()).isNull();
        }
    }

    private Household createTestHousehold() {
        return new Household(tenantId, "Familie Müller");
    }
}

/*
 * Govinda ERP - Household Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model

import net.voytrex.govinda.common.domain.model.HouseholdRole
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class HouseholdTest {

    private val tenantId = UUID.randomUUID()

    @Nested
    inner class `Household Creation` {

        @Test
        fun `should create household with name`() {
            val household = Household(
                tenantId = tenantId,
                name = "Familie Müller"
            )

            assertThat(household.id).isNotNull()
            assertThat(household.name).isEqualTo("Familie Müller")
            assertThat(household.members).isEmpty()
        }

        @Test
        fun `should reject empty name`() {
            assertThatThrownBy {
                Household(tenantId = tenantId, name = "")
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("name")
        }
    }

    @Nested
    inner class `Member Management` {

        @Test
        fun `should add primary member`() {
            val household = createTestHousehold()
            val personId = UUID.randomUUID()

            household.addMember(personId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))

            assertThat(household.members).hasSize(1)
            assertThat(household.members.first().personId).isEqualTo(personId)
            assertThat(household.members.first().role).isEqualTo(HouseholdRole.PRIMARY)
            assertThat(household.hasPrimary()).isTrue()
        }

        @Test
        fun `should add partner member`() {
            val household = createTestHousehold()
            val primaryId = UUID.randomUUID()
            val partnerId = UUID.randomUUID()

            household.addMember(primaryId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))
            household.addMember(partnerId, HouseholdRole.PARTNER, LocalDate.of(2024, 1, 1))

            assertThat(household.members).hasSize(2)
            assertThat(household.currentMembers()).hasSize(2)
        }

        @Test
        fun `should add child member`() {
            val household = createTestHousehold()
            val primaryId = UUID.randomUUID()
            val childId = UUID.randomUUID()

            household.addMember(primaryId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))
            household.addMember(childId, HouseholdRole.CHILD, LocalDate.of(2024, 1, 1))

            assertThat(household.members).hasSize(2)
            assertThat(household.childCount()).isEqualTo(1)
        }

        @Test
        fun `should reject adding second primary`() {
            val household = createTestHousehold()
            val primary1 = UUID.randomUUID()
            val primary2 = UUID.randomUUID()

            household.addMember(primary1, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))

            assertThatThrownBy {
                household.addMember(primary2, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))
            }.isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("primary")
        }

        @Test
        fun `should reject adding same person twice`() {
            val household = createTestHousehold()
            val personId = UUID.randomUUID()

            household.addMember(personId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))

            assertThatThrownBy {
                household.addMember(personId, HouseholdRole.PARTNER, LocalDate.of(2024, 1, 1))
            }.isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("already")
        }
    }

    @Nested
    inner class `Member Removal` {

        @Test
        fun `should remove member with end date`() {
            val household = createTestHousehold()
            val personId = UUID.randomUUID()

            household.addMember(personId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))
            household.removeMember(personId, LocalDate.of(2024, 12, 31))

            assertThat(household.members).hasSize(1)
            assertThat(household.currentMembers()).isEmpty()
            assertThat(household.members.first().validTo).isEqualTo(LocalDate.of(2024, 12, 31))
        }

        @Test
        fun `should throw when removing non-existent member`() {
            val household = createTestHousehold()

            assertThatThrownBy {
                household.removeMember(UUID.randomUUID(), LocalDate.now())
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("not found")
        }
    }

    @Nested
    inner class `Primary Member` {

        @Test
        fun `should return primary member`() {
            val household = createTestHousehold()
            val primaryId = UUID.randomUUID()
            val partnerId = UUID.randomUUID()

            household.addMember(primaryId, HouseholdRole.PRIMARY, LocalDate.of(2024, 1, 1))
            household.addMember(partnerId, HouseholdRole.PARTNER, LocalDate.of(2024, 1, 1))

            val primary = household.primaryMember()
            assertThat(primary).isNotNull
            assertThat(primary!!.personId).isEqualTo(primaryId)
        }

        @Test
        fun `should return null when no primary`() {
            val household = createTestHousehold()

            assertThat(household.primaryMember()).isNull()
        }
    }

    private fun createTestHousehold() = Household(
        tenantId = tenantId,
        name = "Familie Müller"
    )
}

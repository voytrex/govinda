/*
 * Govinda ERP - Person Entity Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model

import net.voytrex.govinda.common.domain.model.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class PersonTest {

    private val tenantId = UUID.randomUUID()
    private val userId = UUID.randomUUID()

    @Nested
    inner class `Person Creation` {

        @Test
        fun `should create person with valid data`() {
            val person = Person(
                tenantId = tenantId,
                ahvNr = AhvNumber("756.1234.5678.90"),
                lastName = "Müller",
                firstName = "Hans",
                dateOfBirth = LocalDate.of(1985, 3, 15),
                gender = Gender.MALE
            )

            assertThat(person.id).isNotNull()
            assertThat(person.lastName).isEqualTo("Müller")
            assertThat(person.firstName).isEqualTo("Hans")
            assertThat(person.fullName()).isEqualTo("Hans Müller")
            assertThat(person.status).isEqualTo(PersonStatus.ACTIVE)
        }

        @Test
        fun `should reject empty last name`() {
            assertThatThrownBy {
                Person(
                    tenantId = tenantId,
                    ahvNr = AhvNumber("756.1234.5678.90"),
                    lastName = "",
                    firstName = "Hans",
                    dateOfBirth = LocalDate.of(1985, 3, 15),
                    gender = Gender.MALE
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Last name")
        }

        @Test
        fun `should reject empty first name`() {
            assertThatThrownBy {
                Person(
                    tenantId = tenantId,
                    ahvNr = AhvNumber("756.1234.5678.90"),
                    lastName = "Müller",
                    firstName = "",
                    dateOfBirth = LocalDate.of(1985, 3, 15),
                    gender = Gender.MALE
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("First name")
        }

        @Test
        fun `should reject future birth date`() {
            assertThatThrownBy {
                Person(
                    tenantId = tenantId,
                    ahvNr = AhvNumber("756.1234.5678.90"),
                    lastName = "Müller",
                    firstName = "Hans",
                    dateOfBirth = LocalDate.now().plusDays(1),
                    gender = Gender.MALE
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Date of birth")
        }
    }

    @Nested
    inner class `Age Calculation` {

        @Test
        fun `should calculate age correctly`() {
            val person = createTestPerson(dateOfBirth = LocalDate.of(1985, 3, 15))

            val ageOn2024 = person.ageAt(LocalDate.of(2024, 3, 15))
            assertThat(ageOn2024).isEqualTo(39)

            val ageBeforeBirthday = person.ageAt(LocalDate.of(2024, 3, 14))
            assertThat(ageBeforeBirthday).isEqualTo(38)
        }

        @Test
        fun `should determine age group for child`() {
            val child = createTestPerson(dateOfBirth = LocalDate.of(2010, 6, 1))

            val ageGroup = child.ageGroupAt(LocalDate.of(2024, 1, 1))
            assertThat(ageGroup).isEqualTo(AgeGroup.CHILD)
        }

        @Test
        fun `should determine age group for young adult`() {
            val youngAdult = createTestPerson(dateOfBirth = LocalDate.of(2002, 6, 1))

            val ageGroup = youngAdult.ageGroupAt(LocalDate.of(2024, 1, 1))
            assertThat(ageGroup).isEqualTo(AgeGroup.YOUNG_ADULT)
        }

        @Test
        fun `should determine age group for adult`() {
            val adult = createTestPerson(dateOfBirth = LocalDate.of(1985, 3, 15))

            val ageGroup = adult.ageGroupAt(LocalDate.of(2024, 1, 1))
            assertThat(ageGroup).isEqualTo(AgeGroup.ADULT)
        }
    }

    @Nested
    inner class `Name Change with History` {

        @Test
        fun `should change name and create history entry`() {
            val person = createTestPerson(lastName = "Müller", firstName = "Anna")

            val historyEntry = person.changeName(
                newLastName = "Schmidt-Müller",
                reason = "Marriage",
                effectiveDate = LocalDate.of(2024, 9, 1),
                changedBy = userId
            )

            // Person should have new name
            assertThat(person.lastName).isEqualTo("Schmidt-Müller")
            assertThat(person.firstName).isEqualTo("Anna")

            // History entry should have old values
            assertThat(historyEntry.lastName).isEqualTo("Müller")
            assertThat(historyEntry.firstName).isEqualTo("Anna")
            assertThat(historyEntry.mutationType).isEqualTo(MutationType.UPDATE)
            assertThat(historyEntry.mutationReason).isEqualTo("Marriage")
            assertThat(historyEntry.validTo).isEqualTo(LocalDate.of(2024, 8, 31))
        }
    }

    @Nested
    inner class `Marital Status Change` {

        @Test
        fun `should change marital status and create history entry`() {
            val person = createTestPerson()
            person.maritalStatus = MaritalStatus.SINGLE

            val historyEntry = person.changeMaritalStatus(
                newStatus = MaritalStatus.MARRIED,
                reason = "Marriage",
                effectiveDate = LocalDate.of(2024, 9, 1),
                changedBy = userId
            )

            assertThat(person.maritalStatus).isEqualTo(MaritalStatus.MARRIED)
            assertThat(historyEntry.maritalStatus).isEqualTo(MaritalStatus.SINGLE)
        }
    }

    private fun createTestPerson(
        lastName: String = "Müller",
        firstName: String = "Hans",
        dateOfBirth: LocalDate = LocalDate.of(1985, 3, 15)
    ) = Person(
        tenantId = tenantId,
        ahvNr = AhvNumber("756.1234.5678.90"),
        lastName = lastName,
        firstName = firstName,
        dateOfBirth = dateOfBirth,
        gender = Gender.MALE
    )
}

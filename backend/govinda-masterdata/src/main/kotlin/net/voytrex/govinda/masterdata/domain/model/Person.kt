/*
 * Govinda ERP - Person Entity
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model

import jakarta.persistence.*
import net.voytrex.govinda.common.domain.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.UUID

/**
 * Person entity representing an insured individual.
 *
 * This is a core entity in the system, representing natural persons
 * who can be insured under health insurance policies.
 *
 * Supports history tracking for name and marital status changes.
 */
@Entity
@Table(name = "person")
class Person(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", updatable = false, nullable = false)
    val tenantId: UUID,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "ahv_nr", length = 16, nullable = false))
    val ahvNr: AhvNumber,

    @Column(name = "last_name", length = 100, nullable = false)
    var lastName: String,

    @Column(name = "first_name", length = 100, nullable = false)
    var firstName: String,

    @Column(name = "date_of_birth", nullable = false)
    val dateOfBirth: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false)
    val gender: Gender,

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    var maritalStatus: MaritalStatus? = null,

    @Column(name = "nationality", length = 3)
    var nationality: String? = "CHE",

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", length = 2)
    var preferredLanguage: Language = Language.DE,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var status: PersonStatus = PersonStatus.ACTIVE,

    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0

) : Historized<PersonHistoryEntry> {

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    private val _addresses: MutableList<Address> = mutableListOf()

    val addresses: List<Address>
        get() = _addresses.toList()

    init {
        require(lastName.isNotBlank()) { "Last name must not be blank" }
        require(firstName.isNotBlank()) { "First name must not be blank" }
        require(!dateOfBirth.isAfter(LocalDate.now())) { "Date of birth cannot be in the future" }
    }

    // ═══════════════════════════════════════════════════════════════
    // Business Methods
    // ═══════════════════════════════════════════════════════════════

    /**
     * Returns the full name (first name + last name).
     */
    fun fullName(): String = "$firstName $lastName"

    /**
     * Calculates the age at a given date.
     */
    fun ageAt(date: LocalDate): Int = Period.between(dateOfBirth, date).years

    /**
     * Determines the age group for premium calculation at a given date.
     */
    fun ageGroupAt(date: LocalDate): AgeGroup = AgeGroup.forAge(ageAt(date))

    /**
     * Returns the current (active) main address.
     */
    fun currentAddress(): Address? =
        _addresses.find { it.addressType == AddressType.MAIN && it.isCurrent() }

    /**
     * Returns the address valid at a specific date.
     */
    fun addressAt(date: LocalDate): Address? =
        _addresses.find { it.addressType == AddressType.MAIN && it.isValidOn(date) }

    /**
     * Adds a new address, closing the previous one if exists.
     */
    fun addAddress(address: Address, closeExistingOn: LocalDate? = null) {
        if (closeExistingOn != null) {
            currentAddress()?.close(closeExistingOn)
        }
        _addresses.add(address)
        updatedAt = Instant.now()
    }

    // ═══════════════════════════════════════════════════════════════
    // History-Creating Mutations
    // ═══════════════════════════════════════════════════════════════

    /**
     * Changes the name (e.g., due to marriage) and creates a history entry.
     *
     * @param newLastName The new last name
     * @param newFirstName The new first name (defaults to current)
     * @param reason The reason for the change
     * @param effectiveDate When the change takes effect
     * @param changedBy The user making the change
     * @return History entry with the old values
     */
    fun changeName(
        newLastName: String,
        newFirstName: String = this.firstName,
        reason: String,
        effectiveDate: LocalDate,
        changedBy: UUID
    ): PersonHistoryEntry {
        require(newLastName.isNotBlank()) { "New last name must not be blank" }
        require(newFirstName.isNotBlank()) { "New first name must not be blank" }

        val historyEntry = createHistoryEntry(
            mutationType = MutationType.UPDATE,
            reason = reason,
            changedBy = changedBy
        ).copy(validTo = effectiveDate.minusDays(1))

        this.lastName = newLastName
        this.firstName = newFirstName
        this.updatedAt = Instant.now()

        return historyEntry
    }

    /**
     * Changes the marital status and creates a history entry.
     */
    fun changeMaritalStatus(
        newStatus: MaritalStatus,
        reason: String,
        effectiveDate: LocalDate,
        changedBy: UUID
    ): PersonHistoryEntry {
        val historyEntry = createHistoryEntry(
            mutationType = MutationType.UPDATE,
            reason = reason,
            changedBy = changedBy
        ).copy(validTo = effectiveDate.minusDays(1))

        this.maritalStatus = newStatus
        this.updatedAt = Instant.now()

        return historyEntry
    }

    override fun createHistoryEntry(
        mutationType: MutationType,
        reason: String?,
        changedBy: UUID
    ) = PersonHistoryEntry(
        personId = this.id,
        lastName = this.lastName,
        firstName = this.firstName,
        maritalStatus = this.maritalStatus,
        validFrom = LocalDate.now(),
        validTo = null,
        mutationType = mutationType,
        mutationReason = reason,
        changedBy = changedBy
    )

    // ═══════════════════════════════════════════════════════════════
    // Equality
    // ═══════════════════════════════════════════════════════════════

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Person
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Person(id=$id, ahvNr=$ahvNr, name='$firstName $lastName')"
}

/**
 * History entry for Person changes.
 */
@Entity
@Table(name = "person_history")
data class PersonHistoryEntry(
    @Id
    @Column(name = "history_id")
    override val historyId: UUID = UUID.randomUUID(),

    @Column(name = "person_id", nullable = false)
    val personId: UUID,

    @Column(name = "last_name", length = 100, nullable = false)
    val lastName: String,

    @Column(name = "first_name", length = 100, nullable = false)
    val firstName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    val maritalStatus: MaritalStatus?,

    @Column(name = "valid_from", nullable = false)
    override val validFrom: LocalDate,

    @Column(name = "valid_to")
    override val validTo: LocalDate?,

    @Column(name = "recorded_at", nullable = false)
    override val recordedAt: Instant = Instant.now(),

    @Column(name = "superseded_at")
    override val supersededAt: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_type", length = 20, nullable = false)
    override val mutationType: MutationType,

    @Column(name = "mutation_reason", length = 500)
    override val mutationReason: String?,

    @Column(name = "changed_by", nullable = false)
    override val changedBy: UUID

) : HistoryEntry(
    historyId, validFrom, validTo, recordedAt,
    supersededAt, mutationType, mutationReason, changedBy
)

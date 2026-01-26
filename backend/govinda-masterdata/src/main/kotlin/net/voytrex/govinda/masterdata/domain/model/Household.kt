/*
 * Govinda ERP - Household Entity
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.domain.model

import jakarta.persistence.*
import net.voytrex.govinda.common.domain.model.HouseholdRole
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * Household entity grouping related persons.
 *
 * A household represents a family unit for insurance purposes.
 * It contains members with different roles (primary, partner, children).
 *
 * Business rules:
 * - Exactly one PRIMARY member (policyholder)
 * - At most one PARTNER
 * - Multiple CHILD members allowed
 * - Each person can only be in one household at a time
 */
@Entity
@Table(name = "household")
class Household(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", updatable = false, nullable = false)
    val tenantId: UUID,

    @Column(name = "name", length = 200, nullable = false)
    var name: String,

    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    var version: Long = 0

) {
    @OneToMany(mappedBy = "household", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    private val _members: MutableList<HouseholdMember> = mutableListOf()

    val members: List<HouseholdMember>
        get() = _members.toList()

    init {
        require(name.isNotBlank()) { "Household name must not be blank" }
    }

    // ═══════════════════════════════════════════════════════════════
    // Business Methods
    // ═══════════════════════════════════════════════════════════════

    /**
     * Returns all currently active members.
     */
    fun currentMembers(): List<HouseholdMember> =
        _members.filter { it.isCurrent() }

    /**
     * Returns the primary member (policyholder).
     */
    fun primaryMember(): HouseholdMember? =
        currentMembers().find { it.role == HouseholdRole.PRIMARY }

    /**
     * Returns true if the household has a primary member.
     */
    fun hasPrimary(): Boolean = primaryMember() != null

    /**
     * Returns the count of child members.
     */
    fun childCount(): Int =
        currentMembers().count { it.role == HouseholdRole.CHILD }

    /**
     * Adds a member to the household.
     *
     * @param personId The person to add
     * @param role The role in the household
     * @param validFrom When the membership starts
     * @throws IllegalStateException if adding would violate business rules
     */
    fun addMember(personId: UUID, role: HouseholdRole, validFrom: LocalDate) {
        // Check if person is already a current member
        if (currentMembers().any { it.personId == personId }) {
            throw IllegalStateException("Person is already a member of this household")
        }

        // Check if adding another primary
        if (role == HouseholdRole.PRIMARY && hasPrimary()) {
            throw IllegalStateException("Household already has a primary member")
        }

        val member = HouseholdMember(
            householdId = this.id,
            personId = personId,
            role = role,
            validFrom = validFrom
        )
        member.household = this
        _members.add(member)
        updatedAt = Instant.now()
    }

    /**
     * Removes a member from the household by setting their end date.
     *
     * @param personId The person to remove
     * @param validTo When the membership ends
     */
    fun removeMember(personId: UUID, validTo: LocalDate) {
        val member = currentMembers().find { it.personId == personId }
            ?: throw IllegalArgumentException("Member not found in household")

        member.validTo = validTo
        updatedAt = Instant.now()
    }

    // ═══════════════════════════════════════════════════════════════
    // Equality
    // ═══════════════════════════════════════════════════════════════

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Household
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Household(id=$id, name='$name')"
}

/**
 * Household member linking a person to a household with a role.
 */
@Entity
@Table(name = "household_member")
class HouseholdMember(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "household_id", nullable = false)
    val householdId: UUID,

    @Column(name = "person_id", nullable = false)
    val personId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    val role: HouseholdRole,

    @Column(name = "valid_from", nullable = false)
    val validFrom: LocalDate,

    @Column(name = "valid_to")
    var validTo: LocalDate? = null

) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", insertable = false, updatable = false)
    var household: Household? = null

    /**
     * Returns true if this membership is currently active.
     */
    fun isCurrent(): Boolean = validTo == null || !validTo!!.isBefore(LocalDate.now())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HouseholdMember
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

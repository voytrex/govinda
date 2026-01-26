/*
 * Govinda ERP - Base Entity
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import java.time.Instant
import java.util.UUID

/**
 * Base class for all domain entities.
 *
 * Provides common fields:
 * - id: UUID primary key
 * - tenantId: Multi-tenant isolation
 * - version: Optimistic locking
 * - createdAt/updatedAt: Audit timestamps
 */
@MappedSuperclass
abstract class BaseEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    open val id: UUID = UUID.randomUUID(),

    @Column(name = "tenant_id", updatable = false, nullable = false)
    open val tenantId: UUID,

    @Column(name = "created_at", updatable = false, nullable = false)
    open val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant = Instant.now(),

    @Version
    @Column(name = "version", nullable = false)
    open var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BaseEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * Interface for entities that support temporal history tracking.
 *
 * @param H The type of history entry this entity produces
 */
interface Historized<H : HistoryEntry> {
    val id: UUID

    /**
     * Creates a history entry capturing the current state.
     *
     * @param mutationType Type of mutation (CREATE, UPDATE, CORRECTION)
     * @param reason Human-readable reason for the change
     * @param changedBy UUID of the user making the change
     * @return A new history entry
     */
    fun createHistoryEntry(
        mutationType: MutationType,
        reason: String?,
        changedBy: UUID
    ): H
}

/**
 * Base class for history/audit entries.
 *
 * Supports bitemporal modeling:
 * - validFrom/validTo: When the fact was true in the real world (business time)
 * - recordedAt/supersededAt: When the fact was known to the system (transaction time)
 */
@MappedSuperclass
abstract class HistoryEntry(
    @Id
    @Column(name = "history_id", updatable = false, nullable = false)
    open val historyId: UUID = UUID.randomUUID(),

    @Column(name = "valid_from", nullable = false)
    open val validFrom: java.time.LocalDate,

    @Column(name = "valid_to")
    open val validTo: java.time.LocalDate?,

    @Column(name = "recorded_at", updatable = false, nullable = false)
    open val recordedAt: Instant = Instant.now(),

    @Column(name = "superseded_at")
    open val supersededAt: Instant? = null,

    @Column(name = "mutation_type", nullable = false)
    open val mutationType: MutationType,

    @Column(name = "mutation_reason")
    open val mutationReason: String?,

    @Column(name = "changed_by", nullable = false)
    open val changedBy: UUID
)

/**
 * Types of mutations/changes to historized entities.
 */
enum class MutationType {
    /** Initial creation of the entity */
    CREATE,

    /** Normal business update (e.g., address change, franchise change) */
    UPDATE,

    /** Correction of an error (may affect historical data) */
    CORRECTION,

    /** Retroactive cancellation */
    CANCELLATION
}

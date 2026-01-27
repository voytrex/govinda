/*
 * Govinda ERP - History Entry
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Base class for history/audit entries.
 *
 * Supports bitemporal modeling:
 * - validFrom/validTo: When the fact was true in the real world (business time)
 * - recordedAt/supersededAt: When the fact was known to the system (transaction time)
 */
@MappedSuperclass
public abstract class HistoryEntry {
    @Id
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID historyId = UUID.randomUUID();

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "recorded_at", updatable = false, nullable = false)
    private Instant recordedAt = Instant.now();

    @Column(name = "superseded_at")
    private Instant supersededAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "mutation_type", nullable = false, length = 20)
    private MutationType mutationType;

    @Column(name = "mutation_reason")
    private String mutationReason;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    protected HistoryEntry() {
    }

    protected HistoryEntry(
        LocalDate validFrom,
        LocalDate validTo,
        MutationType mutationType,
        String mutationReason,
        UUID changedBy
    ) {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.mutationType = mutationType;
        this.mutationReason = mutationReason;
        this.changedBy = changedBy;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    protected void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    protected void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Instant getSupersededAt() {
        return supersededAt;
    }

    public void setSupersededAt(Instant supersededAt) {
        this.supersededAt = supersededAt;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }

    public String getMutationReason() {
        return mutationReason;
    }

    public void setMutationReason(String mutationReason) {
        this.mutationReason = mutationReason;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }
}

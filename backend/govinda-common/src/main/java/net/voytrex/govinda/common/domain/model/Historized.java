/*
 * Govinda ERP - Historized Interface
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import java.util.UUID;

/**
 * Interface for entities that support temporal history tracking.
 *
 * @param <H> The type of history entry this entity produces
 */
public interface Historized<H extends HistoryEntry> {
    UUID getId();

    /**
     * Creates a history entry capturing the current state.
     *
     * @param mutationType Type of mutation (CREATE, UPDATE, CORRECTION)
     * @param reason Human-readable reason for the change
     * @param changedBy UUID of the user making the change
     * @return A new history entry
     */
    H createHistoryEntry(MutationType mutationType, String reason, UUID changedBy);
}

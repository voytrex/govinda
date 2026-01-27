/*
 * Govinda ERP - Mutation Type
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Types of mutations/changes to historized entities.
 */
public enum MutationType {
    /** Initial creation of the entity */
    CREATE,
    /** Normal business update (e.g., address change, franchise change) */
    UPDATE,
    /** Correction of an error (may affect historical data) */
    CORRECTION,
    /** Retroactive cancellation */
    CANCELLATION
}

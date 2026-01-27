/*
 * Govinda ERP - Policy Status
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Policy status.
 */
public enum PolicyStatus {
    /** Quote/offer not yet accepted */
    QUOTE,
    /** Application pending review */
    PENDING,
    /** Active policy */
    ACTIVE,
    /** Temporarily suspended */
    SUSPENDED,
    /** Terminated/cancelled */
    CANCELLED
}

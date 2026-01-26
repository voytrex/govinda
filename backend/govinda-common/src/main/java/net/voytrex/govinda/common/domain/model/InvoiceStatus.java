/*
 * Govinda ERP - Invoice Status
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Invoice status.
 */
public enum InvoiceStatus {
    /** Draft, not yet sent */
    DRAFT,
    /** Sent to customer */
    SENT,
    /** Fully paid */
    PAID,
    /** Partially paid */
    PARTIAL,
    /** Overdue */
    OVERDUE,
    /** Cancelled */
    CANCELLED
}

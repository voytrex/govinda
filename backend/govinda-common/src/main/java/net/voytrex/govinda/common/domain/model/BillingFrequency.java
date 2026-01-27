/*
 * Govinda ERP - Billing Frequency
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import java.math.BigDecimal;

/**
 * Billing frequency.
 */
public enum BillingFrequency {
    MONTHLY("M", 12, BigDecimal.ZERO),
    QUARTERLY("Q", 4, new BigDecimal("0.5")),
    SEMI_ANNUAL("S", 2, new BigDecimal("1.0")),
    ANNUAL("A", 1, new BigDecimal("2.0"));

    private final String code;
    private final int periodsPerYear;
    private final BigDecimal discountPercent;

    BillingFrequency(String code, int periodsPerYear, BigDecimal discountPercent) {
        this.code = code;
        this.periodsPerYear = periodsPerYear;
        this.discountPercent = discountPercent;
    }

    public String getCode() {
        return code;
    }

    public int getPeriodsPerYear() {
        return periodsPerYear;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public static BillingFrequency fromCode(String code) {
        for (BillingFrequency frequency : values()) {
            if (frequency.code.equalsIgnoreCase(code)) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Unknown billing frequency code: " + code);
    }
}

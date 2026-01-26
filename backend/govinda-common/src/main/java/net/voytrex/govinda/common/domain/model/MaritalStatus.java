/*
 * Govinda ERP - Marital Status
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Marital status.
 */
public enum MaritalStatus {
    SINGLE("S"),
    MARRIED("M"),
    DIVORCED("D"),
    WIDOWED("W"),
    REGISTERED_PARTNERSHIP("P"),
    DISSOLVED_PARTNERSHIP("DP");

    private final String code;

    MaritalStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MaritalStatus fromCode(String code) {
        for (MaritalStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown marital status code: " + code);
    }
}

/*
 * Govinda ERP - AHV Number
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Swiss AHV number (Sozialversicherungsnummer).
 *
 * Format: 756.XXXX.XXXX.XX
 * - 756: Country code for Switzerland
 * - 13 digits total, separated by dots
 * - Last digit is a checksum
 *
 * @property value The formatted AHV number string
 */
@Embeddable
public class AhvNumber {
    private static final Pattern FORMAT_REGEX = Pattern.compile("756\\.\\d{4}\\.\\d{4}\\.\\d{2}");

    @Column(name = "ahv_nr", length = 16)
    private String value;

    protected AhvNumber() {
    }

    public AhvNumber(String value) {
        if (!isValidFormat(value)) {
            throw new IllegalArgumentException(
                "Invalid AHV number format: " + value + ". Expected format: 756.XXXX.XXXX.XX"
            );
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (!isValidFormat(value)) {
            throw new IllegalArgumentException(
                "Invalid AHV number format: " + value + ". Expected format: 756.XXXX.XXXX.XX"
            );
        }
        this.value = value;
    }

    /**
     * Returns the AHV number without dots.
     */
    public String toUnformatted() {
        return value.replace(".", "");
    }

    /**
     * Returns the formatted AHV number.
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Validates the format of an AHV number.
     */
    public static boolean isValidFormat(String value) {
        return value != null && FORMAT_REGEX.matcher(value).matches();
    }

    /**
     * Creates an AhvNumber from an unformatted string (13 digits).
     */
    public static AhvNumber fromUnformatted(String digits) {
        if (digits == null || digits.length() != 13 || digits.chars().anyMatch(ch -> !Character.isDigit(ch))) {
            throw new IllegalArgumentException("Unformatted AHV must be 13 digits");
        }
        String formatted = digits.substring(0, 3) + "."
            + digits.substring(3, 7) + "."
            + digits.substring(7, 11) + "."
            + digits.substring(11, 13);
        return new AhvNumber(formatted);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AhvNumber ahvNumber = (AhvNumber) other;
        return Objects.equals(value, ahvNumber.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

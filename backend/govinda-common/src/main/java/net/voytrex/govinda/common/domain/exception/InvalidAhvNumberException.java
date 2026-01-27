/*
 * Govinda ERP - Invalid AHV Number Exception
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class InvalidAhvNumberException extends DomainException {
    public InvalidAhvNumberException(String value) {
        super("Invalid AHV number format: " + value, "INVALID_AHV_NUMBER");
    }
}

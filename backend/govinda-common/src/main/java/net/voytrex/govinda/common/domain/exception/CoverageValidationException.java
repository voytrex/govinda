/*
 * Govinda ERP - Coverage Validation Exception
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class CoverageValidationException extends DomainException {
    public CoverageValidationException(String message) {
        super(message, "COVERAGE_VALIDATION_ERROR");
    }
}

/*
 * Govinda ERP - Business Rule Violation Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class BusinessRuleViolationException extends DomainException {
    public BusinessRuleViolationException(String rule, String details) {
        super(
            "Business rule violation: " + rule + (details != null ? " - " + details : ""),
            "BUSINESS_RULE_VIOLATION"
        );
    }
}

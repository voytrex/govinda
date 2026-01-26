/*
 * Govinda ERP - Policy Validation Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class PolicyValidationException extends DomainException {
    public PolicyValidationException(String message) {
        super(message, "POLICY_VALIDATION_ERROR");
    }
}

/*
 * Govinda ERP - Authentication Exception
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class AuthenticationException extends DomainException {
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR");
    }
}

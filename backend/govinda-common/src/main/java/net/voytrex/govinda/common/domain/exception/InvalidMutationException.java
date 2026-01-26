/*
 * Govinda ERP - Invalid Mutation Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

public final class InvalidMutationException extends DomainException {
    public InvalidMutationException(String message) {
        super(message, "INVALID_MUTATION");
    }
}

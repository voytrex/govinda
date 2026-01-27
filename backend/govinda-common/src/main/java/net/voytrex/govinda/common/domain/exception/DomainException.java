/*
 * Govinda ERP - Domain Exceptions
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

/**
 * Base class for all domain exceptions.
 *
 * Domain exceptions represent business rule violations or
 * invalid operations in the domain layer.
 */
public sealed class DomainException extends RuntimeException
    permits EntityNotFoundException,
            EntityNotFoundByFieldException,
            DuplicateEntityException,
            InvalidAhvNumberException,
            PolicyValidationException,
            CoverageValidationException,
            PremiumCalculationException,
            TariffNotFoundException,
            InvalidMutationException,
            TenantNotFoundException,
            UnauthorizedTenantAccessException,
            ConcurrentModificationException,
            BusinessRuleViolationException,
            AuthenticationException {
    private final String errorCode;

    protected DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

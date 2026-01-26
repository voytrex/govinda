/*
 * Govinda ERP - Domain Exceptions
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception

import java.util.UUID

/**
 * Base class for all domain exceptions.
 *
 * Domain exceptions represent business rule violations or
 * invalid operations in the domain layer.
 */
sealed class DomainException(
    message: String,
    val errorCode: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Entity not found exception.
 */
class EntityNotFoundException(
    entityType: String,
    id: UUID
) : DomainException(
    message = "$entityType not found with id: $id",
    errorCode = "ENTITY_NOT_FOUND"
)

/**
 * Entity not found by a specific field.
 */
class EntityNotFoundByFieldException(
    entityType: String,
    fieldName: String,
    fieldValue: String
) : DomainException(
    message = "$entityType not found with $fieldName: $fieldValue",
    errorCode = "ENTITY_NOT_FOUND"
)

/**
 * Duplicate entity exception.
 */
class DuplicateEntityException(
    entityType: String,
    fieldName: String,
    fieldValue: String
) : DomainException(
    message = "$entityType already exists with $fieldName: $fieldValue",
    errorCode = "DUPLICATE_ENTITY"
)

/**
 * Invalid AHV number exception.
 */
class InvalidAhvNumberException(
    value: String
) : DomainException(
    message = "Invalid AHV number format: $value",
    errorCode = "INVALID_AHV_NUMBER"
)

/**
 * Policy validation exception.
 */
class PolicyValidationException(
    message: String
) : DomainException(
    message = message,
    errorCode = "POLICY_VALIDATION_ERROR"
)

/**
 * Coverage validation exception.
 */
class CoverageValidationException(
    message: String
) : DomainException(
    message = message,
    errorCode = "COVERAGE_VALIDATION_ERROR"
)

/**
 * Premium calculation exception.
 */
class PremiumCalculationException(
    message: String,
    cause: Throwable? = null
) : DomainException(
    message = message,
    errorCode = "PREMIUM_CALCULATION_ERROR",
    cause = cause
)

/**
 * Tariff not found exception.
 */
class TariffNotFoundException(
    year: Int,
    productCode: String,
    regionId: UUID? = null
) : DomainException(
    message = "Tariff not found for year $year, product $productCode" +
            (regionId?.let { ", region $it" } ?: ""),
    errorCode = "TARIFF_NOT_FOUND"
)

/**
 * Invalid mutation exception.
 */
class InvalidMutationException(
    message: String
) : DomainException(
    message = message,
    errorCode = "INVALID_MUTATION"
)

/**
 * Tenant not found exception.
 */
class TenantNotFoundException(
    tenantId: UUID
) : DomainException(
    message = "Tenant not found: $tenantId",
    errorCode = "TENANT_NOT_FOUND"
)

/**
 * Unauthorized tenant access exception.
 */
class UnauthorizedTenantAccessException(
    tenantId: UUID
) : DomainException(
    message = "Access denied to tenant: $tenantId",
    errorCode = "UNAUTHORIZED_TENANT_ACCESS"
)

/**
 * Concurrent modification exception.
 */
class ConcurrentModificationException(
    entityType: String,
    id: UUID
) : DomainException(
    message = "$entityType with id $id was modified by another transaction",
    errorCode = "CONCURRENT_MODIFICATION"
)

/**
 * Business rule violation exception.
 */
class BusinessRuleViolationException(
    rule: String,
    details: String? = null
) : DomainException(
    message = "Business rule violation: $rule" + (details?.let { " - $it" } ?: ""),
    errorCode = "BUSINESS_RULE_VIOLATION"
)

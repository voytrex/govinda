/*
 * Govinda ERP - Global Exception Handler
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api

import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import net.voytrex.govinda.common.domain.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

/**
 * Global exception handler for REST API.
 *
 * Translates domain exceptions to appropriate HTTP responses.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class, EntityNotFoundByFieldException::class)
    fun handleNotFound(
        ex: DomainException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.debug { "Entity not found: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message ?: "Entity not found",
                path = request.requestURI
            ))
    }

    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicate(
        ex: DuplicateEntityException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.debug { "Duplicate entity: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message ?: "Duplicate entity",
                path = request.requestURI
            ))
    }

    @ExceptionHandler(
        InvalidAhvNumberException::class,
        PolicyValidationException::class,
        CoverageValidationException::class,
        InvalidMutationException::class,
        BusinessRuleViolationException::class
    )
    fun handleValidation(
        ex: DomainException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.debug { "Validation error: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message ?: "Validation error",
                path = request.requestURI
            ))
    }

    @ExceptionHandler(TariffNotFoundException::class, PremiumCalculationException::class)
    fun handlePremiumError(
        ex: DomainException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Premium calculation error: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message ?: "Premium calculation error",
                path = request.requestURI
            ))
    }

    @ExceptionHandler(TenantNotFoundException::class, UnauthorizedTenantAccessException::class)
    fun handleTenantError(
        ex: DomainException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Tenant error: ${ex.message}" }
        val status = when (ex) {
            is TenantNotFoundException -> HttpStatus.NOT_FOUND
            is UnauthorizedTenantAccessException -> HttpStatus.FORBIDDEN
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity
            .status(status)
            .body(ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message ?: "Tenant error",
                path = request.requestURI
            ))
    }

    @ExceptionHandler(ConcurrentModificationException::class)
    fun handleConcurrentModification(
        ex: ConcurrentModificationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn { "Concurrent modification: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message ?: "Concurrent modification",
                path = request.requestURI
            ))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.map { error ->
            ErrorDetail(
                field = error.field,
                message = error.defaultMessage ?: "Invalid value",
                rejectedValue = error.rejectedValue
            )
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                errorCode = "VALIDATION_ERROR",
                message = "Request validation failed",
                path = request.requestURI,
                details = details
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Unhandled exception: ${ex.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                errorCode = "INTERNAL_ERROR",
                message = "An internal error occurred",
                path = request.requestURI
            ))
    }
}

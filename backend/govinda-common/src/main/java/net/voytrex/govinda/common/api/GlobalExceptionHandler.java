/*
 * Govinda ERP - Global Exception Handler
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import net.voytrex.govinda.common.domain.exception.AuthenticationException;
import net.voytrex.govinda.common.domain.exception.BusinessRuleViolationException;
import net.voytrex.govinda.common.domain.exception.ConcurrentModificationException;
import net.voytrex.govinda.common.domain.exception.CoverageValidationException;
import net.voytrex.govinda.common.domain.exception.DomainException;
import net.voytrex.govinda.common.domain.exception.DuplicateEntityException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundByFieldException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.common.domain.exception.InvalidAhvNumberException;
import net.voytrex.govinda.common.domain.exception.InvalidMutationException;
import net.voytrex.govinda.common.domain.exception.PolicyValidationException;
import net.voytrex.govinda.common.domain.exception.PremiumCalculationException;
import net.voytrex.govinda.common.domain.exception.TariffNotFoundException;
import net.voytrex.govinda.common.domain.exception.TenantNotFoundException;
import net.voytrex.govinda.common.domain.exception.UnauthorizedTenantAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for REST API.
 *
 * Translates domain exceptions to appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class, EntityNotFoundByFieldException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(DomainException ex, HttpServletRequest request) {
        logger.debug("Entity not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Entity not found",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEntityException ex, HttpServletRequest request) {
        logger.debug("Duplicate entity: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Duplicate entity",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler({
        InvalidAhvNumberException.class,
        PolicyValidationException.class,
        CoverageValidationException.class,
        InvalidMutationException.class,
        BusinessRuleViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleValidation(DomainException ex, HttpServletRequest request) {
        logger.debug("Validation error: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Validation error",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler({TariffNotFoundException.class, PremiumCalculationException.class})
    public ResponseEntity<ErrorResponse> handlePremiumError(DomainException ex, HttpServletRequest request) {
        logger.warn("Premium calculation error: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Premium calculation error",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        logger.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Authentication failed",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFound(
        AuthenticationCredentialsNotFoundException ex,
        HttpServletRequest request
    ) {
        logger.warn("Authentication credentials not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                "AUTHENTICATION_REQUIRED",
                ex.getMessage() != null ? ex.getMessage() : "Authentication required",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(
                "ACCESS_DENIED",
                ex.getMessage() != null ? ex.getMessage() : "Access denied",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler({TenantNotFoundException.class, UnauthorizedTenantAccessException.class})
    public ResponseEntity<ErrorResponse> handleTenantError(DomainException ex, HttpServletRequest request) {
        logger.warn("Tenant error: {}", ex.getMessage());
        HttpStatus status;
        if (ex instanceof TenantNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof UnauthorizedTenantAccessException) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity
            .status(status)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Tenant error",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentModification(ConcurrentModificationException ex, HttpServletRequest request) {
        logger.warn("Concurrent modification: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage() != null ? ex.getMessage() : "Concurrent modification",
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorDetail(
                error.getField(),
                error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                error.getRejectedValue()
            ))
            .toList();
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed",
                null,
                request.getRequestURI(),
                details
            ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        logger.debug("Resource not found: {}", request.getRequestURI());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                "NOT_FOUND",
                "Resource not found: " + request.getRequestURI(),
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        // In development, include the exception message for debugging
        String message = "An internal error occurred";
        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            message = ex.getMessage();
        }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(
                "INTERNAL_ERROR",
                message,
                request.getRequestURI()
            ));
    }
}

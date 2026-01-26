/*
 * Govinda ERP - Global Exception Handler
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for REST API.
 *
 * Translates domain exceptions to appropriate HTTP responses.
 * All error messages are internationalized using MessageSource.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public GlobalExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    private Locale getLocale(HttpServletRequest request) {
        return localeResolver.resolveLocale(request);
    }

    private String translateError(String errorCode, HttpServletRequest request, Object... args) {
        String key = "error." + errorCode.toLowerCase().replace("_", ".");
        Locale locale = getLocale(request);
        return messageSource.getMessage(key, args, locale);
    }

    @ExceptionHandler({EntityNotFoundException.class, EntityNotFoundByFieldException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(DomainException ex, HttpServletRequest request) {
        LOGGER.debug("Entity not found: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("ENTITY_NOT_FOUND", request);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEntityException ex, HttpServletRequest request) {
        LOGGER.debug("Duplicate entity: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("ENTITY_DUPLICATE", request);
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
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
        LOGGER.debug("Validation error: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("VALIDATION", request);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler({TariffNotFoundException.class, PremiumCalculationException.class})
    public ResponseEntity<ErrorResponse> handlePremiumError(DomainException ex, HttpServletRequest request) {
        LOGGER.warn("Premium calculation error: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("PREMIUM_CALCULATION", request);
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        LOGGER.warn("Authentication error: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("AUTHENTICATION_FAILED", request);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFound(
        AuthenticationCredentialsNotFoundException ex,
        HttpServletRequest request
    ) {
        LOGGER.warn("Authentication credentials not found: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("AUTHENTICATION_REQUIRED", request);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                "AUTHENTICATION_REQUIRED",
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        LOGGER.warn("Access denied: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("ACCESS_DENIED", request);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(
                "ACCESS_DENIED",
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler({TenantNotFoundException.class, UnauthorizedTenantAccessException.class})
    public ResponseEntity<ErrorResponse> handleTenantError(DomainException ex, HttpServletRequest request) {
        LOGGER.warn("Tenant error: {}", ex.getMessage());
        HttpStatus status;
        if (ex instanceof TenantNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof UnauthorizedTenantAccessException) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("TENANT", request);
        return ResponseEntity
            .status(status)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentModification(
        ConcurrentModificationException ex,
        HttpServletRequest request
    ) {
        LOGGER.warn("Concurrent modification: {}", ex.getMessage());
        String message = ex.getMessage() != null
            ? ex.getMessage()
            : translateError("CONCURRENT_MODIFICATION", request);
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(
                ex.getErrorCode(),
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        Locale locale = getLocale(request);
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> {
                String message = error.getDefaultMessage();
                if (message == null) {
                    message = messageSource.getMessage(
                        "error.validation.invalid.value",
                        null,
                        locale
                    );
                }
                return new ErrorDetail(
                    error.getField(),
                    message,
                    error.getRejectedValue()
                );
            })
            .toList();
        String summaryMessage = messageSource.getMessage(
            "error.validation.request.failed",
            null,
            locale
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                "VALIDATION_ERROR",
                summaryMessage,
                null,
                request.getRequestURI(),
                details
            ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
        NoResourceFoundException ex,
        HttpServletRequest request
    ) {
        LOGGER.debug("Resource not found: {}", request.getRequestURI());
        Locale locale = getLocale(request);
        String message = messageSource.getMessage(
            "error.resource.not.found",
            new Object[]{request.getRequestURI()},
            locale
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                "NOT_FOUND",
                message,
                request.getRequestURI()
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unhandled exception: {}", ex.getMessage(), ex);
        // In development, include the exception message for debugging
        String message = translateError("INTERNAL", request);
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

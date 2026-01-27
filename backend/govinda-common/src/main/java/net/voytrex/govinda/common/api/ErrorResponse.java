/*
 * Govinda ERP - API Error Response
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * Standard error response for API errors.
 */
@Schema(description = "Standard error response structure returned by the API")
public record ErrorResponse(
    @Schema(
        description = "Machine-readable error code",
        example = "ENTITY_NOT_FOUND",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String errorCode,
    
    @Schema(
        description = "Human-readable error message (localized)",
        example = "Person with ID 550e8400-e29b-41d4-a716-446655440000 not found",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String message,
    
    @Schema(
        description = "Timestamp when the error occurred",
        example = "2024-01-15T10:30:00Z",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Instant timestamp,
    
    @Schema(
        description = "API path where the error occurred",
        example = "/api/v1/masterdata/persons/550e8400-e29b-41d4-a716-446655440000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String path,
    
    @Schema(description = "Optional field-level validation errors. Only present for validation errors.")
    List<ErrorDetail> details
) {
    public ErrorResponse(String errorCode, String message, Instant timestamp, String path, List<ErrorDetail> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.path = path;
        this.details = details;
    }

    public ErrorResponse(String errorCode, String message, String path) {
        this(errorCode, message, Instant.now(), path, null);
    }
}

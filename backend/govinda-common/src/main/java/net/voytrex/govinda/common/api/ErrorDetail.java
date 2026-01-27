/*
 * Govinda ERP - API Error Detail
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Detailed error information for validation errors.
 */
@Schema(description = "Field-level validation error detail")
public record ErrorDetail(
    @Schema(
        description = "Field name that failed validation",
        example = "ahvNr",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String field,
    
    @Schema(
        description = "Validation error message",
        example = "Invalid AHV number format",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String message,
    
    @Schema(description = "The value that was rejected", example = "123456789")
    Object rejectedValue
) { }

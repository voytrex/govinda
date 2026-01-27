/*
 * Govinda ERP - Authentication Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT token")
public record LoginResponse(
    @Schema(description = "JWT token for authenticated requests. Include in Authorization header as 'Bearer <token>'",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    String token,
    
    @Schema(description = "Token type, always 'Bearer'", example = "Bearer", requiredMode = Schema.RequiredMode.REQUIRED)
    String tokenType,
    
    @Schema(description = "Helpful message with usage instructions",
            example = "Use this token in the Authorization header: 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'")
    String message
) { }

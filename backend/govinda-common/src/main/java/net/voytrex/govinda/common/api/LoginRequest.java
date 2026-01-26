/*
 * Govinda ERP - Authentication Request
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login credentials for authentication")
public record LoginRequest(
    @Schema(description = "Username for authentication", example = "admin", required = true)
    @NotBlank(message = "Username is required")
    String username,
    
    @Schema(description = "Password for authentication", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    String password,
    
    @Schema(description = "Optional tenant ID (UUID). If not provided, uses user's default tenant",
            example = "550e8400-e29b-41d4-a716-446655440000")
    String tenantId
) { }

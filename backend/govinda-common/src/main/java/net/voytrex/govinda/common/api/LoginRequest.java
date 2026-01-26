/*
 * Govinda ERP - Authentication Request
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Username is required")
    String username,
    @NotBlank(message = "Password is required")
    String password,
    @Parameter(description = "Optional tenant ID. If not provided, uses user's default tenant")
    String tenantId
) {}

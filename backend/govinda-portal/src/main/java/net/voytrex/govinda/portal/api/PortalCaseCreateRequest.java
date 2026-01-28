/*
 * Govinda ERP - Portal Case Create Request
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.voytrex.govinda.cases.domain.model.CaseType;

public record PortalCaseCreateRequest(
    @NotNull(message = "Case type is required")
    CaseType type,
    @NotBlank(message = "Subject is required")
    String subject,
    String description
) { }

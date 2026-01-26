/*
 * Govinda ERP - Change Name Request
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ChangeNameRequest(
    @NotBlank(message = "New last name is required")
    String newLastName,
    String newFirstName,
    @NotBlank(message = "Reason is required")
    String reason,
    LocalDate effectiveDate
) {}

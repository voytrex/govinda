/*
 * Govinda ERP - Change Marital Status Request
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import net.voytrex.govinda.common.domain.model.MaritalStatus;

public record ChangeMaritalStatusRequest(
    MaritalStatus newStatus,
    @NotBlank(message = "Reason is required")
    String reason,
    LocalDate effectiveDate
) {}

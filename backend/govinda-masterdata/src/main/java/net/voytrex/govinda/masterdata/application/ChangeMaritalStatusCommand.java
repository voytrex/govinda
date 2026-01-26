/*
 * Govinda ERP - Change Marital Status Command
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.MaritalStatus;

public record ChangeMaritalStatusCommand(
    UUID tenantId,
    UUID personId,
    MaritalStatus newStatus,
    String reason,
    LocalDate effectiveDate,
    UUID changedBy
) {}

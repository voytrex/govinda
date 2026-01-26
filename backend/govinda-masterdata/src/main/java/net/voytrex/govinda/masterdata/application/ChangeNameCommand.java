/*
 * Govinda ERP - Change Name Command
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import java.time.LocalDate;
import java.util.UUID;

public record ChangeNameCommand(
    UUID tenantId,
    UUID personId,
    String newLastName,
    String newFirstName,
    String reason,
    LocalDate effectiveDate,
    UUID changedBy
) {}

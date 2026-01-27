/*
 * Govinda ERP - Person History Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.MaritalStatus;

public record PersonHistoryResponse(
    UUID historyId,
    String lastName,
    String firstName,
    MaritalStatus maritalStatus,
    LocalDate validFrom,
    LocalDate validTo,
    String mutationType,
    String mutationReason,
    UUID changedBy,
    String recordedAt
) { }

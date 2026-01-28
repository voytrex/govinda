/*
 * Govinda ERP - Create Case Command
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.cases.application;

import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.CaseType;

public record CreateCaseCommand(
    UUID tenantId,
    UUID personId,
    CaseType type,
    String subject,
    String description
) { }

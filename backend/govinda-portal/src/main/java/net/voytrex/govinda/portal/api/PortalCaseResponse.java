/*
 * Govinda ERP - Portal Case Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import java.time.Instant;
import java.util.UUID;
import net.voytrex.govinda.cases.domain.model.CaseStatus;
import net.voytrex.govinda.cases.domain.model.CaseType;

public record PortalCaseResponse(
    UUID id,
    CaseType type,
    CaseStatus status,
    String subject,
    String description,
    Instant createdAt,
    Instant updatedAt
) { }

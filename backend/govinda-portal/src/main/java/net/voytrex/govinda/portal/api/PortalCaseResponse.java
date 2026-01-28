/*
 * Govinda ERP - Portal Case Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import java.time.Instant;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCaseStatus;
import net.voytrex.govinda.portal.domain.model.PortalCaseType;

public record PortalCaseResponse(
    UUID id,
    PortalCaseType type,
    PortalCaseStatus status,
    String subject,
    String description,
    Instant createdAt,
    Instant updatedAt
) { }

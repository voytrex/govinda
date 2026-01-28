/*
 * Govinda ERP - Portal Document Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import java.time.Instant;
import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalDocumentStatus;
import net.voytrex.govinda.portal.domain.model.PortalDocumentType;

public record PortalDocumentResponse(
    UUID id,
    PortalDocumentType type,
    PortalDocumentStatus status,
    String title,
    Instant createdAt,
    Instant updatedAt
) { }

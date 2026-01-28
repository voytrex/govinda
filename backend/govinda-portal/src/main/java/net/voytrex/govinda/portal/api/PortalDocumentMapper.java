/*
 * Govinda ERP - Portal Document API Mapper
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import net.voytrex.govinda.portal.domain.model.PortalDocument;

public final class PortalDocumentMapper {
    private PortalDocumentMapper() {
    }

    public static PortalDocumentResponse toResponse(PortalDocument document) {
        return new PortalDocumentResponse(
            document.getId(),
            document.getType(),
            document.getStatus(),
            document.getTitle(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }
}

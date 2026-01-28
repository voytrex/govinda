/*
 * Govinda ERP - Portal Case API Mapper
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import net.voytrex.govinda.portal.domain.model.PortalCase;

public final class PortalCaseMapper {
    private PortalCaseMapper() {
    }

    public static PortalCaseResponse toResponse(PortalCase portalCase) {
        return new PortalCaseResponse(
            portalCase.getId(),
            portalCase.getType(),
            portalCase.getStatus(),
            portalCase.getSubject(),
            portalCase.getDescription(),
            portalCase.getCreatedAt(),
            portalCase.getUpdatedAt()
        );
    }
}

/*
 * Govinda ERP - Portal Case API Mapper
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import net.voytrex.govinda.cases.domain.model.Case;

public final class PortalCaseMapper {
    private PortalCaseMapper() {
    }

    public static PortalCaseResponse toResponse(Case caseRecord) {
        return new PortalCaseResponse(
            caseRecord.getId(),
            caseRecord.getType(),
            caseRecord.getStatus(),
            caseRecord.getSubject(),
            caseRecord.getDescription(),
            caseRecord.getCreatedAt(),
            caseRecord.getUpdatedAt()
        );
    }
}

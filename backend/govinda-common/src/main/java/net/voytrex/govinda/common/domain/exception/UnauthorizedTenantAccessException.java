/*
 * Govinda ERP - Unauthorized Tenant Access Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

import java.util.UUID;

public final class UnauthorizedTenantAccessException extends DomainException {
    public UnauthorizedTenantAccessException(UUID tenantId) {
        super("Access denied to tenant: " + tenantId, "UNAUTHORIZED_TENANT_ACCESS");
    }
}

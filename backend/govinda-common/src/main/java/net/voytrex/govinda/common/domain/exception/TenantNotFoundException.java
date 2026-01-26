/*
 * Govinda ERP - Tenant Not Found Exception
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

import java.util.UUID;

public final class TenantNotFoundException extends DomainException {
    public TenantNotFoundException(UUID tenantId) {
        super("Tenant not found: " + tenantId, "TENANT_NOT_FOUND");
    }
}

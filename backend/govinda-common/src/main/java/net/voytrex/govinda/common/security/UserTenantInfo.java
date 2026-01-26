/*
 * Govinda ERP - User Tenant Info
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.security;

import java.util.UUID;

/**
 * User tenant information.
 */
public record UserTenantInfo(
    UUID tenantId,
    String tenantCode,
    String tenantName,
    String roleCode,
    String roleName,
    boolean isDefault
) {}

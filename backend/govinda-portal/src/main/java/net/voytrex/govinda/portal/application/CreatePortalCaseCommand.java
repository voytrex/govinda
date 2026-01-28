/*
 * Govinda ERP - Create Portal Case Command
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.application;

import java.util.UUID;
import net.voytrex.govinda.portal.domain.model.PortalCaseType;

public record CreatePortalCaseCommand(
    UUID tenantId,
    UUID personId,
    PortalCaseType type,
    String subject,
    String description
) { }

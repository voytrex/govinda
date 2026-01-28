/*
 * Govinda ERP - Portal Profile Update Request
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import net.voytrex.govinda.common.domain.model.Language;
import org.springframework.lang.Nullable;

public record PortalProfileUpdateRequest(
    @Nullable String nationality,
    @Nullable Language preferredLanguage
) { }

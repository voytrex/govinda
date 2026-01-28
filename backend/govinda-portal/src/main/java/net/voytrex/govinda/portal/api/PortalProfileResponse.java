/*
 * Govinda ERP - Portal Profile Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;

public record PortalProfileResponse(
    UUID id,
    String firstName,
    String lastName,
    String fullName,
    LocalDate dateOfBirth,
    Gender gender,
    Language preferredLanguage,
    String nationality,
    String status
) { }

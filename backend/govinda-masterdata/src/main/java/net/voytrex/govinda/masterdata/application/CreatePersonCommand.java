/*
 * Govinda ERP - Create Person Command
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;

public record CreatePersonCommand(
    UUID tenantId,
    String ahvNr,
    String lastName,
    String firstName,
    LocalDate dateOfBirth,
    Gender gender,
    MaritalStatus maritalStatus,
    String nationality,
    Language preferredLanguage
) { }

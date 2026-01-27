/*
 * Govinda ERP - Person Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import java.time.LocalDate;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.AgeGroup;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;

public record PersonResponse(
    UUID id,
    String ahvNr,
    String lastName,
    String firstName,
    String fullName,
    LocalDate dateOfBirth,
    Gender gender,
    int age,
    AgeGroup ageGroup,
    MaritalStatus maritalStatus,
    String nationality,
    Language preferredLanguage,
    String status
) { }

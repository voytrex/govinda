/*
 * Govinda ERP - Create Person Request
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;

public record CreatePersonRequest(
    @NotBlank(message = "AHV number is required")
    String ahvNr,
    @NotBlank(message = "Last name is required")
    String lastName,
    @NotBlank(message = "First name is required")
    String firstName,
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,
    Gender gender,
    MaritalStatus maritalStatus,
    String nationality,
    Language preferredLanguage
) { }

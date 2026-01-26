/*
 * Govinda ERP - Person Search Query
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import java.time.LocalDate;
import java.util.UUID;

public record PersonSearchQuery(
    UUID tenantId,
    String lastName,
    String firstName,
    String ahvNr,
    LocalDate dateOfBirth,
    String postalCode
) { }

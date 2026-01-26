/*
 * Govinda ERP - Update Person Command
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import java.util.UUID;
import net.voytrex.govinda.common.domain.model.Language;

public record UpdatePersonCommand(
    UUID tenantId,
    UUID personId,
    String nationality,
    Language preferredLanguage
) { }

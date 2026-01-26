/*
 * Govinda ERP - Update Person Request
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import net.voytrex.govinda.common.domain.model.Language;

public record UpdatePersonRequest(
    String nationality,
    Language preferredLanguage
) {}

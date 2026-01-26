/*
 * Govinda ERP - Authentication Response
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

public record LoginResponse(
    String token,
    String tokenType,
    String message
) {}

/*
 * Govinda ERP - API Error Detail
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

/**
 * Detailed error information for validation errors.
 */
public record ErrorDetail(
    String field,
    String message,
    Object rejectedValue
) {}

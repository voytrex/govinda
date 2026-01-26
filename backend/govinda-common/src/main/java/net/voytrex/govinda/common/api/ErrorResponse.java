/*
 * Govinda ERP - API Error Response
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response for API errors.
 */
public record ErrorResponse(
    String errorCode,
    String message,
    Instant timestamp,
    String path,
    List<ErrorDetail> details
) {
    public ErrorResponse(String errorCode, String message, Instant timestamp, String path, List<ErrorDetail> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.path = path;
        this.details = details;
    }

    public ErrorResponse(String errorCode, String message, String path) {
        this(errorCode, message, Instant.now(), path, null);
    }
}

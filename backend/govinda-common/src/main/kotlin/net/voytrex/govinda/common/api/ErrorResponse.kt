/*
 * Govinda ERP - API Error Response
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api

import java.time.Instant

/**
 * Standard error response for API errors.
 */
data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val path: String? = null,
    val details: List<ErrorDetail>? = null
)

/**
 * Detailed error information for validation errors.
 */
data class ErrorDetail(
    val field: String?,
    val message: String,
    val rejectedValue: Any? = null
)

/**
 * API response wrapper for paginated results.
 */
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)

/*
 * Govinda ERP - API Info Response
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

/**
 * API information response.
 */
public record ApiInfo(
    String name,
    String version,
    String description,
    String documentation,
    String apiDocs,
    String health
) { }

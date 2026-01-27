/*
 * Govinda ERP - Product Category
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Product categories for VVG products.
 *
 * Use {@link net.voytrex.govinda.common.i18n.I18nService#translateProductCategory(String, Language)}
 * to get localized product category names.
 */
public enum ProductCategory {
    /** Basic mandatory insurance */
    BASIC("BASIC"),
    /** Hospital supplementary insurance */
    HOSPITAL("HOSP"),
    /** Dental insurance */
    DENTAL("DENT"),
    /** Alternative/complementary medicine */
    ALTERNATIVE("ALT"),
    /** Travel/abroad insurance */
    TRAVEL("TRAV"),
    /** Daily sickness allowance */
    DAILY_ALLOWANCE("TAGG");

    private final String code;

    ProductCategory(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

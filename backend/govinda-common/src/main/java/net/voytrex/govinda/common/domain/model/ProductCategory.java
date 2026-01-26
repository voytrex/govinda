/*
 * Govinda ERP - Product Category
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Product categories for VVG products.
 */
public enum ProductCategory {
    /** Basic mandatory insurance */
    BASIC("BASIC", "Grundversicherung"),
    /** Hospital supplementary insurance */
    HOSPITAL("HOSP", "Spitalzusatzversicherung"),
    /** Dental insurance */
    DENTAL("DENT", "Zahnversicherung"),
    /** Alternative/complementary medicine */
    ALTERNATIVE("ALT", "Alternativmedizin"),
    /** Travel/abroad insurance */
    TRAVEL("TRAV", "Auslandsversicherung"),
    /** Daily sickness allowance */
    DAILY_ALLOWANCE("TAGG", "Taggeldversicherung");

    private final String code;
    private final String nameDe;

    ProductCategory(String code, String nameDe) {
        this.code = code;
        this.nameDe = nameDe;
    }

    public String getCode() {
        return code;
    }

    public String getNameDe() {
        return nameDe;
    }
}

/*
 * Govinda ERP - Product Type
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Product types in Swiss health insurance.
 */
public enum ProductType {
    /** KVG - Mandatory basic health insurance */
    KVG("KVG", "Obligatorische Krankenpflegeversicherung"),
    /** VVG - Voluntary supplementary insurance */
    VVG("VVG", "Zusatzversicherung");

    private final String code;
    private final String nameDe;

    ProductType(String code, String nameDe) {
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

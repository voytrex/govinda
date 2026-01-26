/*
 * Govinda ERP - Product Type
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Product types in Swiss health insurance.
 *
 * Use {@link net.voytrex.govinda.common.i18n.I18nService#translateProductType(String, Language)}
 * to get localized product type names.
 */
public enum ProductType {
    /** KVG - Mandatory basic health insurance */
    KVG("KVG"),
    /** VVG - Voluntary supplementary insurance */
    VVG("VVG");

    private final String code;

    ProductType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

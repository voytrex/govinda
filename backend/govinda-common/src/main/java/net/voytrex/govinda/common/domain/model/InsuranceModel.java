/*
 * Govinda ERP - Insurance Model
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * KVG insurance models (Versicherungsmodelle).
 *
 * Different models offer premium discounts in exchange for
 * restrictions on healthcare provider choice.
 *
 * Use {@link net.voytrex.govinda.common.i18n.I18nService#translateInsuranceModel(String, Language)}
 * to get localized insurance model names.
 */
public enum InsuranceModel {
    /** Standard model - free choice of provider */
    STANDARD("STD", false),
    /** HMO model - must use HMO center */
    HMO("HMO", true),
    /** Family doctor model - must consult family doctor first */
    HAUSARZT("HAM", true),
    /** Telemedicine model - must call hotline first */
    TELMED("TLM", true);

    private final String code;
    private final boolean hasProviderRestriction;

    InsuranceModel(String code, boolean hasProviderRestriction) {
        this.code = code;
        this.hasProviderRestriction = hasProviderRestriction;
    }

    public String getCode() {
        return code;
    }

    public boolean hasProviderRestriction() {
        return hasProviderRestriction;
    }

    public static InsuranceModel fromCode(String code) {
        for (InsuranceModel model : values()) {
            if (model.code.equalsIgnoreCase(code)) {
                return model;
            }
        }
        throw new IllegalArgumentException("Unknown insurance model code: " + code);
    }
}

/*
 * Govinda ERP - Insurance Model
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * KVG insurance models (Versicherungsmodelle).
 *
 * Different models offer premium discounts in exchange for
 * restrictions on healthcare provider choice.
 */
public enum InsuranceModel {
    /** Standard model - free choice of provider */
    STANDARD("STD", "Standard", false),
    /** HMO model - must use HMO center */
    HMO("HMO", "HMO", true),
    /** Family doctor model - must consult family doctor first */
    HAUSARZT("HAM", "Hausarzt-Modell", true),
    /** Telemedicine model - must call hotline first */
    TELMED("TLM", "Telmed", true);

    private final String code;
    private final String nameDe;
    private final boolean hasProviderRestriction;

    InsuranceModel(String code, String nameDe, boolean hasProviderRestriction) {
        this.code = code;
        this.nameDe = nameDe;
        this.hasProviderRestriction = hasProviderRestriction;
    }

    public String getCode() {
        return code;
    }

    public String getNameDe() {
        return nameDe;
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

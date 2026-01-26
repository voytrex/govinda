/*
 * Govinda ERP - Canton Enumeration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Swiss cantons.
 *
 * Use {@link net.voytrex.govinda.common.i18n.I18nService#translateCanton(String, Language)}
 * to get localized canton names.
 */
public enum Canton {
    ZH("ZH"),
    BE("BE"),
    LU("LU"),
    UR("UR"),
    SZ("SZ"),
    OW("OW"),
    NW("NW"),
    GL("GL"),
    ZG("ZG"),
    FR("FR"),
    SO("SO"),
    BS("BS"),
    BL("BL"),
    SH("SH"),
    AR("AR"),
    AI("AI"),
    SG("SG"),
    GR("GR"),
    AG("AG"),
    TG("TG"),
    TI("TI"),
    VD("VD"),
    VS("VS"),
    NE("NE"),
    GE("GE"),
    JU("JU");

    private final String code;

    Canton(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Canton fromCode(String code) {
        for (Canton canton : values()) {
            if (canton.code.equalsIgnoreCase(code)) {
                return canton;
            }
        }
        throw new IllegalArgumentException("Unknown canton code: " + code);
    }
}

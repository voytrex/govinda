/*
 * Govinda ERP - Canton Enumeration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Swiss cantons.
 */
public enum Canton {
    ZH("ZH", "Zürich", "Zurich"),
    BE("BE", "Bern", "Berne"),
    LU("LU", "Luzern", "Lucerne"),
    UR("UR", "Uri", "Uri"),
    SZ("SZ", "Schwyz", "Schwytz"),
    OW("OW", "Obwalden", "Obwald"),
    NW("NW", "Nidwalden", "Nidwald"),
    GL("GL", "Glarus", "Glaris"),
    ZG("ZG", "Zug", "Zoug"),
    FR("FR", "Freiburg", "Fribourg"),
    SO("SO", "Solothurn", "Soleure"),
    BS("BS", "Basel-Stadt", "Bâle-Ville"),
    BL("BL", "Basel-Landschaft", "Bâle-Campagne"),
    SH("SH", "Schaffhausen", "Schaffhouse"),
    AR("AR", "Appenzell Ausserrhoden", "Appenzell Rhodes-Extérieures"),
    AI("AI", "Appenzell Innerrhoden", "Appenzell Rhodes-Intérieures"),
    SG("SG", "St. Gallen", "Saint-Gall"),
    GR("GR", "Graubünden", "Grisons"),
    AG("AG", "Aargau", "Argovie"),
    TG("TG", "Thurgau", "Thurgovie"),
    TI("TI", "Tessin", "Tessin"),
    VD("VD", "Waadt", "Vaud"),
    VS("VS", "Wallis", "Valais"),
    NE("NE", "Neuenburg", "Neuchâtel"),
    GE("GE", "Genf", "Genève"),
    JU("JU", "Jura", "Jura");

    private final String code;
    private final String nameDe;
    private final String nameFr;

    Canton(String code, String nameDe, String nameFr) {
        this.code = code;
        this.nameDe = nameDe;
        this.nameFr = nameFr;
    }

    public String getCode() {
        return code;
    }

    public String getNameDe() {
        return nameDe;
    }

    public String getNameFr() {
        return nameFr;
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

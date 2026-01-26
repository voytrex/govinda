/*
 * Govinda ERP - Language Enumeration
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

/**
 * Swiss national languages plus English.
 *
 * Use {@link net.voytrex.govinda.common.i18n.I18nService#translateLanguage(String, Language)}
 * to get localized language display names.
 */
public enum Language {
    DE("de"),
    FR("fr"),
    IT("it"),
    EN("en");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Language fromCode(String code) {
        for (Language language : values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }
}

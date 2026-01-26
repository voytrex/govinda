/*
 * Govinda ERP - Localized Text
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Localized text supporting multiple languages.
 *
 * Used for product names, descriptions, and other user-facing text
 * that must be available in all Swiss national languages plus English.
 */
@Embeddable
public class LocalizedText {
    @Column(name = "text_de", length = 1000)
    private String de;

    @Column(name = "text_fr", length = 1000)
    private String fr;

    @Column(name = "text_it", length = 1000)
    private String it;

    @Column(name = "text_en", length = 1000)
    private String en;

    protected LocalizedText() {
    }

    public LocalizedText(String de, String fr, String it, String en) {
        this.de = de;
        this.fr = fr;
        this.it = it;
        this.en = en;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    /**
     * Gets the text for the specified language.
     * Falls back to German if the specified language is not available.
     */
    public String get(Language language) {
        return switch (language) {
            case DE -> de;
            case FR -> isBlank(fr) ? de : fr;
            case IT -> isBlank(it) ? de : it;
            case EN -> isBlank(en) ? de : en;
        };
    }

    /**
     * Returns true if all language variants are blank.
     */
    public boolean isBlank() {
        return isBlank(de) && isBlank(fr) && isBlank(it) && isBlank(en);
    }

    public static LocalizedText of(String text) {
        return new LocalizedText(text, text, text, text);
    }

    public static LocalizedText empty() {
        return new LocalizedText("", "", "", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LocalizedText that = (LocalizedText) other;
        return Objects.equals(de, that.de)
            && Objects.equals(fr, that.fr)
            && Objects.equals(it, that.it)
            && Objects.equals(en, that.en);
    }

    @Override
    public int hashCode() {
        return Objects.hash(de, fr, it, en);
    }
}

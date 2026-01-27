/*
 * Govinda ERP - Internationalization Service
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.i18n;

import net.voytrex.govinda.common.domain.model.Language;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service for internationalization and translation.
 *
 * Provides methods to translate enum codes and error messages
 * based on the user's language preference.
 */
@Service
public class I18nService {
    private final MessageSource messageSource;

    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Translates a message key to the specified language.
     *
     * @param key the message key
     * @param language the target language
     * @param args optional arguments for message formatting
     * @return the translated message
     */
    public String translate(String key, Language language, Object... args) {
        Locale locale = toLocale(language);
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * Translates a canton code to its display name in the specified language.
     *
     * @param cantonCode the canton code (e.g., "ZH", "BE")
     * @param language the target language
     * @return the translated canton name
     */
    public String translateCanton(String cantonCode, Language language) {
        return translate("canton." + cantonCode, language);
    }

    /**
     * Translates an insurance model code to its display name in the specified language.
     *
     * @param modelCode the insurance model code (e.g., "STD", "HMO")
     * @param language the target language
     * @return the translated insurance model name
     */
    public String translateInsuranceModel(String modelCode, Language language) {
        return translate("insurance.model." + modelCode, language);
    }

    /**
     * Translates a product category code to its display name in the specified language.
     *
     * @param categoryCode the product category code (e.g., "BASIC", "HOSP")
     * @param language the target language
     * @return the translated product category name
     */
    public String translateProductCategory(String categoryCode, Language language) {
        return translate("product.category." + categoryCode, language);
    }

    /**
     * Translates a product type code to its display name in the specified language.
     *
     * @param typeCode the product type code (e.g., "KVG", "VVG")
     * @param language the target language
     * @return the translated product type name
     */
    public String translateProductType(String typeCode, Language language) {
        return translate("product.type." + typeCode, language);
    }

    /**
     * Translates a language code to its display name in the specified language.
     *
     * @param languageCode the language code (e.g., "DE", "FR")
     * @param targetLanguage the target language for the display name
     * @return the translated language display name
     */
    public String translateLanguage(String languageCode, Language targetLanguage) {
        return translate("language." + languageCode, targetLanguage);
    }

    /**
     * Translates an error code to its message in the specified language.
     *
     * @param errorCode the error code (e.g., "ENTITY_NOT_FOUND")
     * @param language the target language
     * @param args optional arguments for message formatting
     * @return the translated error message
     */
    public String translateError(String errorCode, Language language, Object... args) {
        String key = "error." + errorCode.toLowerCase().replace("_", ".");
        return translate(key, language, args);
    }

    /**
     * Converts a Language enum to a Java Locale.
     */
    private Locale toLocale(Language language) {
        if (language == null) {
            return Locale.ENGLISH;
        }

        return switch (language) {
            case DE -> Locale.GERMAN;
            case FR -> Locale.FRENCH;
            case IT -> Locale.ITALIAN;
            case EN -> Locale.ENGLISH;
        };
    }
}

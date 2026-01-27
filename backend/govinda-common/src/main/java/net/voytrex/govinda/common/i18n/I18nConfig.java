/*
 * Govinda ERP - Internationalization Configuration
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.i18n;

import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuration for internationalization (i18n).
 *
 * Supports four languages: German (DE), French (FR), Italian (IT), and English (EN).
 * German is the default/fallback language.
 */
@Configuration
public class I18nConfig {

    /**
     * Configures MessageSource for translation lookup.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Configures locale resolver based on Accept-Language header.
     * Defaults to German (DE) if no language preference is specified.
     */
    @Bean
    @SuppressWarnings("null") // Spring API lacks nullness annotations; boundary suppression.
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.GERMAN);
        List<Locale> supportedLocales = List.of(
            Locale.GERMAN,
            Locale.FRENCH,
            Locale.ITALIAN,
            Locale.ENGLISH
        );
        localeResolver.setSupportedLocales(supportedLocales);
        return localeResolver;
    }
}

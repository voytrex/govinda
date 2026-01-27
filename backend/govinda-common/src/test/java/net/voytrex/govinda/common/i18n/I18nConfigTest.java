/*
 * Govinda ERP - I18n Configuration Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

class I18nConfigTest {

    @Test
    void shouldConfigureMessageSource() {
        I18nConfig config = new I18nConfig();

        var messageSource = config.messageSource();

        assertThat(messageSource).isInstanceOf(ResourceBundleMessageSource.class);
        ResourceBundleMessageSource bundle = (ResourceBundleMessageSource) messageSource;
        assertThat(bundle.getBasenameSet()).containsExactly("messages");
        assertThat(bundle.getMessage("unknown.key", null, Locale.GERMAN)).isEqualTo("unknown.key");
    }

    @Test
    void shouldConfigureLocaleResolver() {
        I18nConfig config = new I18nConfig();

        var localeResolver = config.localeResolver();

        assertThat(localeResolver).isInstanceOf(AcceptHeaderLocaleResolver.class);
        AcceptHeaderLocaleResolver resolver = (AcceptHeaderLocaleResolver) localeResolver;
        var defaultRequest = new MockHttpServletRequest();
        assertThat(resolver.resolveLocale(defaultRequest)).isEqualTo(Locale.GERMAN);

        var frenchRequest = new MockHttpServletRequest();
        frenchRequest.addHeader("Accept-Language", "fr");
        assertThat(resolver.resolveLocale(frenchRequest)).isEqualTo(Locale.FRENCH);
    }
}

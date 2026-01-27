/*
 * Govinda ERP - I18n Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import net.voytrex.govinda.common.domain.model.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

@Tag("unit")
@Tag("fast")
class I18nServiceTest {

    private final I18nService service = new I18nService(messageSource());

    @Nested
    @DisplayName("Enum translations")
    class EnumTranslations {

        @Test
        void shouldTranslateCanton() {
            assertThat(service.translateCanton("ZH", Language.DE)).isEqualTo("Zürich");
            assertThat(service.translateCanton("ZH", Language.EN)).isEqualTo("Zurich");
        }

        @Test
        void shouldTranslateInsuranceModel() {
            assertThat(service.translateInsuranceModel("HAM", Language.FR)).isEqualTo("Modèle médecin de famille");
        }

        @Test
        void shouldTranslateProductCategory() {
            assertThat(service.translateProductCategory("BASIC", Language.IT)).isEqualTo("Assicurazione di base");
        }

        @Test
        void shouldTranslateProductType() {
            assertThat(service.translateProductType("KVG", Language.EN)).isEqualTo("Mandatory Health Insurance");
        }

        @Test
        void shouldTranslateLanguageDisplayName() {
            assertThat(service.translateLanguage("DE", Language.FR)).isEqualTo("Allemand");
        }
    }

    @Nested
    @DisplayName("Errors")
    class Errors {

        @Test
        void shouldTranslateErrorCodeToMessage() {
            assertThat(service.translateError("ENTITY_NOT_FOUND", Language.IT)).isEqualTo("Entità non trovata");
        }
    }

    @Nested
    @DisplayName("Locale fallback")
    class LocaleFallback {

        @Test
        void shouldFallbackToEnglish_when_languageIsNull() {
            assertThat(service.translateError("ENTITY_NOT_FOUND", null)).isEqualTo("Entity not found");
        }
    }

    private MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}

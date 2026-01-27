/*
 * Govinda ERP - Global Exception Handler Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.AuthenticationException;
import net.voytrex.govinda.common.domain.exception.BusinessRuleViolationException;
import net.voytrex.govinda.common.domain.exception.ConcurrentModificationException;
import net.voytrex.govinda.common.domain.exception.CoverageValidationException;
import net.voytrex.govinda.common.domain.exception.DuplicateEntityException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.common.domain.exception.InvalidMutationException;
import net.voytrex.govinda.common.domain.exception.PremiumCalculationException;
import net.voytrex.govinda.common.domain.exception.TariffNotFoundException;
import net.voytrex.govinda.common.domain.exception.TenantNotFoundException;
import net.voytrex.govinda.common.domain.exception.UnauthorizedTenantAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Tag("unit")
@Tag("fast")
class GlobalExceptionHandlerTest {

    private final MessageSource messageSource = messageSource();

    @Nested
    @DisplayName("Domain exceptions")
    class DomainExceptions {

        @Test
        void shouldHandleNotFound() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/persons/123");

            ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new EntityNotFoundException("Person", UUID.randomUUID()),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().errorCode()).isEqualTo("ENTITY_NOT_FOUND");
            assertThat(response.getBody().path()).isEqualTo("/api/v1/persons/123");
        }

        @Test
        void shouldHandleDuplicate() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/persons");

            ResponseEntity<ErrorResponse> response = handler.handleDuplicate(
                new DuplicateEntityException("Person", "ahv", "756"),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().errorCode()).isEqualTo("DUPLICATE_ENTITY");
        }

        @Test
        void shouldHandleValidationErrors() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/policies");

            ResponseEntity<ErrorResponse> response = handler.handleValidation(
                new InvalidMutationException("invalid"),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody().errorCode()).isEqualTo("INVALID_MUTATION");
        }

        @Test
        void shouldHandleBusinessRuleViolation() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/policies");

            ResponseEntity<ErrorResponse> response = handler.handleValidation(
                new BusinessRuleViolationException("rule", "details"),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody().errorCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
        }

        @Test
        void shouldHandlePremiumErrors() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/premiums");

            ResponseEntity<ErrorResponse> response = handler.handlePremiumError(
                new TariffNotFoundException(2025, "KVG", null),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody().errorCode()).isEqualTo("TARIFF_NOT_FOUND");
        }

        @Test
        void shouldHandleAuthenticationErrors() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/auth/login");

            ResponseEntity<ErrorResponse> response = handler.handleAuthentication(
                new AuthenticationException("invalid credentials"),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().errorCode()).isEqualTo("AUTHENTICATION_ERROR");
        }

        @Test
        void shouldHandleTenantErrors() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/tenants");

            ResponseEntity<ErrorResponse> notFound = handler.handleTenantError(
                new TenantNotFoundException(UUID.randomUUID()),
                request
            );

            ResponseEntity<ErrorResponse> forbidden = handler.handleTenantError(
                new UnauthorizedTenantAccessException(UUID.randomUUID()),
                request
            );

            assertThat(notFound.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        void shouldHandleConcurrentModification() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/persons/123");

            ResponseEntity<ErrorResponse> response = handler.handleConcurrentModification(
                new ConcurrentModificationException("Person", UUID.randomUUID()),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().errorCode()).isEqualTo("CONCURRENT_MODIFICATION");
        }
    }

    @Nested
    @DisplayName("Security exceptions")
    class SecurityExceptions {

        @Test
        void shouldHandleAuthenticationCredentialsMissing() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/secure");

            ResponseEntity<ErrorResponse> response = handler.handleAuthenticationCredentialsNotFound(
                new AuthenticationCredentialsNotFoundException(null),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().message()).isEqualTo("Authentication required");
        }

        @Test
        void shouldHandleAccessDenied() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/secure");

            ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException(null),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().message()).isEqualTo("Access denied");
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        void shouldHandleMethodArgumentNotValid() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/persons");

            Object target = new Object();
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "person");
            bindingResult.addError(new FieldError("person", "name", "Hans", false, null, null, null));

            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
            ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().errorCode()).isEqualTo("VALIDATION_ERROR");
            assertThat(response.getBody().details()).hasSize(1);
            ErrorDetail detail = response.getBody().details().getFirst();
            assertThat(detail.field()).isEqualTo("name");
            assertThat(detail.message()).isEqualTo("Invalid value");
            assertThat(detail.rejectedValue()).isEqualTo("Hans");
        }
    }

    @Nested
    @DisplayName("Resources")
    class Resources {

        @Test
        void shouldHandleNoResourceFound() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/missing");

            ResponseEntity<ErrorResponse> response = handler.handleNoResourceFound(
                new NoResourceFoundException(HttpMethod.GET, "/missing"),
                request
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().message()).contains("/missing");
        }
    }

    @Nested
    @DisplayName("Generic")
    class Generic {

        @Test
        void shouldHandleUnhandledException() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource, new FixedLocaleResolver(Locale.ENGLISH));
            MockHttpServletRequest request = request("/api/v1/unknown");

            ResponseEntity<ErrorResponse> response = handler.handleGeneric(new Exception(), request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().message()).isEqualTo("An internal error occurred");
        }
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }

    private MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    private static final class FixedLocaleResolver implements LocaleResolver {
        private final Locale locale;

        private FixedLocaleResolver(Locale locale) {
            this.locale = locale;
        }

        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            return locale;
        }

        @Override
        public void setLocale(
            HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Locale locale
        ) {
        }
    }
}

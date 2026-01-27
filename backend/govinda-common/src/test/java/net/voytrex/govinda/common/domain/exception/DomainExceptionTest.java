/*
 * Govinda ERP - Domain Exception Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class DomainExceptionTest {

    @Nested
    @DisplayName("Not Found")
    class NotFound {

        @Test
        void shouldCreateEntityNotFoundException() {
            UUID id = UUID.randomUUID();
            EntityNotFoundException exception = new EntityNotFoundException("Person", id);

            assertThat(exception.getErrorCode()).isEqualTo("ENTITY_NOT_FOUND");
            assertThat(exception.getMessage()).contains("Person").contains(id.toString());
        }

        @Test
        void shouldCreateEntityNotFoundByFieldException() {
            EntityNotFoundByFieldException exception = new EntityNotFoundByFieldException("Person", "ahv", "756");

            assertThat(exception.getErrorCode()).isEqualTo("ENTITY_NOT_FOUND");
            assertThat(exception.getMessage()).contains("Person").contains("ahv").contains("756");
        }
    }

    @Nested
    @DisplayName("Duplicates")
    class Duplicates {

        @Test
        void shouldCreateDuplicateEntityException() {
            DuplicateEntityException exception = new DuplicateEntityException("Person", "ahv", "756");

            assertThat(exception.getErrorCode()).isEqualTo("DUPLICATE_ENTITY");
            assertThat(exception.getMessage()).contains("Person").contains("ahv").contains("756");
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        void shouldCreateInvalidAhvNumberException() {
            InvalidAhvNumberException exception = new InvalidAhvNumberException("invalid");

            assertThat(exception.getErrorCode()).isEqualTo("INVALID_AHV_NUMBER");
            assertThat(exception.getMessage()).contains("Invalid AHV number format");
        }

        @Test
        void shouldCreatePolicyValidationException() {
            PolicyValidationException exception = new PolicyValidationException("policy invalid");

            assertThat(exception.getErrorCode()).isEqualTo("POLICY_VALIDATION_ERROR");
            assertThat(exception.getMessage()).contains("policy invalid");
        }

        @Test
        void shouldCreateCoverageValidationException() {
            CoverageValidationException exception = new CoverageValidationException("coverage invalid");

            assertThat(exception.getErrorCode()).isEqualTo("COVERAGE_VALIDATION_ERROR");
            assertThat(exception.getMessage()).contains("coverage invalid");
        }

        @Test
        void shouldCreateInvalidMutationException() {
            InvalidMutationException exception = new InvalidMutationException("mutation invalid");

            assertThat(exception.getErrorCode()).isEqualTo("INVALID_MUTATION");
            assertThat(exception.getMessage()).contains("mutation invalid");
        }
    }

    @Nested
    @DisplayName("Premium")
    class Premium {

        @Test
        void shouldCreatePremiumCalculationException() {
            PremiumCalculationException exception = new PremiumCalculationException("calc failed");

            assertThat(exception.getErrorCode()).isEqualTo("PREMIUM_CALCULATION_ERROR");
            assertThat(exception.getMessage()).contains("calc failed");
        }

        @Test
        void shouldPropagateCauseForPremiumCalculationException() {
            RuntimeException cause = new RuntimeException("cause");
            PremiumCalculationException exception = new PremiumCalculationException("calc failed", cause);

            assertThat(exception.getErrorCode()).isEqualTo("PREMIUM_CALCULATION_ERROR");
            assertThat(exception.getCause()).isEqualTo(cause);
        }

        @Test
        void shouldCreateTariffNotFoundException() {
            UUID regionId = UUID.randomUUID();
            TariffNotFoundException exception = new TariffNotFoundException(2025, "KVG", regionId);

            assertThat(exception.getErrorCode()).isEqualTo("TARIFF_NOT_FOUND");
            assertThat(exception.getMessage()).contains("2025").contains("KVG").contains(regionId.toString());
        }

        @Test
        void shouldCreateTariffNotFoundExceptionWithoutRegion() {
            TariffNotFoundException exception = new TariffNotFoundException(2025, "KVG", null);

            assertThat(exception.getErrorCode()).isEqualTo("TARIFF_NOT_FOUND");
            assertThat(exception.getMessage()).contains("2025").contains("KVG").doesNotContain("region");
        }
    }

    @Nested
    @DisplayName("Tenant")
    class Tenant {

        @Test
        void shouldCreateTenantNotFoundException() {
            UUID tenantId = UUID.randomUUID();
            TenantNotFoundException exception = new TenantNotFoundException(tenantId);

            assertThat(exception.getErrorCode()).isEqualTo("TENANT_NOT_FOUND");
            assertThat(exception.getMessage()).contains(tenantId.toString());
        }

        @Test
        void shouldCreateUnauthorizedTenantAccessException() {
            UUID tenantId = UUID.randomUUID();
            UnauthorizedTenantAccessException exception = new UnauthorizedTenantAccessException(tenantId);

            assertThat(exception.getErrorCode()).isEqualTo("UNAUTHORIZED_TENANT_ACCESS");
            assertThat(exception.getMessage()).contains(tenantId.toString());
        }
    }

    @Nested
    @DisplayName("Concurrency")
    class Concurrency {

        @Test
        void shouldCreateConcurrentModificationException() {
            UUID id = UUID.randomUUID();
            ConcurrentModificationException exception = new ConcurrentModificationException("Person", id);

            assertThat(exception.getErrorCode()).isEqualTo("CONCURRENT_MODIFICATION");
            assertThat(exception.getMessage()).contains("Person").contains(id.toString());
        }
    }

    @Nested
    @DisplayName("Business Rules")
    class BusinessRules {

        @Test
        void shouldCreateBusinessRuleViolationWithDetails() {
            BusinessRuleViolationException exception = new BusinessRuleViolationException("rule", "details");

            assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
            assertThat(exception.getMessage()).contains("rule").contains("details");
        }

        @Test
        void shouldCreateBusinessRuleViolationWithoutDetails() {
            BusinessRuleViolationException exception = new BusinessRuleViolationException("rule", null);

            assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
            assertThat(exception.getMessage()).contains("rule");
        }
    }

    @Nested
    @DisplayName("Authentication")
    class Authentication {

        @Test
        void shouldCreateAuthenticationException() {
            AuthenticationException exception = new AuthenticationException("auth failed");

            assertThat(exception.getErrorCode()).isEqualTo("AUTHENTICATION_ERROR");
            assertThat(exception.getMessage()).contains("auth failed");
        }
    }
}

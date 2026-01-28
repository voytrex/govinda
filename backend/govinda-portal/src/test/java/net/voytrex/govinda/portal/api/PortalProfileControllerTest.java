/*
 * Govinda ERP - Portal Profile Controller Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.portal.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import net.voytrex.govinda.common.api.GlobalExceptionHandler;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.portal.application.PortalIdentityService;
import net.voytrex.govinda.portal.application.PortalProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;

@ExtendWith(MockitoExtension.class)
class PortalProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortalProfileService portalProfileService;

    @Mock
    private PortalIdentityService portalIdentityService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    private ObjectMapper objectMapper;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID personId = UUID.randomUUID();
    private final String subject = "portal-subject-123";

    @BeforeEach
    void setUp() {
        var controller = new PortalProfileController(portalProfileService, portalIdentityService);
        var globalExceptionHandler = new GlobalExceptionHandler(messageSource, localeResolver);

        lenient().when(localeResolver.resolveLocale(any())).thenReturn(Locale.ENGLISH);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(globalExceptionHandler)
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("Get Portal Profile")
    class GetPortalProfile {

        @Test
        @DisplayName("should return portal profile when person exists")
        void should_returnProfile_when_personExists() throws Exception {
            var person = createTestPerson();
            when(portalIdentityService.resolvePersonId(eq(tenantId), eq(subject))).thenReturn(personId);
            when(portalProfileService.getProfile(eq(tenantId), eq(personId))).thenReturn(person);

            mockMvc.perform(
                    get("/api/portal/v1/profile")
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-Portal-Subject", subject)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personId.toString()))
                .andExpect(jsonPath("$.firstName").value("Hans"))
                .andExpect(jsonPath("$.lastName").value("Müller"))
                .andExpect(jsonPath("$.fullName").value("Hans Müller"))
                .andExpect(jsonPath("$.preferredLanguage").value("DE"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return 404 when person not found")
        void should_return404_when_personNotFound() throws Exception {
            when(portalIdentityService.resolvePersonId(eq(tenantId), eq(subject))).thenReturn(personId);
            when(portalProfileService.getProfile(eq(tenantId), eq(personId)))
                .thenThrow(new EntityNotFoundException("Person", personId));

            mockMvc.perform(
                    get("/api/portal/v1/profile")
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-Portal-Subject", subject)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
        }
    }

    private Person createTestPerson() {
        var person = new Person(
            tenantId,
            new AhvNumber("756.1234.5678.90"),
            "Müller",
            "Hans",
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            MaritalStatus.SINGLE,
            "CHE",
            Language.DE
        );
        person.setId(personId);
        return person;
    }
}

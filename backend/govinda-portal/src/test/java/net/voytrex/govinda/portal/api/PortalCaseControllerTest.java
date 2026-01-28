/*
 * Govinda ERP - Portal Case Controller Tests
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.voytrex.govinda.common.api.GlobalExceptionHandler;
import net.voytrex.govinda.portal.application.PortalCaseService;
import net.voytrex.govinda.portal.application.PortalIdentityService;
import net.voytrex.govinda.portal.domain.model.PortalCase;
import net.voytrex.govinda.portal.domain.model.PortalCaseType;
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
class PortalCaseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortalCaseService portalCaseService;

    @Mock
    private PortalIdentityService portalIdentityService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    private ObjectMapper objectMapper;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID personId = UUID.randomUUID();
    private final String subject = "portal-subject-456";

    @BeforeEach
    void setUp() {
        var controller = new PortalCaseController(portalCaseService, portalIdentityService);
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
    @DisplayName("Create Portal Case")
    class CreatePortalCase {

        @Test
        @DisplayName("should create case and return 201")
        void should_createCase_when_validRequest() throws Exception {
            var portalCase = new PortalCase(
                tenantId,
                personId,
                PortalCaseType.ADDRESS_CHANGE,
                "Moving to new address",
                "New address from 2026-03-01"
            );
            var caseId = UUID.randomUUID();
            portalCase.setId(caseId);

            when(portalCaseService.createCase(any())).thenReturn(portalCase);
            when(portalIdentityService.resolvePersonId(eq(tenantId), eq(subject))).thenReturn(personId);

            var request = new PortalCaseCreateRequest(
                PortalCaseType.ADDRESS_CHANGE,
                "Moving to new address",
                "New address from 2026-03-01"
            );

            mockMvc.perform(
                    post("/api/portal/v1/cases")
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-Portal-Subject", subject)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(caseId.toString()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.type").value("ADDRESS_CHANGE"));
        }
    }

    @Nested
    @DisplayName("List Portal Cases")
    class ListPortalCases {

        @Test
        @DisplayName("should return list of cases")
        void should_returnCases_when_exists() throws Exception {
            var portalCase = new PortalCase(
                tenantId,
                personId,
                PortalCaseType.ADDRESS_CHANGE,
                "Address change",
                "Moving to new city"
            );
            var caseId = UUID.randomUUID();
            portalCase.setId(caseId);

            when(portalCaseService.listCases(eq(tenantId), eq(personId)))
                .thenReturn(List.of(portalCase));
            when(portalIdentityService.resolvePersonId(eq(tenantId), eq(subject))).thenReturn(personId);

            mockMvc.perform(
                    get("/api/portal/v1/cases")
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-Portal-Subject", subject)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(caseId.toString()))
                .andExpect(jsonPath("$[0].subject").value("Address change"));
        }
    }
}

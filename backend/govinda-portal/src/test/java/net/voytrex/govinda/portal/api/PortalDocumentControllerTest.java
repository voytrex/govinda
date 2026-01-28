/*
 * Govinda ERP - Portal Document Controller Tests
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.voytrex.govinda.common.api.GlobalExceptionHandler;
import net.voytrex.govinda.portal.application.PortalDocumentService;
import net.voytrex.govinda.portal.application.PortalIdentityService;
import net.voytrex.govinda.portal.domain.model.PortalDocument;
import net.voytrex.govinda.portal.domain.model.PortalDocumentStatus;
import net.voytrex.govinda.portal.domain.model.PortalDocumentType;
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
class PortalDocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortalDocumentService portalDocumentService;

    @Mock
    private PortalIdentityService portalIdentityService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    private ObjectMapper objectMapper;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID personId = UUID.randomUUID();
    private final String subject = "portal-subject-789";

    @BeforeEach
    void setUp() {
        var controller = new PortalDocumentController(portalDocumentService, portalIdentityService);
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
    @DisplayName("List Portal Documents")
    class ListPortalDocuments {

        @Test
        @DisplayName("should return list of documents")
        void should_returnDocuments_when_exists() throws Exception {
            var document = new PortalDocument(
                tenantId,
                personId,
                PortalDocumentType.CONTRACT,
                PortalDocumentStatus.AVAILABLE,
                "Policy Contract 2026",
                "storage/contract-2026.pdf"
            );
            var documentId = UUID.randomUUID();
            document.setId(documentId);

            when(portalIdentityService.resolvePersonId(eq(tenantId), eq(subject))).thenReturn(personId);
            when(portalDocumentService.listDocuments(eq(tenantId), eq(personId)))
                .thenReturn(List.of(document));

            mockMvc.perform(
                    get("/api/portal/v1/documents")
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-Portal-Subject", subject)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(documentId.toString()))
                .andExpect(jsonPath("$[0].type").value("CONTRACT"));
        }
    }

    @Nested
    @DisplayName("Get Portal Document")
    class GetPortalDocument {

        @Test
        @DisplayName("should return document when found")
        void should_returnDocument_when_found() throws Exception {
            var documentId = UUID.randomUUID();
            var document = new PortalDocument(
                tenantId,
                personId,
                PortalDocumentType.INVOICE,
                PortalDocumentStatus.AVAILABLE,
                "Invoice 2026-01",
                "storage/invoice-2026-01.pdf"
            );
            document.setId(documentId);

            when(portalIdentityService.resolvePersonId(eq(tenantId), eq(subject))).thenReturn(personId);
            when(portalDocumentService.getDocument(eq(tenantId), eq(personId), eq(documentId)))
                .thenReturn(document);

            mockMvc.perform(
                    get("/api/portal/v1/documents/{id}", documentId)
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-Portal-Subject", subject)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentId.toString()))
                .andExpect(jsonPath("$.title").value("Invoice 2026-01"))
                .andExpect(jsonPath("$.type").value("INVOICE"));
        }
    }
}

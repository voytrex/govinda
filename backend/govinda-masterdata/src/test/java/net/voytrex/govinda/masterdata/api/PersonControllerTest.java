/*
 * Govinda ERP - Person Controller Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.voytrex.govinda.common.api.GlobalExceptionHandler;
import net.voytrex.govinda.common.domain.exception.DuplicateEntityException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.masterdata.application.ChangeNameCommand;
import net.voytrex.govinda.masterdata.application.CreatePersonCommand;
import net.voytrex.govinda.masterdata.application.PersonService;
import net.voytrex.govinda.masterdata.application.UpdatePersonCommand;
import net.voytrex.govinda.masterdata.domain.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;

@ExtendWith(MockitoExtension.class)
// CHECKSTYLE:OFF: MethodName - Test methods follow BDD naming convention (should_X_when_Y)
class PersonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PersonService personService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    private ObjectMapper objectMapper;
    private PersonController personController;
    private GlobalExceptionHandler globalExceptionHandler;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID personId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        personController = new PersonController(personService);
        globalExceptionHandler = new GlobalExceptionHandler(messageSource, localeResolver);
        
        lenient().when(localeResolver.resolveLocale(any())).thenReturn(Locale.ENGLISH);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc = MockMvcBuilders.standaloneSetup(personController)
            .setControllerAdvice(globalExceptionHandler)
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("List Persons")
    class ListPersons {

        @Test
        @DisplayName("should return paginated list of persons")
        void should_returnPaginatedList_when_called() throws Exception {
            Person person = createTestPerson();
            var page = new PageImpl<>(List.of(person), PageRequest.of(0, 20), 1);
            when(personService.listPersons(eq(tenantId), any())).thenReturn(page);

            mockMvc.perform(
                    get("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].lastName").value("Müller"))
                .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("should return empty list when no persons")
        void should_returnEmptyList_when_noPersons() throws Exception {
            var page = new PageImpl<Person>(List.of(), PageRequest.of(0, 20), 0);
            when(personService.listPersons(eq(tenantId), any())).thenReturn(page);

            mockMvc.perform(
                    get("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("Get Person by ID")
    class GetPersonById {

        @Test
        @DisplayName("should return person when found")
        void should_returnPerson_when_found() throws Exception {
            Person person = createTestPerson();
            when(personService.getPerson(personId, tenantId)).thenReturn(person);

            mockMvc.perform(
                    get("/api/v1/masterdata/persons/{id}", personId)
                        .header("X-Tenant-Id", tenantId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Müller"))
                .andExpect(jsonPath("$.firstName").value("Hans"))
                .andExpect(jsonPath("$.fullName").value("Hans Müller"));
        }

        @Test
        @DisplayName("should return 404 when person not found")
        void should_return404_when_notFound() throws Exception {
            when(personService.getPerson(personId, tenantId))
                .thenThrow(new EntityNotFoundException("Person", personId));

            mockMvc.perform(
                    get("/api/v1/masterdata/persons/{id}", personId)
                        .header("X-Tenant-Id", tenantId.toString())
                )
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Create Person")
    class CreatePerson {

        @Test
        @DisplayName("should create person and return 201")
        void should_createPerson_when_validRequest() throws Exception {
            Person person = createTestPerson();
            when(personService.createPerson(any(CreatePersonCommand.class))).thenReturn(person);

            CreatePersonRequest request = new CreatePersonRequest(
                "756.1234.5678.90",
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            mockMvc.perform(
                    post("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.lastName").value("Müller"));
        }

        @Test
        @DisplayName("should return 400 when AHV number is missing")
        void should_return400_when_ahvNumberMissing() throws Exception {
            CreatePersonRequest request = new CreatePersonRequest(
                null,
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            mockMvc.perform(
                    post("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when last name is empty")
        void should_return400_when_lastNameEmpty() throws Exception {
            CreatePersonRequest request = new CreatePersonRequest(
                "756.1234.5678.90",
                "",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            mockMvc.perform(
                    post("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when date of birth is in the future")
        void should_return400_when_futureDateOfBirth() throws Exception {
            CreatePersonRequest request = new CreatePersonRequest(
                "756.1234.5678.90",
                "Müller",
                "Hans",
                LocalDate.now().plusDays(1),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            mockMvc.perform(
                    post("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 409 when AHV already exists")
        void should_return409_when_duplicateAhv() throws Exception {
            when(personService.createPerson(any(CreatePersonCommand.class)))
                .thenThrow(new DuplicateEntityException("Person", "AHV number", "756.1234.5678.90"));

            CreatePersonRequest request = new CreatePersonRequest(
                "756.1234.5678.90",
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            mockMvc.perform(
                    post("/api/v1/masterdata/persons")
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Update Person")
    class UpdatePerson {

        @Test
        @DisplayName("should update person when valid request")
        void should_updatePerson_when_validRequest() throws Exception {
            Person person = createTestPerson();
            person.setNationality("DEU");
            when(personService.updatePerson(any(UpdatePersonCommand.class))).thenReturn(person);

            UpdatePersonRequest request = new UpdatePersonRequest("DEU", Language.FR);

            mockMvc.perform(
                    put("/api/v1/masterdata/persons/{id}", personId)
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nationality").value("DEU"));
        }

        @Test
        @DisplayName("should return 404 when person not found for update")
        void should_return404_when_notFoundForUpdate() throws Exception {
            when(personService.updatePerson(any(UpdatePersonCommand.class)))
                .thenThrow(new EntityNotFoundException("Person", personId));

            UpdatePersonRequest request = new UpdatePersonRequest("DEU", Language.FR);

            mockMvc.perform(
                    put("/api/v1/masterdata/persons/{id}", personId)
                        .header("X-Tenant-Id", tenantId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Change Name")
    class ChangeName {

        @Test
        @DisplayName("should change name when valid request")
        void should_changeName_when_validRequest() throws Exception {
            Person person = createTestPerson("Schmidt", "Hans");
            when(personService.changeName(any(ChangeNameCommand.class))).thenReturn(person);

            ChangeNameRequest request = new ChangeNameRequest(
                "Schmidt",
                "Hans",
                "Marriage",
                LocalDate.of(2024, 9, 1)
            );

            mockMvc.perform(
                    post("/api/v1/masterdata/persons/{id}/name-change", personId)
                        .header("X-Tenant-Id", tenantId.toString())
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Schmidt"));
        }
    }

    @Nested
    @DisplayName("Search Persons")
    class SearchPersons {

        @Test
        @DisplayName("should search persons by last name")
        void should_searchByLastName_when_provided() throws Exception {
            Person person = createTestPerson();
            var page = new PageImpl<>(List.of(person), PageRequest.of(0, 20), 1);
            when(personService.searchPersons(any(), any())).thenReturn(page);

            mockMvc.perform(
                    get("/api/v1/masterdata/persons/search")
                        .header("X-Tenant-Id", tenantId.toString())
                        .param("lastName", "Müller")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].lastName").value("Müller"));
        }
    }

    @Nested
    @DisplayName("Get Person History")
    class GetPersonHistory {

        @Test
        @DisplayName("should return empty history list")
        void should_returnEmptyList_when_noHistory() throws Exception {
            when(personService.getPersonHistory(personId, tenantId)).thenReturn(List.of());

            mockMvc.perform(
                    get("/api/v1/masterdata/persons/{id}/history", personId)
                        .header("X-Tenant-Id", tenantId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    private Person createTestPerson() {
        return createTestPerson("Müller", "Hans");
    }

    private Person createTestPerson(String lastName, String firstName) {
        Person person = new Person(
            tenantId,
            new AhvNumber("756.1234.5678.90"),
            lastName,
            firstName,
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            MaritalStatus.SINGLE,
            "CHE",
            Language.DE
        );
        try {
            var idField = Person.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(person, personId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set person ID", e);
        }
        return person;
    }
}

/*
 * Govinda ERP - Person Service Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.voytrex.govinda.common.domain.exception.DuplicateEntityException;
import net.voytrex.govinda.common.domain.exception.EntityNotFoundException;
import net.voytrex.govinda.common.domain.model.AhvNumber;
import net.voytrex.govinda.common.domain.model.Gender;
import net.voytrex.govinda.common.domain.model.Language;
import net.voytrex.govinda.common.domain.model.MaritalStatus;
import net.voytrex.govinda.common.domain.model.MutationType;
import net.voytrex.govinda.masterdata.domain.model.Person;
import net.voytrex.govinda.masterdata.domain.model.PersonHistoryEntry;
import net.voytrex.govinda.masterdata.domain.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
// CHECKSTYLE:OFF: MethodName - Test methods follow BDD naming convention (should_X_when_Y)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    private PersonService personService;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID personId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final String ahvNr = "756.1234.5678.90";

    @BeforeEach
    void setUp() {
        personService = new PersonService(personRepository);
    }

    @Nested
    @DisplayName("Create Person")
    class CreatePerson {

        @Test
        @DisplayName("should create person with valid data")
        void should_createPerson_when_validData() {
            CreatePersonCommand command = new CreatePersonCommand(
                tenantId,
                ahvNr,
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            when(personRepository.existsByAhvNr(any(AhvNumber.class), eq(tenantId))).thenReturn(false);
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Person result = personService.createPerson(command);

            assertThat(result).isNotNull();
            assertThat(result.getLastName()).isEqualTo("Müller");
            assertThat(result.getFirstName()).isEqualTo("Hans");
            assertThat(result.getAhvNr().getValue()).isEqualTo(ahvNr);
            verify(personRepository).save(any(Person.class));
        }

        @Test
        @DisplayName("should use default nationality when not provided")
        void should_useDefaultNationality_when_notProvided() {
            CreatePersonCommand command = new CreatePersonCommand(
                tenantId,
                ahvNr,
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                null,
                Language.DE
            );

            when(personRepository.existsByAhvNr(any(AhvNumber.class), eq(tenantId))).thenReturn(false);
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Person result = personService.createPerson(command);

            assertThat(result.getNationality()).isEqualTo("CHE");
        }

        @Test
        @DisplayName("should use default language when not provided")
        void should_useDefaultLanguage_when_notProvided() {
            CreatePersonCommand command = new CreatePersonCommand(
                tenantId,
                ahvNr,
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                null
            );

            when(personRepository.existsByAhvNr(any(AhvNumber.class), eq(tenantId))).thenReturn(false);
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Person result = personService.createPerson(command);

            assertThat(result.getPreferredLanguage()).isEqualTo(Language.DE);
        }

        @Test
        @DisplayName("should throw exception when AHV number already exists")
        void should_throwException_when_ahvNumberExists() {
            CreatePersonCommand command = new CreatePersonCommand(
                tenantId,
                ahvNr,
                "Müller",
                "Hans",
                LocalDate.of(1985, 3, 15),
                Gender.MALE,
                MaritalStatus.SINGLE,
                "CHE",
                Language.DE
            );

            when(personRepository.existsByAhvNr(any(AhvNumber.class), eq(tenantId))).thenReturn(true);

            assertThatThrownBy(() -> personService.createPerson(command))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("AHV number");

            verify(personRepository, never()).save(any(Person.class));
        }
    }

    @Nested
    @DisplayName("Get Person")
    class GetPerson {

        @Test
        @DisplayName("should return person when found")
        void should_returnPerson_when_found() {
            Person person = createTestPerson();
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));

            Person result = personService.getPerson(personId, tenantId);

            assertThat(result).isEqualTo(person);
            verify(personRepository).findByIdAndTenantId(personId, tenantId);
        }

        @Test
        @DisplayName("should throw exception when person not found")
        void should_throwException_when_personNotFound() {
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.getPerson(personId, tenantId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Person");
        }
    }

    @Nested
    @DisplayName("Get Person By AHV Number")
    class GetPersonByAhvNr {

        @Test
        @DisplayName("should return person when found by AHV number")
        void should_returnPerson_when_foundByAhvNr() {
            Person person = createTestPerson();
            when(personRepository.findByAhvNr(any(AhvNumber.class), eq(tenantId))).thenReturn(Optional.of(person));

            Person result = personService.getPersonByAhvNr(ahvNr, tenantId);

            assertThat(result).isEqualTo(person);
        }

        @Test
        @DisplayName("should throw exception when person not found by AHV number")
        void should_throwException_when_notFoundByAhvNr() {
            when(personRepository.findByAhvNr(any(AhvNumber.class), eq(tenantId))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.getPersonByAhvNr(ahvNr, tenantId))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("List Persons")
    class ListPersons {

        @Test
        @DisplayName("should return paginated list of persons")
        void should_returnPaginatedList_when_personsExist() {
            Person person = createTestPerson();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Person> expectedPage = new PageImpl<>(List.of(person), pageable, 1);

            when(personRepository.findByTenantId(tenantId, pageable)).thenReturn(expectedPage);

            Page<Person> result = personService.listPersons(tenantId, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return empty page when no persons exist")
        void should_returnEmptyPage_when_noPersonsExist() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Person> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(personRepository.findByTenantId(tenantId, pageable)).thenReturn(emptyPage);

            Page<Person> result = personService.listPersons(tenantId, pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Search Persons")
    class SearchPersons {

        @Test
        @DisplayName("should search persons by query criteria")
        void should_searchPersons_when_queryCriteriaProvided() {
            Person person = createTestPerson();
            PersonSearchQuery query = new PersonSearchQuery(
                tenantId,
                "Müller",
                null,
                null,
                null,
                null
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<Person> expectedPage = new PageImpl<>(List.of(person), pageable, 1);

            when(personRepository.search(
                eq(tenantId),
                eq("Müller"),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(pageable)
            )).thenReturn(expectedPage);

            Page<Person> result = personService.searchPersons(query, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(personRepository).search(tenantId, "Müller", null, null, null, null, pageable);
        }
    }

    @Nested
    @DisplayName("Update Person")
    class UpdatePerson {

        @Test
        @DisplayName("should update nationality")
        void should_updateNationality_when_provided() {
            Person person = createTestPerson();
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UpdatePersonCommand command = new UpdatePersonCommand(
                tenantId,
                personId,
                "DEU",
                null
            );

            Person result = personService.updatePerson(command);

            assertThat(result.getNationality()).isEqualTo("DEU");
            verify(personRepository).save(person);
        }

        @Test
        @DisplayName("should update preferred language")
        void should_updatePreferredLanguage_when_provided() {
            Person person = createTestPerson();
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UpdatePersonCommand command = new UpdatePersonCommand(
                tenantId,
                personId,
                null,
                Language.FR
            );

            Person result = personService.updatePerson(command);

            assertThat(result.getPreferredLanguage()).isEqualTo(Language.FR);
        }

        @Test
        @DisplayName("should throw exception when person not found")
        void should_throwException_when_personNotFoundForUpdate() {
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.empty());

            UpdatePersonCommand command = new UpdatePersonCommand(
                tenantId,
                personId,
                "DEU",
                null
            );

            assertThatThrownBy(() -> personService.updatePerson(command))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Change Name")
    class ChangeName {

        @Test
        @DisplayName("should change name and create history entry")
        void should_changeNameAndCreateHistory_when_validCommand() {
            Person person = createTestPerson();
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ChangeNameCommand command = new ChangeNameCommand(
                tenantId,
                personId,
                "Schmidt",
                "Hans",
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            Person result = personService.changeName(command);

            assertThat(result.getLastName()).isEqualTo("Schmidt");
            verify(personRepository).saveHistory(any(PersonHistoryEntry.class));
            verify(personRepository).save(person);
        }

        @Test
        @DisplayName("should keep original first name when not provided")
        void should_keepOriginalFirstName_when_notProvided() {
            Person person = createTestPerson();
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ChangeNameCommand command = new ChangeNameCommand(
                tenantId,
                personId,
                "Schmidt",
                null,
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            Person result = personService.changeName(command);

            assertThat(result.getLastName()).isEqualTo("Schmidt");
            assertThat(result.getFirstName()).isEqualTo("Hans");
        }

        @Test
        @DisplayName("should throw exception when person not found for name change")
        void should_throwException_when_personNotFoundForNameChange() {
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.empty());

            ChangeNameCommand command = new ChangeNameCommand(
                tenantId,
                personId,
                "Schmidt",
                "Hans",
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            assertThatThrownBy(() -> personService.changeName(command))
                .isInstanceOf(EntityNotFoundException.class);

            verify(personRepository, never()).saveHistory(any(PersonHistoryEntry.class));
        }
    }

    @Nested
    @DisplayName("Change Marital Status")
    class ChangeMaritalStatus {

        @Test
        @DisplayName("should change marital status and create history entry")
        void should_changeMaritalStatusAndCreateHistory_when_validCommand() {
            Person person = createTestPerson();
            person.setMaritalStatus(MaritalStatus.SINGLE);
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ChangeMaritalStatusCommand command = new ChangeMaritalStatusCommand(
                tenantId,
                personId,
                MaritalStatus.MARRIED,
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            Person result = personService.changeMaritalStatus(command);

            assertThat(result.getMaritalStatus()).isEqualTo(MaritalStatus.MARRIED);
            verify(personRepository).saveHistory(any(PersonHistoryEntry.class));
            verify(personRepository).save(person);
        }

        @Test
        @DisplayName("should throw exception when person not found for marital status change")
        void should_throwException_when_personNotFoundForMaritalStatusChange() {
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.empty());

            ChangeMaritalStatusCommand command = new ChangeMaritalStatusCommand(
                tenantId,
                personId,
                MaritalStatus.MARRIED,
                "Marriage",
                LocalDate.of(2024, 9, 1),
                userId
            );

            assertThatThrownBy(() -> personService.changeMaritalStatus(command))
                .isInstanceOf(EntityNotFoundException.class);

            verify(personRepository, never()).saveHistory(any(PersonHistoryEntry.class));
        }
    }

    @Nested
    @DisplayName("Get Person History")
    class GetPersonHistory {

        @Test
        @DisplayName("should return history entries for person")
        void should_returnHistoryEntries_when_personExists() {
            Person person = createTestPerson();
            PersonHistoryEntry historyEntry = createTestHistoryEntry();

            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.findHistoryByPersonId(personId)).thenReturn(List.of(historyEntry));

            List<PersonHistoryEntry> result = personService.getPersonHistory(personId, tenantId);

            assertThat(result).hasSize(1);
            verify(personRepository).findHistoryByPersonId(personId);
        }

        @Test
        @DisplayName("should return empty list when no history exists")
        void should_returnEmptyList_when_noHistoryExists() {
            Person person = createTestPerson();
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.findHistoryByPersonId(personId)).thenReturn(List.of());

            List<PersonHistoryEntry> result = personService.getPersonHistory(personId, tenantId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw exception when person not found for history")
        void should_throwException_when_personNotFoundForHistory() {
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.getPersonHistory(personId, tenantId))
                .isInstanceOf(EntityNotFoundException.class);

            verify(personRepository, never()).findHistoryByPersonId(any());
        }
    }

    @Nested
    @DisplayName("Get Person State At Date")
    class GetPersonStateAtDate {

        @Test
        @DisplayName("should return person state at specific date")
        void should_returnPersonState_when_historyExistsAtDate() {
            Person person = createTestPerson();
            PersonHistoryEntry historyEntry = createTestHistoryEntry();
            LocalDate date = LocalDate.of(2024, 6, 1);

            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.findHistoryAt(personId, date)).thenReturn(Optional.of(historyEntry));

            var result = personService.getPersonStateAt(personId, tenantId, date);

            assertThat(result).isPresent();
            verify(personRepository).findHistoryAt(personId, date);
        }

        @Test
        @DisplayName("should return empty when no history exists at date")
        void should_returnEmpty_when_noHistoryAtDate() {
            Person person = createTestPerson();
            LocalDate date = LocalDate.of(2020, 1, 1);

            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.of(person));
            when(personRepository.findHistoryAt(personId, date)).thenReturn(Optional.empty());

            var result = personService.getPersonStateAt(personId, tenantId, date);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw exception when person not found for state at date")
        void should_throwException_when_personNotFoundForStateAtDate() {
            when(personRepository.findByIdAndTenantId(personId, tenantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> personService.getPersonStateAt(personId, tenantId, LocalDate.now()))
                .isInstanceOf(EntityNotFoundException.class);

            verify(personRepository, never()).findHistoryAt(any(), any());
        }
    }

    private Person createTestPerson() {
        Person person = new Person(
            tenantId,
            new AhvNumber(ahvNr),
            "Müller",
            "Hans",
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            MaritalStatus.SINGLE,
            "CHE",
            Language.DE
        );
        // Set the ID via reflection
        try {
            var idField = Person.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(person, personId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set person ID", e);
        }
        return person;
    }

    private PersonHistoryEntry createTestHistoryEntry() {
        return new PersonHistoryEntry(
            personId,
            "Müller",
            "Hans",
            MaritalStatus.SINGLE,
            LocalDate.of(2020, 1, 1),
            null,
            MutationType.CREATE,
            "Initial creation",
            userId
        );
    }
}

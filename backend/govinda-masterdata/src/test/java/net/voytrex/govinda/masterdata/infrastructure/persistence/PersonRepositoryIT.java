/*
 * Govinda ERP - Person Repository Integration Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.masterdata.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.masterdata.TestApplication;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("resource")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@Tag("integration")
@Tag("database")
// CHECKSTYLE:OFF: MethodName - Test methods follow BDD naming convention (should_X_when_Y)
class PersonRepositoryIT {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("govinda")
        .withUsername("govinda")
        .withPassword("govinda")
        .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (datasourceUrl != null && !datasourceUrl.isEmpty()) {
            registry.add("spring.datasource.url", () -> datasourceUrl);
            registry.add("spring.datasource.username", () ->
                System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", "govinda"));
            registry.add("spring.datasource.password", () ->
                System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", "govinda"));
        } else {
            registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
            registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        }
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private PersonRepository personRepository;

    private final UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID otherTenantId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private Person testPerson;

    @BeforeEach
    void setUp() {
        testPerson = createTestPerson("756.1234.5678.90", "Müller", "Hans");
    }

    @Nested
    @DisplayName("Save and Find Operations")
    class SaveAndFindOperations {

        @Test
        @DisplayName("should save and retrieve person by ID")
        void should_saveAndRetrieve_when_validPerson() {
            Person saved = personRepository.save(testPerson);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getVersion()).isEqualTo(0L);

            Person found = personRepository.findById(saved.getId());

            assertThat(found).isNotNull();
            assertThat(found.getLastName()).isEqualTo("Müller");
            assertThat(found.getFirstName()).isEqualTo("Hans");
            assertThat(found.getAhvNr().getValue()).isEqualTo("756.1234.5678.90");
        }

        @Test
        @DisplayName("should return null when person not found by ID")
        void should_returnNull_when_personNotFoundById() {
            Person found = personRepository.findById(UUID.randomUUID());

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("should find person by ID and tenant ID")
        void should_findPerson_when_idAndTenantIdMatch() {
            Person saved = personRepository.save(testPerson);

            Person found = personRepository.findByIdAndTenantId(saved.getId(), tenantId);

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("should return null when tenant ID does not match")
        void should_returnNull_when_tenantIdDoesNotMatch() {
            Person saved = personRepository.save(testPerson);

            Person found = personRepository.findByIdAndTenantId(saved.getId(), otherTenantId);

            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("Find by AHV Number")
    class FindByAhvNumber {

        @Test
        @DisplayName("should find person by AHV number")
        void should_findPerson_when_ahvNumberExists() {
            personRepository.save(testPerson);
            AhvNumber ahvNr = new AhvNumber("756.1234.5678.90");

            Person found = personRepository.findByAhvNr(ahvNr, tenantId);

            assertThat(found).isNotNull();
            assertThat(found.getLastName()).isEqualTo("Müller");
        }

        @Test
        @DisplayName("should return null when AHV number not found")
        void should_returnNull_when_ahvNumberNotFound() {
            AhvNumber ahvNr = new AhvNumber("756.9999.9999.90");

            Person found = personRepository.findByAhvNr(ahvNr, tenantId);

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("should check existence by AHV number")
        void should_checkExistence_when_ahvNumberProvided() {
            personRepository.save(testPerson);
            AhvNumber existingAhv = new AhvNumber("756.1234.5678.90");
            AhvNumber nonExistingAhv = new AhvNumber("756.9999.9999.90");

            assertThat(personRepository.existsByAhvNr(existingAhv, tenantId)).isTrue();
            assertThat(personRepository.existsByAhvNr(nonExistingAhv, tenantId)).isFalse();
        }
    }

    @Nested
    @DisplayName("Pagination and Listing")
    class PaginationAndListing {

        @Test
        @DisplayName("should return paginated list of persons")
        void should_returnPaginatedList_when_personsExist() {
            personRepository.save(testPerson);
            personRepository.save(createTestPerson("756.2222.2222.22", "Schmidt", "Anna"));
            personRepository.save(createTestPerson("756.3333.3333.33", "Weber", "Peter"));

            Page<Person> page = personRepository.findByTenantId(tenantId, PageRequest.of(0, 2));

            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getTotalElements()).isEqualTo(3);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return empty page when no persons exist")
        void should_returnEmptyPage_when_noPersonsForTenant() {
            Page<Person> page = personRepository.findByTenantId(otherTenantId, PageRequest.of(0, 10));

            assertThat(page.getContent()).isEmpty();
            assertThat(page.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Search Operations")
    class SearchOperations {

        @Test
        @DisplayName("should search by last name")
        void should_findPersons_when_searchingByLastName() {
            personRepository.save(testPerson);
            personRepository.save(createTestPerson("756.2222.2222.22", "Müller-Schmidt", "Anna"));
            personRepository.save(createTestPerson("756.3333.3333.33", "Weber", "Peter"));

            Page<Person> page = personRepository.search(
                tenantId, "Müller", null, null, null, null, PageRequest.of(0, 10)
            );

            assertThat(page.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("should search by first name")
        void should_findPersons_when_searchingByFirstName() {
            personRepository.save(testPerson);
            personRepository.save(createTestPerson("756.2222.2222.22", "Schmidt", "Hans"));

            Page<Person> page = personRepository.search(
                tenantId, null, "Hans", null, null, null, PageRequest.of(0, 10)
            );

            assertThat(page.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("should search by date of birth")
        void should_findPersons_when_searchingByDateOfBirth() {
            personRepository.save(testPerson);

            Page<Person> page = personRepository.search(
                tenantId, null, null, null, LocalDate.of(1985, 3, 15), null, PageRequest.of(0, 10)
            );

            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getContent().getFirst().getLastName()).isEqualTo("Müller");
        }

        @Test
        @DisplayName("should search with multiple criteria")
        void should_findPersons_when_multipleSearchCriteria() {
            personRepository.save(testPerson);
            personRepository.save(createTestPerson("756.2222.2222.22", "Müller", "Anna"));

            Page<Person> page = personRepository.search(
                tenantId, "Müller", "Hans", null, null, null, PageRequest.of(0, 10)
            );

            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Hans");
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("should delete person")
        void should_deletePerson_when_exists() {
            Person saved = personRepository.save(testPerson);
            UUID personId = saved.getId();

            personRepository.delete(saved);

            Person found = personRepository.findById(personId);
            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("History Operations")
    class HistoryOperations {

        @Test
        @DisplayName("should save and retrieve history entry")
        void should_saveAndRetrieveHistory_when_validEntry() {
            Person saved = personRepository.save(testPerson);

            PersonHistoryEntry historyEntry = new PersonHistoryEntry(
                saved.getId(),
                "Müller",
                "Hans",
                MaritalStatus.SINGLE,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2024, 8, 31),
                MutationType.CREATE,
                "Initial creation",
                userId
            );

            PersonHistoryEntry savedHistory = personRepository.saveHistory(historyEntry);

            assertThat(savedHistory).isNotNull();
            assertThat(savedHistory.getHistoryId()).isNotNull();
        }

        @Test
        @DisplayName("should find history entries by person ID")
        void should_findHistoryEntries_when_personHasHistory() {
            Person saved = personRepository.save(testPerson);

            PersonHistoryEntry entry1 = new PersonHistoryEntry(
                saved.getId(),
                "Müller",
                "Hans",
                MaritalStatus.SINGLE,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2024, 8, 31),
                MutationType.CREATE,
                "Initial creation",
                userId
            );

            PersonHistoryEntry entry2 = new PersonHistoryEntry(
                saved.getId(),
                "Schmidt-Müller",
                "Hans",
                MaritalStatus.MARRIED,
                LocalDate.of(2024, 9, 1),
                null,
                MutationType.UPDATE,
                "Marriage",
                userId
            );

            personRepository.saveHistory(entry1);
            personRepository.saveHistory(entry2);

            List<PersonHistoryEntry> history = personRepository.findHistoryByPersonId(saved.getId());

            assertThat(history).hasSize(2);
        }

        @Test
        @DisplayName("should find history at specific date")
        void should_findHistoryAtDate_when_historyExists() {
            Person saved = personRepository.save(testPerson);

            PersonHistoryEntry entry = new PersonHistoryEntry(
                saved.getId(),
                "Müller",
                "Hans",
                MaritalStatus.SINGLE,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2024, 8, 31),
                MutationType.CREATE,
                "Initial creation",
                userId
            );

            personRepository.saveHistory(entry);

            PersonHistoryEntry found = personRepository.findHistoryAt(saved.getId(), LocalDate.of(2022, 6, 15));

            assertThat(found).isNotNull();
            assertThat(found.getLastName()).isEqualTo("Müller");
        }

        @Test
        @DisplayName("should return null when no history at date")
        void should_returnNull_when_noHistoryAtDate() {
            Person saved = personRepository.save(testPerson);

            PersonHistoryEntry found = personRepository.findHistoryAt(saved.getId(), LocalDate.of(2022, 6, 15));

            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("Tenant Isolation")
    class TenantIsolation {

        @Test
        @DisplayName("should isolate persons by tenant")
        void should_isolatePersons_when_differentTenants() {
            personRepository.save(testPerson);

            Page<Person> tenant1Page = personRepository.findByTenantId(tenantId, PageRequest.of(0, 10));
            Page<Person> tenant2Page = personRepository.findByTenantId(otherTenantId, PageRequest.of(0, 10));

            assertThat(tenant1Page.getTotalElements()).isEqualTo(1);
            assertThat(tenant2Page.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("should not find person by AHV in different tenant")
        void should_notFindPerson_when_ahvInDifferentTenant() {
            personRepository.save(testPerson);
            AhvNumber ahvNr = new AhvNumber("756.1234.5678.90");

            Person found = personRepository.findByAhvNr(ahvNr, otherTenantId);

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("should allow same AHV in different tenants")
        void should_allowSameAhv_when_differentTenants() {
            personRepository.save(testPerson);
            AhvNumber ahvNr = new AhvNumber("756.1234.5678.90");

            assertThat(personRepository.existsByAhvNr(ahvNr, tenantId)).isTrue();
            assertThat(personRepository.existsByAhvNr(ahvNr, otherTenantId)).isFalse();
        }
    }

    private Person createTestPerson(String ahvNr, String lastName, String firstName) {
        return new Person(
            tenantId,
            new AhvNumber(ahvNr),
            lastName,
            firstName,
            LocalDate.of(1985, 3, 15),
            Gender.MALE,
            MaritalStatus.SINGLE,
            "CHE",
            Language.DE
        );
    }
}

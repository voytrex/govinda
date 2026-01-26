/*
 * Govinda ERP - JPA User Repository Integration Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import net.voytrex.govinda.TestApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for JpaUserRepository.
 */
@SuppressWarnings("resource")
@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Container
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("govinda")
        .withUsername("govinda")
        .withPassword("govinda")
        .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Nested
    @DisplayName("Save and Find")
    class SaveAndFind {

        @Test
        void shouldSaveAndFindUserById() {
            UUID userId = UUID.randomUUID();
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setId(userId);
            user.setStatus(UserStatus.ACTIVE);

            jpaUserRepository.save(user);
            var found = jpaUserRepository.findById(userId);

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("testuser");
            assertThat(found.get().getEmail()).isEqualTo("test@example.com");
            assertThat(found.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void shouldFindUserByUsername() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            jpaUserRepository.save(user);

            User found = jpaUserRepository.findByUsername("testuser");

            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("testuser");
        }

        @Test
        void shouldFindUserByEmail() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            jpaUserRepository.save(user);

            User found = jpaUserRepository.findByEmail("test@example.com");

            assertThat(found).isNotNull();
            assertThat(found.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        void shouldReturnNullWhenUserNotFoundByUsername() {
            User found = jpaUserRepository.findByUsername("nonexistent");

            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("User Status")
    class UserStatusTests {

        @Test
        void shouldSaveUserWithDifferentStatuses() {
            User activeUser = new User("active", "active@example.com", "hash");
            activeUser.setStatus(UserStatus.ACTIVE);
            User inactiveUser = new User("inactive", "inactive@example.com", "hash");
            inactiveUser.setStatus(UserStatus.INACTIVE);
            User lockedUser = new User("locked", "locked@example.com", "hash");
            lockedUser.setStatus(UserStatus.LOCKED);

            jpaUserRepository.save(activeUser);
            jpaUserRepository.save(inactiveUser);
            jpaUserRepository.save(lockedUser);

            assertThat(jpaUserRepository.findByUsername("active").getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(jpaUserRepository.findByUsername("inactive").getStatus()).isEqualTo(UserStatus.INACTIVE);
            assertThat(jpaUserRepository.findByUsername("locked").getStatus()).isEqualTo(UserStatus.LOCKED);
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUser {

        @Test
        void shouldUpdateExistingUser() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            User saved = jpaUserRepository.save(user);

            saved.setFirstName("Updated");
            saved.setLastName("Name");
            User updated = jpaUserRepository.save(saved);

            assertThat(updated.getFirstName()).isEqualTo("Updated");
            assertThat(updated.getLastName()).isEqualTo("Name");
        }
    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUser {

        @Test
        void shouldDeleteUser() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            User saved = jpaUserRepository.save(user);
            UUID userId = saved.getId();

            jpaUserRepository.delete(saved);

            assertThat(jpaUserRepository.findById(userId)).isEmpty();
        }
    }
}

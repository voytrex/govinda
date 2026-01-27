/*
 * Govinda ERP - User Domain Model Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@Tag("fast")
class UserTest {

    private final UUID userId = UUID.randomUUID();

    @Nested
    @DisplayName("User Creation")
    class UserCreation {

        @Test
        void shouldCreateUserWithValidData() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setId(userId);
            user.setFirstName("Test");
            user.setLastName("User");
            user.setStatus(UserStatus.ACTIVE);

            assertThat(user.getId()).isEqualTo(userId);
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.isActive()).isTrue();
        }

        @Test
        void shouldCreateUserWithoutFirstAndLastName() {
            User user = new User("testuser", "test@example.com", "hashed_password");

            assertThat(user.getFirstName()).isNull();
            assertThat(user.getLastName()).isNull();
            assertThat(user.fullName()).isEqualTo("testuser");
        }

        @Test
        void shouldDefaultToActiveStatus() {
            User user = new User("testuser", "test@example.com", "hashed_password");

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Full Name")
    class FullName {

        @Test
        void shouldReturnFullNameWhenFirstAndLastNameProvided() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setFirstName("John");
            user.setLastName("Doe");

            assertThat(user.fullName()).isEqualTo("John Doe");
        }

        @Test
        void shouldReturnUsernameWhenNameNotProvided() {
            User user = new User("testuser", "test@example.com", "hashed_password");

            assertThat(user.fullName()).isEqualTo("testuser");
        }

        @Test
        void shouldReturnFirstNameOnlyWhenLastNameMissing() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setFirstName("John");

            assertThat(user.fullName()).isEqualTo("John");
        }
    }

    @Nested
    @DisplayName("User Status")
    class UserStatusChecks {

        @Test
        void shouldReturnTrueForActiveUser() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setStatus(UserStatus.ACTIVE);

            assertThat(user.isActive()).isTrue();
        }

        @Test
        void shouldReturnFalseForInactiveUser() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setStatus(UserStatus.INACTIVE);

            assertThat(user.isActive()).isFalse();
        }

        @Test
        void shouldReturnFalseForLockedUser() {
            User user = new User("testuser", "test@example.com", "hashed_password");
            user.setStatus(UserStatus.LOCKED);

            assertThat(user.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Last Login")
    class LastLogin {

        @Test
        void shouldUpdateLastLoginTimestamp() {
            User user = new User("testuser", "test@example.com", "hashed_password");

            Instant beforeUpdate = Instant.now();
            user.updateLastLogin();
            Instant afterUpdate = Instant.now();

            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getLastLoginAt()).isAfterOrEqualTo(beforeUpdate);
            assertThat(user.getLastLoginAt()).isBeforeOrEqualTo(afterUpdate);
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        }
    }

    @Nested
    @DisplayName("Mutators")
    class Mutators {

        @Test
        void shouldUpdateProfileFields() {
            User user = new User("initial", "initial@example.com", "hash");
            Instant createdAt = Instant.now().minusSeconds(60);
            Instant updatedAt = Instant.now();
            Instant lastLoginAt = Instant.now().minusSeconds(5);

            user.setUsername("updated");
            user.setEmail("updated@example.com");
            user.setPasswordHash("newhash");
            user.setCreatedAt(createdAt);
            user.setUpdatedAt(updatedAt);
            user.setLastLoginAt(lastLoginAt);

            assertThat(user.getUsername()).isEqualTo("updated");
            assertThat(user.getEmail()).isEqualTo("updated@example.com");
            assertThat(user.getPasswordHash()).isEqualTo("newhash");
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
        }

        @Test
        void shouldAllowReplacingTenantAccessList() {
            User user = new User("testuser", "test@example.com", "hashed_password");

            user.setTenantAccess(List.of());

            assertThat(user.getTenantAccess()).isEmpty();
        }
    }
}

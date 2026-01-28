/*
 * Govinda ERP - JPA UserTenant Repository Integration Tests
 * Copyright 2026 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import net.voytrex.govinda.TestApplication;
import net.voytrex.govinda.common.domain.model.Role;
import net.voytrex.govinda.common.domain.model.Tenant;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.model.UserStatus;
import net.voytrex.govinda.common.domain.model.UserTenant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("resource")
@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
@org.junit.jupiter.api.Tag("integration")
@org.junit.jupiter.api.Tag("database")
class JpaUserTenantRepositoryTest {

    @Autowired
    private JpaUserTenantRepository userTenantRepository;

    @Autowired
    private EntityManager entityManager;

    @Container
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("govinda")
        .withUsername("govinda")
        .withPassword("govinda")
        .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Nested
    @DisplayName("Lookup Methods")
    class LookupMethods {

        @Test
        void shouldFindByUserId() {
            User user = persistUser();
            Tenant tenant = persistTenant();
            Role role = persistRole();
            UserTenant userTenant = persistUserTenant(user, tenant, role, true);
            entityManager.flush();
            entityManager.clear();

            List<UserTenant> result = userTenantRepository.findByUserId(user.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(userTenant.getId());
        }

        @Test
        void shouldFindByUserIdAndTenantId() {
            User user = persistUser();
            Tenant tenant = persistTenant();
            Role role = persistRole();
            UserTenant userTenant = persistUserTenant(user, tenant, role, true);
            entityManager.flush();
            entityManager.clear();

            var result = userTenantRepository.findByUserIdAndTenantId(user.getId(), tenant.getId());

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().getId()).isEqualTo(userTenant.getId());
        }

        @Test
        void shouldFindByUserIdAndIsDefaultTrue() {
            User user = persistUser();
            Tenant tenant = persistTenant();
            Role role = persistRole();
            UserTenant defaultTenant = persistUserTenant(user, tenant, role, true);
            entityManager.flush();
            entityManager.clear();

            var result = userTenantRepository.findByUserIdAndDefaultAccessTrue(user.getId());

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().getId()).isEqualTo(defaultTenant.getId());
        }

        @Test
        void shouldFindUserTenantAccess() {
            User user = persistUser();
            Tenant tenant = persistTenant();
            Role role = persistRole();
            UserTenant userTenant = persistUserTenant(user, tenant, role, false);
            entityManager.flush();
            entityManager.clear();

            var result = userTenantRepository.findUserTenantAccess(user.getId(), tenant.getId());

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().getId()).isEqualTo(userTenant.getId());
        }
    }

    private User persistUser() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        User user = new User("user_" + suffix, "user_" + suffix + "@example.com", "hash");
        user.setStatus(UserStatus.ACTIVE);
        entityManager.persist(user);
        return user;
    }

    private Tenant persistTenant() {
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        Tenant tenant = new Tenant(UUID.randomUUID(), "T" + suffix, "Tenant " + suffix);
        entityManager.persist(tenant);
        return tenant;
    }

    private Role persistRole() {
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        Role role = new Role("ROLE_" + suffix, "Role " + suffix);
        entityManager.persist(role);
        return role;
    }

    private UserTenant persistUserTenant(User user, Tenant tenant, Role role, boolean isDefault) {
        UserTenant userTenant = new UserTenant(user, tenant, role);
        userTenant.setDefault(isDefault);
        entityManager.persist(userTenant);
        return userTenant;
    }
}

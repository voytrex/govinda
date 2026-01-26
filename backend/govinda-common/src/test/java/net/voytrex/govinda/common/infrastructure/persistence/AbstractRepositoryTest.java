/*
 * Govinda ERP - Base Test Configuration for Repository Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 *
 * Base class for integration tests using Testcontainers.
 */

package net.voytrex.govinda.common.infrastructure.persistence;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for repository integration tests.
 * 
 * Uses Testcontainers when Docker is available (local development),
 * or falls back to environment variables when running in CI.
 */
@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class AbstractRepositoryTest {

    @SuppressWarnings("resource")
    @Container
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("govinda")
        .withUsername("govinda")
        .withPassword("govinda")
        .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Check if we're in CI (GitHub Actions provides SPRING_DATASOURCE_URL)
        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (datasourceUrl != null && !datasourceUrl.isEmpty()) {
            // Use CI-provided PostgreSQL service
            registry.add("spring.datasource.url", () -> datasourceUrl);
            registry.add("spring.datasource.username", () -> 
                System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", "govinda"));
            registry.add("spring.datasource.password", () -> 
                System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", "govinda"));
            // For @DataJpaTest, enable Hibernate DDL since Flyway doesn't run
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
            // Enable global quoting for DDL to handle reserved words like "user"
            registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "true");
        } else {
            // Use Testcontainers
            registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
            registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
            // For @DataJpaTest, enable Hibernate DDL since Flyway doesn't run
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
            // Enable global quoting for DDL to handle reserved words like "user"
            registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "true");
        }
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }
}

/*
 * Govinda ERP - Authentication Controller Integration Tests
 * Copyright 2024 Voytrex
 * SPDX-License-Identifier: Apache-2.0
 */

package net.voytrex.govinda.common.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import net.voytrex.govinda.common.domain.model.Role;
import net.voytrex.govinda.common.domain.model.Tenant;
import net.voytrex.govinda.common.domain.model.User;
import net.voytrex.govinda.common.domain.model.UserStatus;
import net.voytrex.govinda.common.domain.model.UserTenant;
import net.voytrex.govinda.common.domain.repository.UserRepository;
import net.voytrex.govinda.common.infrastructure.persistence.JpaUserTenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import net.voytrex.govinda.TestApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("resource")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@org.junit.jupiter.api.Tag("integration")
@org.junit.jupiter.api.Tag("api")
@org.junit.jupiter.api.Tag("database")
class AuthControllerIntegrationTest {

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
            // Ensure Flyway runs for @SpringBootTest
            registry.add("spring.flyway.enabled", () -> "true");
            registry.add("spring.flyway.locations", () -> "classpath:db/migration");
            registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        } else {
            // Use Testcontainers
            registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
            registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
            // Ensure Flyway runs for @SpringBootTest
            registry.add("spring.flyway.enabled", () -> "true");
            registry.add("spring.flyway.locations", () -> "classpath:db/migration");
            registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        }
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaUserTenantRepository userTenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID tenantId;
    private UUID roleId;
    private final String username = "testuser";
    private final String password = "password123";

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        roleId = UUID.fromString("00000000-0000-0000-0000-000000000011");

        Role role = entityManager.find(Role.class, roleId);
        Tenant tenant = entityManager.find(Tenant.class, tenantId);

        User user = new User(username, "test@example.com", passwordEncoder.encode(password));
        user.setId(userId);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserTenant userTenant = new UserTenant(user, tenant, role);
        userTenant.setDefault(true);
        userTenantRepository.save(userTenant);
    }

    @Nested
    @DisplayName("Login Endpoint")
    class LoginEndpoint {

        @Test
        void shouldReturnJwtTokenOnSuccessfulLogin() throws Exception {
            LoginRequest request = new LoginRequest(username, password, null);

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void shouldReturn400WhenUsernameIsMissing() throws Exception {
            var request = java.util.Map.of("password", password);

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenPasswordIsMissing() throws Exception {
            var request = java.util.Map.of("username", username);

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
            LoginRequest request = new LoginRequest(username, "wrongpassword", null);

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_ERROR"));
        }

        @Test
        void shouldReturn401WhenUserDoesNotExist() throws Exception {
            LoginRequest request = new LoginRequest("nonexistent", password, null);

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldAcceptOptionalTenantIdInLoginRequest() throws Exception {
            LoginRequest request = new LoginRequest(username, password, tenantId.toString());

            mockMvc.perform(
                    post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
        }
    }

    @Nested
    @DisplayName("Get User Tenants Endpoint")
    class GetUserTenantsEndpoint {

        @Test
        void shouldReturnUserTenantsWhenAuthenticated() throws Exception {
            String token = authenticateAndGetToken();

            mockMvc.perform(
                    get("/api/v1/auth/tenants")
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tenantId").exists())
                .andExpect(jsonPath("$[0].roleCode").exists());
        }

        @Test
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/v1/auth/tenants"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Get Current User Endpoint")
    class GetCurrentUserEndpoint {

        @Test
        void shouldReturnCurrentUserInfoWhenAuthenticated() throws Exception {
            String token = authenticateAndGetToken();

            mockMvc.perform(
                    get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.authorities").isArray());
        }

        @Test
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
        }
    }

    private String authenticateAndGetToken() throws Exception {
        LoginRequest request = new LoginRequest(username, password, null);

        var response = mockMvc.perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andReturn();

        LoginResponse responseBody = objectMapper.readValue(
            response.getResponse().getContentAsString(),
            LoginResponse.class
        );

        return responseBody.token();
    }
}

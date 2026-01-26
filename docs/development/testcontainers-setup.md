# Testcontainers Setup Guide

## Quick Answer

**Yes, you should use Testcontainers** for integration tests in your Java Spring Boot monorepo. You already have it configured!

## What I've Set Up

1. ✅ Using Testcontainers version 1.19.x (aligned with BOM)
2. ✅ Created `AbstractRepositoryTest` base class for repository tests
3. ✅ Created example `JpaUserRepositoryTest` showing best practices
4. ✅ Created testing strategy documentation

## Two Approaches for Testcontainers

### Approach 1: JDBC URL (Current in application.yml)
```yaml
spring:
  datasource:
    url: jdbc:tc:postgresql:18:///govinda
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
```

**Pros:**
- Simple, no code needed
- Works automatically with Spring Boot

**Cons:**
- Less control over container configuration
- Harder to reuse containers
- Limited customization

### Approach 2: @DynamicPropertySource (Recommended)
```java
@Container
static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:18-alpine")
    .withReuse(true);

@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    // ...
}
```

**Pros:**
- Full control over container configuration
- Container reuse for better performance
- Can configure multiple containers (PostgreSQL, Redis, etc.)
- Better for monorepo with multiple modules

**Cons:**
- Requires test setup code

## Best Practices for 2025/2026

### 1. Container Reuse
```java
new PostgreSQLContainer<>("postgres:18-alpine")
    .withReuse(true); // Reuses container across test runs
```
- Speeds up tests significantly
- Requires `testcontainers.reuse.enable=true` in `~/.testcontainers.properties`

### 2. Test Organization
```
govinda-common/
  src/test/java/
    domain/model/          # Unit tests (mocks)
    infrastructure/        # Integration tests (Testcontainers)
      persistence/
        JpaUserRepositoryTest.java
```

### 3. Test Isolation
- Each test should clean up its data
- Use `@BeforeEach` to delete all data
- Or use `@Transactional` (rolls back after test)

### 4. Performance Tips
- Run unit tests first (fast)
- Use `@DataJpaTest` for repository tests (slimmer Spring context)
- Use `@SpringBootTest` only for full integration tests
- Consider parallel test execution carefully

### 5. CI/CD Strategy

**Option A: Testcontainers in CI (Recommended)**
```yaml
# .github/workflows/ci.yml
- name: Run tests
  run: mvn test
  # Testcontainers will start containers automatically
```

**Option B: Keep Service-based (Current)**
- Your current CI setup works fine
- Consider migrating to Testcontainers for consistency

## When to Use What

| Test Type | Tool | Example |
|-----------|------|---------|
| Domain logic | Mockito | `UserTest`, `MoneyTest` |
| Value objects | JUnit 5 | `AhvNumberTest`, `FranchiseTest` |
| Services | Mockito | `AuthenticationServiceTest` |
| Repositories | Testcontainers | `JpaUserRepositoryTest` |
| Controllers | Testcontainers + MockMvc | `PersonControllerTest` |

## @DataJpaTest vs @SpringBootTest

### @DataJpaTest (Faster, Slimmer)
- ✅ Fast startup (minimal Spring context)
- ✅ Good for simple JPA mapping tests
- ❌ Doesn't run Flyway migrations
- ❌ Uses Hibernate DDL (create-drop)
- **Use for**: Simple CRUD, query method tests

### @SpringBootTest (Slower, Full Context)
- ✅ Runs Flyway migrations
- ✅ Full Spring context
- ✅ Tests actual schema constraints
- ❌ Slower startup
- **Use for**: Tests requiring Flyway schema, full integration tests

**Recommendation**: Start with `@DataJpaTest` for simple tests, use `@SpringBootTest` when you need the actual Flyway schema.

## Next Steps

1. **Run the example test:**
   ```bash
   mvn -pl backend/govinda-common -am -Dtest=JpaUserRepositoryTest test
   ```

2. **Create more repository tests:**
   - `JpaPersonRepositoryTest` (use `@SpringBootTest` if you need Flyway schema)
   - `JpaHouseholdRepositoryTest`
   - `JpaUserTenantRepositoryTest`

3. **Create controller tests:**
   - Use `@SpringBootTest` with Testcontainers
   - Use `MockMvc` or `WebTestClient`

4. **Optional: Update CI**
   - Migrate from service-based to Testcontainers
   - Ensures consistency between local and CI

## Troubleshooting

### Container doesn't start
- Ensure Docker is running
- Check Docker daemon is accessible
- Verify Testcontainers version compatibility

### Tests are slow
- Enable container reuse: `testcontainers.reuse.enable=true`
- Use `@DataJpaTest` instead of `@SpringBootTest` when possible
- Run tests in parallel (with caution)

### Flyway migrations fail
- Ensure test profile includes Flyway configuration
- Check migration files are in classpath
- Verify database schema matches expectations

## Resources

- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers with Spring Boot](https://www.testcontainers.org/modules/databases/jdbc/)

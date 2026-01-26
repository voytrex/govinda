# Testing Strategy for Govinda ERP

## Overview

This document outlines the testing strategy for Govinda ERP, following DDD (Domain-Driven Design) and TDD (Test-Driven Development) principles in a Java Spring Boot monorepo.

## Test Pyramid

```
        /\
       /  \      E2E Tests (Optional - Manual/Contract Tests)
      /____\
     /      \    Integration Tests (Testcontainers)
    /________\
  /          \  Unit Tests (Mockito)
  /____________\
```

### 1. Unit Tests (70-80% of tests)
- **Purpose**: Test domain logic, value objects, business rules in isolation
- **Tools**: Mockito, AssertJ, JUnit 5
- **No dependencies**: Pure Java, no Spring, no database
- **Example**: `AuthenticationServiceTest`, `UserTest`, `MoneyTest`

### 2. Integration Tests (15-25% of tests)
- **Purpose**: Test repository layer, JPA mappings, Flyway migrations, transactions
- **Tools**: Testcontainers, Spring Boot Test, @DataJpaTest
- **Real database**: PostgreSQL via Testcontainers
- **Example**: `JpaUserRepositoryTest`, `PersonRepositoryTest`

### 3. Contract/API Tests (5-10% of tests)
- **Purpose**: Test REST endpoints with real database
- **Tools**: Testcontainers, @SpringBootTest, MockMvc/WebTestClient
- **Full stack**: Spring context, database, security
- **Example**: `PersonControllerTest`, `AuthControllerTest`

## Testcontainers Best Practices (2025/2026)

### When to Use Testcontainers

✅ **Use Testcontainers for:**
- Repository/DAO tests
- JPA entity mapping tests
- Flyway migration tests
- Transaction boundary tests
- Multi-tenant data isolation tests
- Complex queries with joins/aggregations

❌ **Don't use Testcontainers for:**
- Pure domain logic (use mocks)
- Value object validation
- Business rule calculations
- Service layer with mocked repositories

### Performance Optimization

1. **Container Reuse**: Use `@Testcontainers` with `@Container` static fields
2. **Parallel Execution**: Configure JUnit parallel execution carefully
3. **Test Isolation**: Each test should clean up its data (use `@Transactional` or manual cleanup)
4. **Fast Tests First**: Run unit tests before integration tests

### Configuration

- **Version**: Use Testcontainers 1.19.x (aligned with BOM)
- **PostgreSQL**: Match production version (PostgreSQL 18)
- **Flyway**: Run migrations in tests to validate schema
- **Profiles**: Use `@ActiveProfiles("test")` for test-specific config

## Monorepo Testing Strategy

### Module-Level Testing

Each bounded context module should have:
- `domain/model/*Test.java` - Unit tests for domain models
- `domain/repository/*Test.java` - Integration tests for repositories
- `api/*ControllerTest.java` - Contract tests for REST endpoints
- `application/*ServiceTest.java` - Unit tests with mocked repositories

### Shared Test Infrastructure

- Base test classes in `govinda-common` for common setup
- Test fixtures/factories for domain objects
- Test utilities for database setup/teardown

## CI/CD Considerations

### Option 1: Testcontainers in CI (Recommended)
- **Pros**: Same environment as local, no service dependencies
- **Cons**: Requires Docker in CI, slightly slower
- **Best for**: Consistency, parallel test execution

### Option 2: Service-based PostgreSQL (Current)
- **Pros**: Faster startup, no Docker requirement
- **Cons**: Different from local environment, harder to parallelize
- **Best for**: Simple CI setups, faster feedback

**Recommendation**: Use Testcontainers in CI for consistency with local development.

## Example Test Structure

```
govinda-common/
  src/test/java/
    domain/
      model/
        UserTest.java                   # Unit test (mocks)
      repository/
        UserRepositoryTest.java         # Integration test (Testcontainers)
    security/
      AuthenticationServiceTest.java   # Unit test (mocks)
      JwtTokenServiceTest.java          # Unit test (mocks)
    api/
      AuthControllerTest.java           # Contract test (Testcontainers + MockMvc)
```

## Migration Path

1. ✅ Already have Testcontainers dependencies
2. ✅ Test profile configured
3. ⏳ Create base test configuration class
4. ⏳ Write integration tests for repositories
5. ⏳ Update CI to use Testcontainers (optional)
6. ⏳ Add test fixtures/factories

## Resources

- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

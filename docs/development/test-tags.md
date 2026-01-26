# Test Tags and Categorization

## Overview

JUnit 5 tags allow us to categorize tests and run them selectively. This is essential for:
- **Fast feedback**: Run unit tests quickly during development
- **CI optimization**: Run different test types in parallel or sequentially
- **Selective execution**: Run only relevant tests for specific changes

## Standard Test Tags

### Primary Tags (Required)

| Tag | Purpose | Example |
|-----|---------|---------|
| `@Tag("unit")` | Pure unit tests, no Spring, no database | Domain model tests, service tests with mocks |
| `@Tag("integration")` | Integration tests with real dependencies | Repository tests, API tests with Testcontainers |

### Secondary Tags (Optional)

| Tag | Purpose | Example |
|-----|---------|---------|
| `@Tag("fast")` | Tests that run in < 100ms | Simple value object tests |
| `@Tag("slow")` | Tests that take > 1 second | Complex integration tests |
| `@Tag("database")` | Tests requiring database | Repository tests, integration tests |
| `@Tag("api")` | REST API endpoint tests | Controller tests with MockMvc |
| `@Tag("security")` | Security-related tests | Authentication, authorization tests |

## Usage Examples

### Unit Test (Domain Model)

```java
@Tag("unit")
@Tag("fast")
class UserTest {
    
    @Test
    void should_calculateFullName_when_firstAndLastNameProvided() {
        // Pure domain logic, no Spring, no database
    }
}
```

### Integration Test (Repository)

```java
@Tag("integration")
@Tag("database")
@Testcontainers
@DataJpaTest
class JpaUserRepositoryTest {
    
    @Test
    void should_saveAndFindUser_when_validUserProvided() {
        // Uses Testcontainers, real database
    }
}
```

### Integration Test (API)

```java
@Tag("integration")
@Tag("api")
@Tag("database")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    
    @Test
    void should_returnJwtToken_when_validCredentials() {
        // Full Spring context, database, MockMvc
    }
}
```

## Maven Configuration

### Running Tests by Tag

```bash
# Run only unit tests
mvn test -Dgroups=unit

# Run only integration tests
mvn test -Dgroups=integration

# Run fast tests only
mvn test -Dgroups=fast

# Exclude slow tests
mvn test -DexcludedGroups=slow

# Run unit and fast tests
mvn test -Dgroups="unit,fast"
```

### Surefire Configuration (Unit Tests)

Unit tests are automatically run by Surefire (files matching `*Test.java` but not `*IT.java` or `*IntegrationTest.java`).

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
        </includes>
        <excludes>
            <exclude>**/*IT.java</exclude>
            <exclude>**/*IntegrationTest.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

### Failsafe Configuration (Integration Tests)

Integration tests are run by Failsafe (files matching `*IT.java` or `*IntegrationTest.java`).

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
            <include>**/*IntegrationTest.java</include>
        </includes>
    </configuration>
</plugin>
```

## Naming Conventions

### Test File Names

| Test Type | Naming Pattern | Example |
|-----------|---------------|---------|
| Unit Test | `{Class}Test.java` | `UserTest.java`, `MoneyTest.java` |
| Integration Test | `{Class}IT.java` or `{Class}IntegrationTest.java` | `JpaUserRepositoryIT.java`, `AuthControllerIntegrationTest.java` |

### Class-Level Tags

Always add tags at the class level:

```java
@Tag("unit")
@Tag("fast")
class UserTest {  // ✅ Good
}

class UserTest {  // ❌ Missing tags
    @Tag("unit")  // ❌ Tag at method level (less efficient)
    void test() {}
}
```

## CI/CD Integration

### GitHub Actions

The CI workflow runs tests in separate phases:

1. **Unit Tests** (fast feedback)
   ```yaml
   - name: Run unit tests
     run: mvn -B surefire:test
   ```

2. **Integration Tests** (after unit tests pass)
   ```yaml
   - name: Run integration tests
     run: mvn -B failsafe:integration-test
   ```

### Selective Test Execution

For faster feedback during development:

```bash
# Local development - run only fast unit tests
./mvnw test -Dgroups=fast

# Before commit - run all unit tests
./mvnw test

# Before push - run all tests
./mvnw verify
```

## Best Practices

### ✅ Do

- **Always tag integration tests** with `@Tag("integration")`
- **Tag slow tests** with `@Tag("slow")` to allow exclusion
- **Use class-level tags** for consistency
- **Combine tags** when appropriate (e.g., `@Tag("integration")` + `@Tag("database")`)

### ❌ Don't

- **Don't tag every test method individually** - use class-level tags
- **Don't create too many custom tags** - stick to standard tags
- **Don't mix unit and integration tests** in the same class

## Migration Guide

### Existing Tests

1. **Add `@Tag("unit")`** to all pure unit tests (domain models, services with mocks)
2. **Add `@Tag("integration")`** to all tests using Testcontainers or Spring context
3. **Rename integration test files** to end with `IT.java` or `IntegrationTest.java`
4. **Add `@Tag("fast")`** to tests that run in < 100ms
5. **Add `@Tag("slow")`** to tests that take > 1 second

### Example Migration

**Before:**
```java
class UserTest {
    @Test
    void testFullName() { }
}
```

**After:**
```java
@Tag("unit")
@Tag("fast")
class UserTest {
    @Test
    void should_returnFullName_when_firstAndLastNameProvided() { }
}
```

## Resources

- [JUnit 5 Tags Documentation](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)

# Govinda ERP - Claude Code Development Guide

## Project Overview

| Aspect | Details |
|--------|---------|
| **Type** | Modular monolith ERP for Swiss health insurance |
| **Stack** | Java 21, Spring Boot 3.2+, PostgreSQL 18+, Maven |
| **Architecture** | Domain-Driven Design (DDD) |
| **Languages** | DE (primary), FR, IT, EN |
| **Testing** | JUnit 5, Mockito, Testcontainers, AssertJ |

---

## TDD Workflow - MANDATORY

**Every code change MUST follow Red-Green-Refactor. This is non-negotiable.**

### The TDD Cycle

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  1. RED        Write a failing test FIRST                       │
│                ├─ Test describes the desired behavior            │
│                ├─ Run test: ./mvnw test -Dtest={Test}#{method}  │
│                └─ Verify it FAILS (proves test validity)         │
│                                                                  │
│  2. GREEN      Write MINIMAL code to pass                       │
│                ├─ Only enough code to satisfy the test           │
│                ├─ No extra features, no optimization             │
│                └─ Run test: verify it PASSES                     │
│                                                                  │
│  3. REFACTOR   Improve code quality                             │
│                ├─ Clean up, remove duplication                   │
│                ├─ Improve naming and structure                   │
│                └─ Run ALL tests: verify they PASS                │
│                                                                  │
│  REPEAT for each new requirement                                │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Phase 1: RED - Write Failing Test First

**Before writing ANY production code:**

1. Create the test file in the correct location:
   ```
   src/main/java/.../domain/model/Premium.java
   → src/test/java/.../domain/model/PremiumTest.java
   ```

2. Write a test that describes the behavior:
   ```java
   @Test
   @DisplayName("should apply franchise deduction to premium")
   void should_applyFranchise_when_franchiseProvided() {
       // Arrange
       var premium = Premium.of(Money.chf(350));
       var franchise = Franchise.F_300;

       // Act
       var result = premium.applyFranchise(franchise);

       // Assert
       assertThat(result.amount()).isEqualByComparingTo(BigDecimal.valueOf(50));
   }
   ```

3. Run the test and **confirm it FAILS**:
   ```bash
   ./mvnw test -pl backend/govinda-{module} -Dtest=PremiumTest#should_applyFranchise_when_franchiseProvided
   ```

   **The test MUST fail because the code doesn't exist yet.** This proves the test is valid.

### Phase 2: GREEN - Write Minimal Code

1. Write **ONLY** the code needed to pass:
   ```java
   public Premium applyFranchise(Franchise franchise) {
       return Premium.of(this.amount.subtract(franchise.getAmount()));
   }
   ```

2. Run the test and **confirm it PASSES**:
   ```bash
   ./mvnw test -pl backend/govinda-{module} -Dtest=PremiumTest#should_applyFranchise_when_franchiseProvided
   ```

3. **Resist adding extra functionality** - only satisfy the current test.

### Phase 3: REFACTOR - Improve While Green

1. Clean up code (naming, structure, duplication)
2. Run **ALL tests** to ensure nothing broke:
   ```bash
   ./mvnw test -pl backend/govinda-{module}
   ```
3. Repeat for the next requirement

---

## Claude Code Workflows

### New Feature

```
1. UNDERSTAND   → Read existing related code
2. TEST         → Write failing test (RED)
3. VERIFY       → Run test, confirm FAILS
4. IMPLEMENT    → Write minimal code (GREEN)
5. VERIFY       → Run test, confirm PASSES
6. REFACTOR     → Clean up, all tests green
7. i18n         → Add translations to all 4 language files
8. REPEAT       → Next test case
```

### Bug Fix

```
1. REPRODUCE    → Write test that exposes the bug (must FAIL)
2. VERIFY       → Run test, confirm it FAILS
3. FIX          → Make minimal change to fix
4. VERIFY       → Run test, confirm it PASSES
5. REGRESSION   → Run ALL tests, confirm they PASS
```

### Refactoring

```
1. BASELINE     → Run all tests (must PASS)
2. REFACTOR     → Make structural improvements
3. VERIFY       → Run all tests (must still PASS)
4. NO NEW FEATURES - Refactoring is behavior-preserving only
```

---

## Project Structure

```
/backend/
├── govinda-common/         # Shared kernel (security, i18n, exceptions)
├── govinda-masterdata/     # Master data (Person, Household, Address)
├── govinda-app/            # Main Spring Boot application
├── govinda-product/        # Products and tariffs
├── govinda-contract/       # Policies and coverages
├── govinda-premium/        # Premium calculation engine
└── govinda-billing/        # Invoicing and payments
```

### Module Package Structure

```
net.voytrex.govinda.{module}/
├── domain/
│   ├── model/              # Entities, Value Objects, Aggregates
│   ├── repository/         # Repository interfaces (ports)
│   └── exception/          # Domain-specific exceptions
├── application/            # Use cases, Commands, Queries, Services
├── infrastructure/
│   └── persistence/        # JPA adapters
└── api/                    # REST controllers, DTOs, Mappers
```

---

## Testing Templates

### Unit Test (Domain Model)

```java
package net.voytrex.govinda.{module}.domain.model;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

class {ClassName}Test {

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("should create valid instance")
        void should_createInstance_when_validInput() {
            // Arrange
            var input = "valid-input";

            // Act
            var result = new ClassName(input);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getValue()).isEqualTo(input);
        }

        @Test
        @DisplayName("should reject null input")
        void should_throwException_when_inputIsNull() {
            assertThatThrownBy(() -> new ClassName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
        }
    }
}
```

### Service Test (with Mockito)

```java
@ExtendWith(MockitoExtension.class)
class {ServiceName}Test {

    @Mock
    private {Repository} repository;

    @InjectMocks
    private {ServiceName} service;

    @Test
    @DisplayName("should return entity when found")
    void should_returnEntity_when_existsInRepository() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = {Fixture}.createDefault();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        var result = service.findById(id);

        // Assert
        assertThat(result).isPresent();
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("should throw when not found")
    void should_throwException_when_notFound() {
        // Arrange
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getById(id))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
```

### Integration Test (with Testcontainers)

```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class {Repository}IntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private {Repository} repository;

    @Test
    @DisplayName("should persist and retrieve entity")
    void should_persistAndRetrieve_when_validEntity() {
        // Arrange
        var entity = {Fixture}.createDefault();

        // Act
        var saved = repository.save(entity);
        var found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
    }
}
```

---

## Naming Conventions

### Classes

| Type | Pattern | Example |
|------|---------|---------|
| Entity | `{Name}` | `Person`, `Contract` |
| Value Object | `{Concept}` | `AhvNumber`, `Money` |
| Repository (Domain) | `{Entity}Repository` | `PersonRepository` |
| Repository (JPA) | `Jpa{Entity}RepositoryAdapter` | `JpaPersonRepositoryAdapter` |
| Service | `{Domain}Service` | `PersonService` |
| Controller | `{Domain}Controller` | `PersonController` |
| Request DTO | `{Action}{Entity}Request` | `CreatePersonRequest` |
| Response DTO | `{Entity}Response` | `PersonResponse` |
| Command | `{Action}{Entity}Command` | `CreatePersonCommand` |
| Exception | `{Situation}Exception` | `EntityNotFoundException` |
| Test | `{ClassName}Test` | `PersonTest` |
| Fixture | `{Entity}Fixture` | `PersonFixture` |

### Test Methods

```
should_{expectedBehavior}_when_{condition}()
```

Examples:
- `should_calculateAge_when_birthDateProvided()`
- `should_throwException_when_amountIsNegative()`
- `should_returnEmpty_when_entityNotFound()`
- `should_createPerson_when_validDataProvided()`

---

## i18n Rules

**ALL user-facing text MUST be internationalized. NO EXCEPTIONS.**

### Do's and Don'ts

| Do | Don't |
|----|-------|
| Use `LocalizedText` for content | Hardcode translations in enums |
| Use `MessageSource` for errors | Put `nameDe`, `nameFr` in enums |
| Use error codes in exceptions | Hardcode error messages |
| Add keys to ALL 4 language files | Forget any language |

### Message Properties

```
backend/govinda-common/src/main/resources/
├── messages.properties      # English (fallback)
├── messages_de.properties   # German (primary)
├── messages_fr.properties   # French
└── messages_it.properties   # Italian
```

### Key Format

```properties
# Errors
error.entity.not.found=Entity with ID {0} not found
error.person.not.found=Person with ID {0} not found
error.validation.required={0} is required

# Enums (translate in API layer)
canton.ZH=Zurich
canton.BE=Bern
insurance.model.HMO=HMO Model
```

---

## Common Commands

### Tests

```bash
# Single test method
./mvnw test -pl backend/govinda-{module} -Dtest={TestClass}#{method}

# Single test class
./mvnw test -pl backend/govinda-{module} -Dtest={TestClass}

# All tests in module
./mvnw test -pl backend/govinda-{module}

# All tests
./mvnw test

# With coverage
./mvnw test jacoco:report
```

### Build

```bash
# Full build with tests
./mvnw clean install

# Skip tests (only for quick checks)
./mvnw clean install -DskipTests
```

### Run

```bash
./mvnw spring-boot:run -pl backend/govinda-app
```

---

## Value Objects Reference

### Money

```java
Money amount = Money.of(BigDecimal.valueOf(100), Currency.CHF);
Money sum = amount.add(Money.chf(50));
Money rounded = amount.roundToRappen();  // Swiss 5-Rappen rounding
```

### AhvNumber

```java
AhvNumber ahv = AhvNumber.of("756.1234.5678.97");
boolean valid = AhvNumber.isValid("756.1234.5678.97");
String masked = ahv.masked();  // "756.****.****.97"
```

### LocalizedText

```java
LocalizedText text = LocalizedText.builder()
    .de("Deutscher Text")
    .fr("Texte français")
    .it("Testo italiano")
    .en("English text")
    .build();

String localized = text.get(Language.FR);  // "Texte français"
```

---

## Exception Handling

### Throwing

```java
throw new EntityNotFoundException("error.person.not.found", personId);
throw new ValidationException("error.validation.required", "firstName");
throw new DuplicateEntityException("error.person.ahv.duplicate", ahvNumber);
```

### Testing

```java
assertThatThrownBy(() -> service.findById(invalidId))
    .isInstanceOf(EntityNotFoundException.class)
    .hasFieldOrPropertyWithValue("errorCode", "error.person.not.found");
```

---

## AssertJ Quick Reference

```java
// Basic
assertThat(result).isNotNull();
assertThat(result).isEqualTo(expected);
assertThat(name).isEqualTo("Hans");
assertThat(amount).isEqualByComparingTo(BigDecimal.TEN);

// Boolean
assertThat(isActive).isTrue();
assertThat(isEmpty).isFalse();

// String
assertThat(message).contains("error");
assertThat(message).startsWith("Hello");
assertThat(message).isBlank();

// Collections
assertThat(list).hasSize(3);
assertThat(list).contains(item1, item2);
assertThat(list).containsExactly(item1, item2);
assertThat(list).isEmpty();

// Optional
assertThat(optional).isPresent();
assertThat(optional).isEmpty();
assertThat(optional).contains(expected);

// Exceptions
assertThatThrownBy(() -> doSomething())
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("invalid");

assertThatCode(() -> doSomething()).doesNotThrowAnyException();
```

---

## Definition of Done Checklist

Before considering ANY task complete:

```
TDD Compliance:
[ ] Test written FIRST (RED phase)
[ ] Test verified to FAIL before implementation
[ ] Minimal code written (GREEN phase)
[ ] All tests pass after implementation

Code Quality:
[ ] No compiler warnings
[ ] Code follows naming conventions
[ ] Domain layer has no Spring/JPA imports
[ ] No TODO/FIXME comments (create tickets instead)

i18n:
[ ] All user text uses LocalizedText or MessageSource
[ ] No hardcoded translations in enums
[ ] Keys added to ALL 4 message property files
[ ] Error codes translated via MessageSource

Testing:
[ ] Unit tests for domain logic (100% coverage)
[ ] Integration tests for repositories
[ ] Edge cases covered
[ ] All tests pass: ./mvnw test
```

---

## Related Documentation

- [Definition of Done](docs/development/definition-of-done.md) - Complete DoD checklist
- [Coding Guidelines](docs/development/coding-guidelines.md) - Detailed coding standards
- [Testing Strategy](docs/development/testing-strategy.md) - Test pyramid and patterns
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution workflow

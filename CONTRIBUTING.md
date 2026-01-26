# Contributing to Govinda ERP

Thank you for your interest in contributing to Govinda! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## Getting Started

### Prerequisites

- JDK 21 or later
- Docker & Docker Compose
- Git
- IDE: IntelliJ IDEA (recommended) or VS Code

### Setup Development Environment

1. **Clone the repository**
   ```bash
   git clone https://github.com/voytrex/govinda.git
   cd govinda
   ```

2. **Start PostgreSQL**
   ```bash
   docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres
   ```

3. **Build the project**
   ```bash
   mvn -DskipTests package
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

5. **Start the application**
   ```bash
   mvn -pl backend/govinda-app -am spring-boot:run
   ```

6. **Access Swagger UI**
   Open http://localhost:8080/swagger-ui.html

For more details, see the [Developer Guide](docs/development/developer-guide.md).

## Development Workflow

### Branch Naming

- `feature/GOV-XXX-short-description` - New features
- `fix/GOV-XXX-bug-description` - Bug fixes
- `refactor/GOV-XXX-what-refactored` - Refactoring
- `docs/GOV-XXX-documentation-topic` - Documentation

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style (formatting, no logic change)
- `refactor`: Refactoring
- `test`: Tests
- `chore`: Build, CI, dependencies

**Scopes:**
- `masterdata`: Person, household, address
- `contract`: Policy, coverage
- `billing`: Invoices, payments
- `premium`: Premium calculation
- `common`: Shared components
- `api`: REST endpoints

**Example:**
```
feat(masterdata): add person search by AHV number

- Implement fuzzy search for partial AHV numbers
- Add validation for AHV format
- Include unit tests for PersonRepository

Closes #42
```

### Pull Request Process

1. **Create a feature branch** from `develop`
2. **Write tests first** (TDD)
3. **Implement the feature**
4. **Ensure all tests pass**: `mvn test`
5. **Run formatting checks** (if configured)
6. **Create a Pull Request** with:
   - Clear description of changes
   - Link to related issue
   - Screenshots (if UI changes)
7. **Address review feedback**
8. **Merge** after approval

### PR Template

```markdown
## Summary
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking API changes
- [ ] Follows coding standards
- [ ] All user-facing text is internationalized (DE, FR, IT, EN)
- [ ] No hardcoded translations in enums or domain models
```

## Coding Standards

### Java Style

- Follow standard Java coding conventions
- Use records for DTOs and value objects when appropriate
- Prefer immutability where possible
- Use meaningful names (no abbreviations)
- Maximum line length: 120 characters

### Architecture Rules

1. **Layer Dependencies**
   - API → Application → Domain ← Infrastructure
   - Domain has no dependencies on other layers

2. **Package Structure**
   ```
   net.voytrex.govinda.{module}/
   ├── domain/
   │   ├── model/
   │   ├── repository/
   │   └── service/
   ├── application/
   ├── infrastructure/
   └── api/
   ```

3. **Testing**
   - Unit tests for domain logic
   - Integration tests for repositories and APIs
   - Use Mockito for mocking
   - Use Testcontainers for database tests

### Internationalization (i18n)

**CRITICAL: All user-facing text must be internationalized.**

Govinda ERP supports four languages:
- **DE** (German) - Primary language
- **FR** (French)
- **IT** (Italian)
- **EN** (English)

#### Rules for i18n:

1. **Use `LocalizedText` for all user-facing content**
   - Product names, descriptions, labels, and any text displayed to users
   - Use `LocalizedText` class from `net.voytrex.govinda.common.domain.model`
   - Always provide all four language variants (DE, FR, IT, EN)
   - German (DE) is the fallback language if a translation is missing

2. **DO NOT hardcode translations in enums**
   - ❌ **BAD**: `Canton` enum with `nameDe` and `nameFr` fields
   - ❌ **BAD**: `InsuranceModel` enum with `nameDe` field
   - ❌ **BAD**: `ProductCategory` enum with `nameDe` field
   - ✅ **GOOD**: Use a translation service or store translations in the database
   - ✅ **GOOD**: Return enum codes and translate them in the API layer using a message source

3. **Error messages must be internationalized**
   - Use Spring's `MessageSource` for error message translation
   - Error codes should be language-agnostic (e.g., `ENTITY_NOT_FOUND`)
   - Messages should be resolved based on the `Accept-Language` header
   - Avoid hardcoded English fallback messages in exception handlers

4. **API responses**
   - All user-facing strings in API responses must be localized
   - Use `Accept-Language` header to determine the user's preferred language
   - Default to German (DE) if no language preference is specified

5. **Database entities**
   - Use `LocalizedText` embeddable for multilingual fields
   - Example: Product names, descriptions, category names

6. **Exception messages**
   - Domain exceptions should use error codes, not hardcoded messages
   - Translate error codes to messages in the API layer using `MessageSource`

#### Examples:

**✅ Correct - Using LocalizedText:**
```java
public class Product {
    private LocalizedText name;
    private LocalizedText description;
    
    public String getName(Language language) {
        return name.get(language);
    }
}
```

**✅ Correct - Using MessageSource for errors:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        EntityNotFoundException ex,
        HttpServletRequest request,
        Locale locale
    ) {
        String message = messageSource.getMessage(
            ex.getErrorCode(),
            null,
            locale
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getErrorCode(), message, request.getRequestURI()));
    }
}
```

**❌ Incorrect - Hardcoded translations:**
```java
public enum Canton {
    ZH("ZH", "Zürich", "Zurich"),  // ❌ Hardcoded translations
    BE("BE", "Bern", "Berne");
    
    private final String nameDe;
    private final String nameFr;
}
```

### Documentation

- Javadoc for public APIs
- OpenAPI annotations for REST endpoints
- ADR for architectural decisions
- Update README when adding features

## Testing

### Running Tests

```bash
# All tests
mvn test

# Specific module
mvn -pl backend/govinda-masterdata -am test

# With coverage
mvn -DskipTests=false test
```

### Test Structure

```java
class PersonServiceTest {

    @Nested
    class WhenCreatingAPerson {

        @Test
        void shouldValidateAhvNumberFormat() {
            // Given
            String invalidAhv = "invalid";

            // When / Then
            assertThatThrownBy(() -> { /* ... */ })
                .isInstanceOf(InvalidAhvNumberException.class);
        }
    }
}
```

## Questions?

- Open an [issue](https://github.com/voytrex/govinda/issues)
- Check existing [documentation](docs/)

Thank you for contributing!

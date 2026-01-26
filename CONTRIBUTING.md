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

# Contributing to Govinda ERP

Thank you for your interest in contributing to Govinda! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## Getting Started

### Prerequisites

- JDK 21 or later
- Docker & Docker Compose
- Git
- IDE: IntelliJ IDEA (recommended) or VS Code with Kotlin plugin

### Setup Development Environment

1. **Clone the repository**
   ```bash
   git clone https://github.com/voytrex/govinda.git
   cd govinda
   ```

2. **Start PostgreSQL**
   ```bash
   cd infrastructure/docker
   docker-compose up -d postgres
   ```

3. **Build the project**
   ```bash
   cd backend
   ./gradlew build
   ```

4. **Run tests**
   ```bash
   ./gradlew test
   ```

5. **Start the application**
   ```bash
   ./gradlew :govinda-app:bootRun
   ```

6. **Access Swagger UI**
   Open http://localhost:8080/swagger-ui.html

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
4. **Ensure all tests pass**: `./gradlew test`
5. **Run lint checks**: `./gradlew ktlintCheck`
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

### Kotlin Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use data classes for DTOs and value objects
- Prefer immutability (`val` over `var`)
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
   - Use MockK for mocking
   - Use Testcontainers for database tests

### Documentation

- KDoc for public APIs
- OpenAPI annotations for REST endpoints
- ADR for architectural decisions
- Update README when adding features

## Testing

### Running Tests

```bash
# All tests
./gradlew test

# Specific module
./gradlew :govinda-masterdata:test

# With coverage
./gradlew test jacocoTestReport
```

### Test Structure

```kotlin
class PersonServiceTest {

    @Nested
    inner class `When creating a person` {

        @Test
        fun `should validate AHV number format`() {
            // Given
            val invalidAhv = "invalid"

            // When / Then
            assertThatThrownBy { /* ... */ }
                .isInstanceOf(InvalidAhvNumberException::class.java)
        }
    }
}
```

## Questions?

- Open an [issue](https://github.com/voytrex/govinda/issues)
- Check existing [documentation](docs/)

Thank you for contributing!

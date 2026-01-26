# Definition of Done (DoD)

A feature, story, or task is considered **DONE** when all the following criteria are met.

---

## TDD Compliance (MANDATORY)

- [ ] **RED phase completed**: Failing test written BEFORE any implementation
- [ ] **GREEN phase completed**: Minimal code written to pass the test
- [ ] **REFACTOR phase completed**: Code cleaned up while tests remain green
- [ ] No production code exists without corresponding tests
- [ ] Test was verified to fail before implementation (proves test validity)

---

## Code Quality

### General
- [ ] Code compiles without errors or warnings
- [ ] No compiler deprecation warnings (or documented exception)
- [ ] Code formatting follows project standards (120 char lines)
- [ ] No TODO/FIXME comments in production code (create tickets instead)
- [ ] No commented-out code blocks
- [ ] No debug statements (System.out, console.log, etc.)

### Architecture
- [ ] Layer dependencies respected: API → Application → Domain ← Infrastructure
- [ ] Domain layer has ZERO dependencies on infrastructure
- [ ] No circular dependencies between packages
- [ ] Single Responsibility Principle followed
- [ ] Value objects are immutable
- [ ] Entities have proper equals/hashCode based on ID

### Naming
- [ ] Class names are nouns (e.g., `Person`, `ContractService`)
- [ ] Method names are verbs (e.g., `calculatePremium`, `findById`)
- [ ] Boolean methods start with `is`, `has`, `can`, `should`
- [ ] No abbreviations except well-known ones (ID, URL, DTO)
- [ ] Test methods follow `should_{behavior}_when_{condition}()` pattern

---

## Testing

### Coverage Requirements
- [ ] **Domain logic**: 100% coverage (entities, value objects, domain services)
- [ ] **Application services**: Minimum 80% coverage
- [ ] **Infrastructure adapters**: Integration tests exist
- [ ] **API endpoints**: Contract tests exist for all endpoints

### Test Quality
- [ ] Unit tests for ALL domain logic (pure Java, no Spring)
- [ ] Integration tests for repositories (Testcontainers)
- [ ] Contract tests for REST endpoints (MockMvc + Testcontainers)
- [ ] All tests pass locally: `./mvnw test`
- [ ] Tests are deterministic (no random failures)
- [ ] Tests are independent (no order dependency)
- [ ] Tests are fast (unit tests < 100ms each)
- [ ] Edge cases covered (null, empty, boundary values)
- [ ] Error scenarios tested (exceptions, validation failures)
- [ ] AAA pattern followed: Arrange, Act, Assert

### Test Naming
- [ ] Test class: `{ClassName}Test.java`
- [ ] Test methods: `should_{expectedBehavior}_when_{condition}()`
- [ ] `@DisplayName` used for readable test output
- [ ] `@Nested` classes used for logical grouping

---

## Internationalization (i18n)

**All user-facing text MUST be internationalized. NO EXCEPTIONS.**

### Content
- [ ] User-facing text uses `LocalizedText` value object
- [ ] All 4 languages provided: DE, FR, IT, EN
- [ ] German (DE) is fallback language
- [ ] Translation keys follow naming convention: `{domain}.{entity}.{field}`

### Enums
- [ ] NO hardcoded translations in enums (`nameDe`, `nameFr` fields)
- [ ] Enum values translated via `MessageSource` in API layer
- [ ] Translation keys added to all 4 message property files

### Errors
- [ ] Exceptions use error codes, not hardcoded messages
- [ ] Error codes translated via `MessageSource`
- [ ] Error responses respect `Accept-Language` header

### API
- [ ] API responses localized based on `Accept-Language` header
- [ ] Default to German (DE) when no language specified
- [ ] OpenAPI descriptions in English

---

## Security

### Authentication & Authorization
- [ ] Endpoints protected with appropriate `@PreAuthorize`
- [ ] Multi-tenant isolation verified (data cannot leak between tenants)
- [ ] JWT token validation in place for protected endpoints

### Data Protection
- [ ] No sensitive data in logs (passwords, full AHV numbers, tokens)
- [ ] AHV numbers masked in logs (show only last 4 digits)
- [ ] No hardcoded credentials or secrets
- [ ] Passwords hashed with BCrypt (never stored plain)

### Input Validation
- [ ] All inputs validated (null checks, format, length, range)
- [ ] SQL injection prevented (use parameterized queries/JPA)
- [ ] XSS prevented (proper encoding in responses)
- [ ] OWASP Top 10 vulnerabilities considered

---

## API Standards

### RESTful Design
- [ ] Proper HTTP methods: GET (read), POST (create), PUT (replace), PATCH (partial update), DELETE
- [ ] Proper HTTP status codes: 200, 201, 204, 400, 401, 403, 404, 409, 422, 500
- [ ] Resource naming: plural nouns (`/persons`, `/contracts`)
- [ ] API versioning maintained (`/api/v1/...`)

### Response Format
- [ ] Consistent error response structure (code, message, details, timestamp, path)
- [ ] Pagination for list endpoints (page, size, totalElements, totalPages)
- [ ] HATEOAS links where appropriate

### Documentation
- [ ] OpenAPI annotations complete (`@Operation`, `@ApiResponse`, `@Schema`)
- [ ] Request/response examples provided
- [ ] Error scenarios documented

---

## Performance

### Database
- [ ] No N+1 query issues (use `@EntityGraph` or fetch joins)
- [ ] Indexes exist for frequently queried columns
- [ ] Pagination for unbounded queries
- [ ] Optimistic locking (`@Version`) for concurrent updates

### Code
- [ ] No obvious performance bottlenecks
- [ ] Lazy loading used appropriately for associations
- [ ] Batch operations for bulk updates

---

## Documentation

### Code Documentation
- [ ] Javadoc for public classes and methods
- [ ] Complex business logic has inline comments
- [ ] Public API methods document parameters and return values

### Project Documentation
- [ ] README updated for new features
- [ ] ADR written for significant architectural decisions
- [ ] API documentation auto-generated via OpenAPI

---

## Version Control

### Commits
- [ ] Conventional commit format: `type(scope): subject`
- [ ] Atomic commits (one logical change per commit)
- [ ] No broken commits (all tests pass at each commit)

### Pull Request
- [ ] PR description explains what and why
- [ ] Related issues linked
- [ ] No merge conflicts
- [ ] CI pipeline passes
- [ ] Code reviewed by at least 1 team member

---

## Quick Checklists

### Before Writing Code
```
[ ] Understand the requirement
[ ] Write failing test (RED)
[ ] Verify test fails
```

### Before Committing
```
[ ] All tests pass: ./mvnw test
[ ] No compiler warnings
[ ] Code formatted
[ ] i18n keys added to all 4 language files
[ ] No hardcoded strings
```

### Before PR
```
[ ] Self-review completed
[ ] Tests cover edge cases
[ ] Documentation updated
[ ] Conventional commit messages
[ ] CI passes
```

---

## Definition of Done Summary

| Category | Key Requirement |
|----------|-----------------|
| TDD | Test written FIRST, verified to fail |
| Coverage | Domain 100%, Services 80%+ |
| i18n | ALL user text in 4 languages |
| Security | No secrets, input validated, tenant isolated |
| API | RESTful, documented, versioned |
| Performance | No N+1, paginated, indexed |
| Code | Clean, named properly, no warnings |

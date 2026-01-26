# Definition of Done (DoD)

A feature, story, or task is considered **DONE** when all the following criteria are met:

## Code Quality

- [ ] Code compiles without warnings
- [ ] All new code has unit tests (minimum 80% coverage)
- [ ] Integration tests exist for API endpoints
- [ ] No SonarQube critical or blocker issues (when configured)
- [ ] Code formatting checks pass
- [ ] Code reviewed by at least 1 team member
- [ ] No TODO comments left in production code

## Documentation

- [ ] Public APIs have Javadoc comments
- [ ] OpenAPI annotations are complete and accurate
- [ ] ADR written for significant architectural decisions
- [ ] README updated if applicable

## Testing

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Manual testing completed for UI features
- [ ] Edge cases and error scenarios covered
- [ ] Tests are deterministic (no flaky tests)

## Functionality

- [ ] Acceptance criteria from the story/ticket are met
- [ ] Works in all supported languages (DE, FR, IT, EN)
- [ ] Multi-tenant isolation verified
- [ ] Audit trail entries created for relevant changes
- [ ] Error handling provides meaningful messages

## Internationalization (i18n)

- [ ] All user-facing text uses `LocalizedText` or message source
- [ ] No hardcoded translations in enums (e.g., `nameDe`, `nameFr` fields)
- [ ] Error messages are internationalized using `MessageSource`
- [ ] API responses support `Accept-Language` header
- [ ] All four languages (DE, FR, IT, EN) are supported for new features
- [ ] German (DE) is used as fallback when translation is missing
- [ ] Translation keys follow consistent naming convention

## Security

- [ ] No sensitive data logged (passwords, AHV numbers in full, etc.)
- [ ] Input validation implemented
- [ ] Authorization checks in place
- [ ] OWASP Top 10 vulnerabilities considered
- [ ] No hardcoded credentials or secrets

## Performance

- [ ] No N+1 query issues
- [ ] Database indexes reviewed for new queries
- [ ] Pagination implemented for list endpoints
- [ ] No obvious performance bottlenecks

## API Standards

- [ ] RESTful conventions followed
- [ ] Consistent error response format
- [ ] Proper HTTP status codes used
- [ ] API versioning maintained
- [ ] Backward compatibility preserved (or version bumped)

## Commit & PR Standards

- [ ] Commits follow conventional commit format
- [ ] PR description explains changes clearly
- [ ] Related issues/tickets linked
- [ ] No merge conflicts
- [ ] CI pipeline passes

---

## Quick Checklist for PRs

```markdown
## PR Checklist
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Documentation updated
- [ ] No breaking API changes (or version bumped)
- [ ] Manual testing completed
- [ ] Code reviewed
```

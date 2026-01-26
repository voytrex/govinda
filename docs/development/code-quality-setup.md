# Code Quality Setup

## Overview

This document describes the code quality tools and CI/CD improvements added to the Govinda ERP project.

## Code Quality Tools

### 1. Checkstyle

**Purpose**: Enforces coding standards and style consistency.

**Configuration**: `checkstyle.xml` in project root

**Key Rules**:
- Line length: 120 characters
- Method length: 150 lines max
- Parameter count: 7 max
- Enforces naming conventions
- Validates imports and whitespace

**Usage**:
```bash
# Run checkstyle
mvn checkstyle:check

# Generate report
mvn checkstyle:checkstyle
```

**CI Integration**: Runs automatically in `code-quality` job, fails build on violations.

### 2. SpotBugs

**Purpose**: Static analysis to find bugs, security vulnerabilities, and code smells.

**Configuration**: `spotbugs-exclude.xml` in project root

**Key Features**:
- Detects common bugs (null pointer exceptions, resource leaks)
- Security vulnerability detection
- Performance issues
- Code smell detection

**Usage**:
```bash
# Run SpotBugs
mvn spotbugs:check

# Generate HTML report
mvn spotbugs:spotbugs
# View: target/spotbugsXml.html
```

**CI Integration**: Runs in `code-quality` job, results uploaded as artifact.

### 3. JaCoCo (Code Coverage)

**Purpose**: Measures test coverage and enforces minimum coverage thresholds.

**Configuration**: In `backend/pom.xml` plugin configuration

**Coverage Rules**:
- Package-level: 80% line coverage minimum
- Class-level: Maximum 10 missed lines per class

**Usage**:
```bash
# Generate coverage report
mvn jacoco:report
# View: target/site/jacoco/index.html

# Check coverage thresholds
mvn jacoco:check
```

**CI Integration**:
- Coverage reports generated after tests
- Uploaded to Codecov (if configured)
- Artifacts stored for 30 days

## Test Categorization

### Maven Surefire (Unit Tests)

Runs tests matching `*Test.java` (excluding `*IT.java` and `*IntegrationTest.java`).

**Configuration**:
- Parallel execution: `methods` with 4 threads
- Fork count: 1 (reuse forks for speed)
- Memory: 1024MB per fork

### Maven Failsafe (Integration Tests)

Runs tests matching `*IT.java` or `*IntegrationTest.java`.

**Configuration**:
- Parallel execution: `methods` with 2 threads
- Fork count: 1 (reuse forks)
- Memory: 1024MB per fork
- Runs in `integration-test` phase, verifies in `verify` phase

### Test Tags

Tests are categorized using JUnit 5 `@Tag` annotations:

- `@Tag("unit")` - Pure unit tests
- `@Tag("integration")` - Integration tests
- `@Tag("fast")` - Fast tests (< 100ms)
- `@Tag("slow")` - Slow tests (> 1s)
- `@Tag("database")` - Database tests
- `@Tag("api")` - API endpoint tests

See [Test Tags Documentation](test-tags.md) for details.

## CI/CD Improvements

### GitHub Actions Workflow

The CI pipeline now includes:

1. **Code Quality Job** (runs in parallel)
   - Checkstyle validation
   - SpotBugs analysis
   - Results uploaded as artifacts

2. **Build & Test Job**
   - Compiles project
   - Runs unit tests (Surefire)
   - Runs integration tests (Failsafe)
   - Generates test reports
   - Generates coverage reports
   - Uploads coverage to Codecov
   - Stores coverage artifacts

3. **Security Scan Job** (runs in parallel)
   - CodeQL static analysis
   - Security vulnerability detection

### Test Execution Strategy

```
┌─────────────────────────────────────┐
│ 1. Code Quality Checks (parallel)   │
│    - Checkstyle                     │
│    - SpotBugs                       │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│ 2. Build                            │
│    - Compile                        │
│    - Package                        │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│ 3. Unit Tests (Surefire)            │
│    - Fast feedback                  │
│    - Parallel execution             │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│ 4. Integration Tests (Failsafe)     │
│    - Database tests                 │
│    - API tests                      │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│ 5. Reports & Artifacts              │
│    - Test reports                   │
│    - Coverage reports               │
│    - SpotBugs results               │
└─────────────────────────────────────┘
```

## Local Development

### Running Code Quality Checks

```bash
# Checkstyle only
mvn checkstyle:check

# SpotBugs only
mvn spotbugs:check

# Coverage report
mvn jacoco:report
open backend/govinda-common/target/site/jacoco/index.html
```

### Running Tests Selectively

```bash
# Unit tests only (fast)
mvn surefire:test

# Integration tests only
mvn failsafe:integration-test

# All tests
mvn verify

# By tag (if configured)
mvn test -Dgroups=unit
mvn test -Dgroups=integration
```

### Pre-commit Checklist

Before committing, ensure:

```bash
# 1. Code compiles
mvn clean compile

# 2. Code style passes
mvn checkstyle:check

# 3. Unit tests pass
mvn surefire:test

# 4. Integration tests pass (optional, slower)
mvn failsafe:integration-test

# 5. Coverage meets thresholds
mvn jacoco:check
```

## Configuration Files

### Project Structure

```
.
├── checkstyle.xml              # Checkstyle rules
├── spotbugs-exclude.xml        # SpotBugs exclusions
├── backend/
│   └── pom.xml                 # Plugin configurations
└── .github/
    └── workflows/
        └── ci.yml              # CI/CD pipeline
```

### Key Maven Properties

Defined in `backend/pom.xml`:

```xml
<properties>
    <spotbugs.version>4.8.3</spotbugs.version>
    <checkstyle.version>10.12.5</checkstyle.version>
    <jacoco.version>0.8.11</jacoco.version>
    <maven.surefire.version>3.2.5</maven.surefire.version>
    <maven.failsafe.version>3.2.5</maven.failsafe.version>
</properties>
```

## Troubleshooting

### Checkstyle Fails

**Problem**: Checkstyle violations found

**Solution**:
1. Review violations: `mvn checkstyle:checkstyle`
2. Fix violations in code
3. Re-run: `mvn checkstyle:check`

### SpotBugs False Positives

**Problem**: SpotBugs reports false positives

**Solution**:
1. Add exclusion to `spotbugs-exclude.xml`
2. Or use `@SuppressFBWarnings` annotation

### Coverage Below Threshold

**Problem**: JaCoCo check fails due to low coverage

**Solution**:
1. Review coverage report: `mvn jacoco:report`
2. Add tests for uncovered code
3. Or adjust thresholds in `pom.xml` (if justified)

### Integration Tests Fail in CI

**Problem**: Tests pass locally but fail in CI

**Solution**:
1. Check PostgreSQL service health in CI
2. Verify environment variables are set
3. Check test isolation (cleanup between tests)

## Next Steps

### Optional Enhancements

1. **SonarQube Integration**
   - Add SonarQube analysis to CI
   - Quality gates and metrics

2. **Dependency Vulnerability Scanning**
   - OWASP Dependency-Check
   - Snyk or Dependabot

3. **Performance Testing**
   - JMeter or Gatling integration
   - Performance regression detection

4. **Mutation Testing**
   - PIT (Pitest) for test quality validation

## Resources

- [Checkstyle Documentation](https://checkstyle.sourceforge.io/)
- [SpotBugs Documentation](https://spotbugs.github.io/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)

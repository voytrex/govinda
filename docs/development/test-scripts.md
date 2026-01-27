# Test Scripts Guide

## Overview

Two test scripts are provided to run the full test suite locally, mirroring the CI pipeline:

- **`scripts/test-local.sh`** - Uses Testcontainers (requires Docker/Colima)
- **`scripts/test-ci-mode.sh`** - Uses docker-compose PostgreSQL service (simulates CI)

## Quick Start

### Local Testing (Testcontainers)

```bash
./scripts/test-local.sh
```

This script:
1. Configures Docker to use Colima
2. Runs code quality checks (Checkstyle, SpotBugs)
3. Builds the project
4. Runs unit tests (Surefire)
5. Runs integration tests (Failsafe)
6. Generates coverage reports

### CI Mode Testing (docker-compose)

```bash
./scripts/test-ci-mode.sh
```

This script:
1. Starts PostgreSQL via docker-compose
2. Waits for database to be ready
3. Runs the same checks as local mode
4. Uses service PostgreSQL instead of Testcontainers

## Script Options

Both scripts support the same options:

### Skip Specific Phases

```bash
# Skip code quality checks
./scripts/test-local.sh --skip-quality

# Skip unit tests
./scripts/test-local.sh --skip-unit

# Skip integration tests
./scripts/test-local.sh --skip-integration

# Skip coverage report
./scripts/test-local.sh --skip-coverage

# Combine options
./scripts/test-local.sh --skip-quality --skip-coverage
```

### Run Only Specific Phases

```bash
# Unit tests only (fast feedback)
./scripts/test-local.sh --unit-only

# Integration tests only
./scripts/test-local.sh --integration-only
```

## Usage Examples

### Fast Development Cycle

During active development, run only unit tests:

```bash
./scripts/test-local.sh --unit-only
```

This skips:
- Code quality checks
- Integration tests
- Coverage reports

### Before Committing

Run full suite to ensure everything passes:

```bash
./scripts/test-local.sh
```

### Before Pushing

Run in CI mode to simulate GitHub Actions:

```bash
./scripts/test-ci-mode.sh
```

### Debugging Integration Tests

Run only integration tests with verbose output:

```bash
./scripts/test-local.sh --integration-only -X
```

## What Each Script Does

### scripts/test-local.sh

1. **Setup**: Configures `DOCKER_HOST` for Colima
2. **Code Quality**:
   - Checkstyle validation
   - SpotBugs analysis
3. **Build**: Compiles and packages (skips tests)
4. **Unit Tests**: Runs Surefire (fast tests)
5. **Integration Tests**: Runs Failsafe (database tests)
6. **Coverage**: Generates JaCoCo reports

### scripts/test-ci-mode.sh

1. **Setup**: Starts PostgreSQL container via docker-compose
2. **Wait**: Ensures PostgreSQL is ready
3. **Environment**: Sets CI-like environment variables
4. **Same Steps**: Follows same phases as local script

## Output

### Success

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ All checks completed successfully!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Coverage Reports

After completion, coverage reports are available at:

```
backend/govinda-common/target/site/jacoco/index.html
backend/govinda-masterdata/target/site/jacoco/index.html
```

Open in browser to view detailed coverage.

### Test Reports

Test results are available at:

```
backend/**/target/surefire-reports/  # Unit tests
backend/**/target/failsafe-reports/  # Integration tests
```

## Troubleshooting

### Colima Not Running

**Error**: `Cannot connect to Docker daemon`

**Solution**:
```bash
colima start
./scripts/test-local.sh
```

### PostgreSQL Not Ready

**Error**: Connection refused in CI mode

**Solution**:
```bash
# Check container status
docker-compose -f infrastructure/docker/docker-compose.yml ps

# Restart if needed
docker-compose -f infrastructure/docker/docker-compose.yml restart postgres

# Wait a bit longer
sleep 5
./scripts/test-ci-mode.sh
```

### Checkstyle Failures

**Error**: Checkstyle violations found

**Solution**:
1. Review violations: `mvn checkstyle:checkstyle`
2. Fix code style issues
3. Re-run: `./scripts/test-local.sh`

### Test Failures

**Error**: Tests failing

**Solution**:
1. Run specific test class: `mvn test -Dtest=UserTest`
2. Run with debug: `mvn test -X`
3. Check logs in `target/surefire-reports/`

### Coverage Below Threshold

**Warning**: Coverage below 80%

**Solution**:
1. View report: Open `target/site/jacoco/index.html`
2. Identify uncovered code
3. Add tests for missing coverage
4. Or adjust threshold in `backend/pom.xml` (if justified)

## Comparison with CI

| Phase | Local Script | CI Mode Script | GitHub Actions |
|-------|-------------|----------------|----------------|
| Docker | Colima | docker-compose | Service container |
| Database | Testcontainers | docker-compose | Service container |
| Code Quality | ✅ | ✅ | ✅ (separate job) |
| Unit Tests | ✅ | ✅ | ✅ |
| Integration Tests | ✅ | ✅ | ✅ |
| Coverage | ✅ | ✅ | ✅ |

## Best Practices

1. **During Development**: Use `--unit-only` for fast feedback
2. **Before Commit**: Run full `./scripts/test-local.sh`
3. **Before Push**: Run `./scripts/test-ci-mode.sh` to simulate CI
4. **CI Failures**: Reproduce locally with `./scripts/test-ci-mode.sh`

## Integration with IDE

### IntelliJ IDEA

1. Right-click script → "Run"
2. Or configure as external tool
3. View output in Run window

### VS Code

1. Use integrated terminal
2. Run: `./scripts/test-local.sh`
3. View test results in Test Explorer

## Next Steps

- Add script to run specific test classes
- Add performance benchmarking
- Add mutation testing (PIT)
- Add test result summary

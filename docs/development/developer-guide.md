# Developer Guide

This guide covers local setup, build, test, and run commands for Govinda ERP.

## Requirements

- JDK 21+
- Maven 3.9+
- Docker & Docker Compose (for PostgreSQL/Testcontainers)
- Git
- IDE: IntelliJ IDEA (recommended) or VS Code

## Repository Setup

```bash
git clone https://github.com/voytrex/govinda.git
cd govinda
```

## Database (Docker Compose)

Start PostgreSQL:

```bash
docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres
```

Optional tools:

```bash
docker-compose --profile tools up -d
```

## Build

From the repo root:

```bash
mvn -DskipTests package
```

## Run the Application

```bash
mvn -pl backend/govinda-app -am spring-boot:run
```

Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

## Testing

Run all tests:

```bash
mvn test
```

Run a single module:

```bash
mvn -pl backend/govinda-masterdata -am test
```

Run a single test class:

```bash
mvn -pl backend/govinda-common -am -Dtest=AuthControllerIntegrationTest test
```

### Testcontainers Notes

- Integration tests use Testcontainers and are skipped if Docker is unavailable.
- Ensure Docker/Colima is running for full integration coverage.

## Common Tasks

- Clean build: `mvn clean`
- Rebuild with tests: `mvn clean test`
- Rebuild and run app: `mvn -pl backend/govinda-app -am spring-boot:run`


# Authentication Testing Strategy

## Overview

This document outlines the testing strategy for authentication and authorization features, following TDD and DDD principles.

## Test Pyramid

```
        /\
       /  \     E2E Tests (Manual/Swagger UI)
      /----\
     /      \   Integration Tests (API endpoints)
    /--------\
   /          \  Unit Tests (Domain, Services, Filters)
  /------------\
```

## Test Coverage

### 1. Domain Model Tests ✅

**Location**: `govinda-common/src/test/java/net/voytrex/govinda/common/domain/model/`

- `UserTest.java` - User entity business logic
- `RoleTest.java` - Role and permission checking
- `UserTenantTest.java` - User-tenant relationship

**Coverage**:
- Entity creation and validation
- Business logic (fullName, isActive, etc.)
- Permission checking logic

### 2. Service Tests ✅

**Location**: `govinda-common/src/test/java/net/voytrex/govinda/common/security/`

- `JwtTokenServiceTest.java` - JWT token generation and validation
- `AuthenticationServiceTest.java` - Authentication business logic

**Coverage**:
- Token generation with correct claims
- Token validation (valid, invalid, expired)
- Authentication success and failure scenarios
- Tenant selection logic
- Permission extraction

### 3. Integration Tests ✅

**Location**: `govinda-common/src/test/java/net/voytrex/govinda/common/api/`

- `AuthControllerIntegrationTest.java` - API endpoint tests

**Coverage**:
- Login endpoint (success, failures)
- Get user tenants endpoint
- Get current user endpoint
- Authentication required for protected endpoints

### 4. Security Component Tests (TODO)

**Location**: `govinda-common/src/test/java/net/voytrex/govinda/common/security/`

- `JwtAuthenticationFilterTest.java` - Filter behavior
- `TenantContextResolverTest.java` - Tenant validation

**Coverage**:
- Token extraction from headers
- Authentication context setting
- Tenant context validation
- Public endpoint bypass

## Test Data Setup

### Test Fixtures

Use consistent test data across tests:

```java
UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000002");
UUID roleId = UUID.fromString("00000000-0000-0000-0000-000000000003");
```

### Test Users

- **admin**: Full access, default tenant
- **testuser**: Standard user, multiple tenants
- **readonly**: Read-only access

## Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn -pl backend/govinda-common -am -Dtest=AuthenticationServiceTest test

# With coverage
mvn -DskipTests=false test
```

## Test Principles

1. **Isolation**: Each test is independent
2. **Deterministic**: Tests produce same results every time
3. **Fast**: Unit tests run in milliseconds
4. **Clear**: Test names describe what is being tested
5. **Complete**: Cover happy path, error cases, edge cases

## Next Steps

1. ✅ Domain model tests
2. ✅ Service tests
3. ✅ Integration tests (basic)
4. ⏳ Security filter tests
5. ⏳ Tenant context resolver tests
6. ⏳ End-to-end authentication flow tests

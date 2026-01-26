# Authentication & Authorization Use Cases

## Overview

This document defines the use cases for authentication and authorization in the Govinda ERP system, following Domain-Driven Design principles.

## Bounded Context: Identity & Access Management

### Domain Model

- **User**: Represents an application user who can access multiple tenants
- **Role**: Defines a set of permissions (e.g., ADMIN, USER, READONLY)
- **Permission**: Fine-grained access control (resource:action pattern, e.g., `person:read`)
- **UserTenant**: Links a user to a tenant with a specific role
- **Tenant**: Represents an insurance company/organization

### Business Rules

1. Users can access multiple tenants
2. Each user-tenant relationship has exactly one role
3. A user can have different roles in different tenants
4. One tenant per user can be marked as default
5. All API requests require authentication (except public endpoints)
6. All API requests require tenant context
7. Users can only access tenants they are assigned to
8. Permissions are checked on every request

---

## Use Case 1: Authenticate User

### Specification

**Actor**: User (via API client)

**Preconditions**:
- User exists in the system
- User account is ACTIVE
- User has at least one tenant assignment

**Main Flow**:
1. User provides username and password
2. System validates credentials
3. System determines target tenant (from request or user's default)
4. System verifies user has access to target tenant
5. System generates JWT token with user ID, tenant ID, and permissions
6. System updates user's last login timestamp
7. System returns JWT token

**Alternative Flows**:

**1a. Invalid credentials**:
- System returns 401 Unauthorized

**1b. User account is INACTIVE or LOCKED**:
- System returns 401 Unauthorized with appropriate message

**1c. User has no tenant access**:
- System returns 401 Unauthorized

**1d. User does not have access to requested tenant**:
- System returns 401 Unauthorized

**Postconditions**:
- User's last login timestamp is updated
- JWT token is generated and returned

**Business Rules**:
- Password must be hashed using BCrypt
- JWT token expires after configured time (default: 1 hour)
- JWT token includes user ID, username, tenant ID, and permissions
- If tenant ID not provided, use user's default tenant

---

## Use Case 2: Validate JWT Token

### Specification

**Actor**: System (Security Filter)

**Preconditions**:
- Request includes Authorization header with Bearer token

**Main Flow**:
1. System extracts JWT token from Authorization header
2. System validates token signature
3. System checks token expiration
4. System extracts user ID, tenant ID, and permissions
5. System sets authentication context
6. System sets tenant context in request attributes

**Alternative Flows**:

**2a. Token missing or invalid format**:
- System continues without authentication (public endpoints allowed)

**2b. Token signature invalid**:
- System rejects request with 401 Unauthorized

**2c. Token expired**:
- System rejects request with 401 Unauthorized

**Postconditions**:
- Authentication context is set (if valid token)
- Tenant context is available in request attributes

---

## Use Case 3: Validate Tenant Access

### Specification

**Actor**: System (Tenant Context Resolver)

**Preconditions**:
- Request is authenticated
- Tenant ID is provided (from header or token)

**Main Flow**:
1. System extracts tenant ID from request (header or token)
2. System gets authenticated user ID
3. System verifies user has UserTenant relationship for tenant
4. System allows request to proceed

**Alternative Flows**:

**3a. Tenant ID missing**:
- System returns 400 Bad Request

**3b. User does not have access to tenant**:
- System returns 403 Forbidden

**3c. Tenant ID invalid format**:
- System returns 400 Bad Request

**Postconditions**:
- Tenant context is validated
- Request proceeds if validation passes

**Business Rules**:
- Tenant ID can come from `X-Tenant-Id` header or JWT token
- If both provided, header takes precedence
- Unauthenticated requests to public endpoints skip tenant validation

---

## Use Case 4: Check Permission

### Specification

**Actor**: System (Method Security)

**Preconditions**:
- Request is authenticated
- Endpoint has `@PreAuthorize` annotation

**Main Flow**:
1. System extracts permissions from JWT token
2. System checks if user has required permission
3. System allows request if permission granted

**Alternative Flows**:

**4a. User lacks required permission**:
- System returns 403 Forbidden

**Postconditions**:
- Request proceeds if permission check passes

**Business Rules**:
- Permissions follow `resource:action` pattern (e.g., `person:read`, `person:write`)
- Roles aggregate multiple permissions
- User's permissions come from their role in the current tenant context

---

## Use Case 5: Get User's Accessible Tenants

### Specification

**Actor**: Authenticated User

**Preconditions**:
- User is authenticated

**Main Flow**:
1. System gets authenticated user ID
2. System retrieves all UserTenant relationships for user
3. System returns list of tenants with roles

**Postconditions**:
- User receives list of accessible tenants with their roles

**Business Rules**:
- Returns all tenants user has access to
- Includes role information for each tenant
- Indicates which tenant is default

---

## Use Case 6: Get Current User Information

### Specification

**Actor**: Authenticated User

**Preconditions**:
- User is authenticated

**Main Flow**:
1. System extracts user ID from authentication context
2. System extracts permissions from JWT token
3. System returns user information

**Postconditions**:
- User receives their user ID and permissions

---

## Permission Model

### Resources and Actions

| Resource | Actions | Description |
|----------|---------|-------------|
| `person` | `read`, `write`, `delete` | Person master data |
| `contract` | `read`, `write` | Policy and coverage management |
| `premium` | `read`, `write` | Premium calculation |
| `billing` | `read`, `write` | Invoice and payment management |

### Role Definitions

**ADMIN**:
- All permissions on all resources

**USER**:
- `read` and `write` permissions on all resources
- No `delete` permissions

**READONLY**:
- `read` permissions on all resources
- No write or delete permissions

---

## Non-Functional Requirements

1. **Security**:
   - Passwords must be hashed with BCrypt (minimum 10 rounds)
   - JWT tokens must use HS256 algorithm
   - JWT secret must be at least 256 bits
   - Tokens must expire (configurable, default 1 hour)

2. **Performance**:
   - Token validation should be fast (no database lookup)
   - Permission checks should be in-memory (from token)

3. **Auditability**:
   - Last login timestamp tracked
   - All authentication attempts logged

4. **Multi-tenancy**:
   - Tenant isolation enforced at all layers
   - Users can switch tenants (with new token)

---

## Error Handling

| Error | HTTP Status | Error Code |
|-------|-------------|------------|
| Invalid credentials | 401 | `AUTHENTICATION_ERROR` |
| User account locked/inactive | 401 | `AUTHENTICATION_ERROR` |
| Missing tenant ID | 400 | `MISSING_TENANT_ID` |
| Unauthorized tenant access | 403 | `UNAUTHORIZED_TENANT_ACCESS` |
| Missing permission | 403 | `ACCESS_DENIED` |
| Invalid token | 401 | `AUTHENTICATION_ERROR` |
| Expired token | 401 | `AUTHENTICATION_ERROR` |

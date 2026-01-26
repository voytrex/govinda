# ADR-001: Technology Stack Selection

## Status
**Accepted**

## Date
2024-01-01

## Context

We are building an open-source ERP system (Govinda) for Swiss health insurance companies. The system needs to:

- Handle complex business logic (premium calculation, policy management)
- Support multi-tenancy for multiple insurance companies
- Provide a robust REST API
- Ensure data integrity and audit trails
- Scale to 500+ concurrent users
- Support Swiss compliance requirements (KVG/VVG, BAG)

## Decision

### Backend Language: Java 21 (LTS)

**Chosen because:**
- Long-term support and stability (2026-ready)
- Broad ecosystem and tooling support
- Strong interoperability with Spring Boot 3.2+
- Predictable upgrade path with LTS cadence
- Native support for records and sealed classes

### Framework: Spring Boot 3.2+

**Rationale:**
- Mature, battle-tested framework
- Excellent Java support
- Comprehensive ecosystem (Security, Data JPA, Actuator)
- Strong community and documentation
- Native OpenAPI/Swagger integration
- Easy testing with Mockito and Testcontainers

### Database: PostgreSQL 18+

**Chosen over alternatives because:**
- Robust ACID compliance
- Excellent JSON support (JSONB for flexible fields)
- Advanced features (partial indexes, CTEs, window functions)
- Mature, open-source, widely deployed
- Good support for temporal/bitemporal data patterns
- Excellent Spring Data JPA support

### Build System: Maven

**Rationale:**
- Widely adopted in enterprise Java ecosystems
- Stable, predictable build lifecycle
- Strong multi-module support
- Excellent CI/CD integration

### API Documentation: SpringDoc OpenAPI 3.1

**Rationale:**
- Standard OpenAPI specification
- Automatic Swagger UI generation
- Code-first approach with annotations
- Support for Java records and annotations

## Consequences

### Positive
- Stable, widely adopted Java toolchain
- Excellent tooling support (IntelliJ IDEA)
- Large talent pool familiar with Spring ecosystem
- Easier onboarding with standard Java

### Negative
- More boilerplate than some alternatives in certain areas

### Risks
- Java LTS upgrade cadence requires periodic migration
- Mitigation: Pin versions, test upgrades in CI

## Alternatives Considered

| Alternative | Reason for Rejection |
|-------------|---------------------|
| Quarkus | Smaller ecosystem, less mature |
| Micronaut | Less Spring ecosystem integration |
| Node.js | Less suitable for complex business logic |
| Go | Less suitable for enterprise ORM patterns |
| MySQL | Less feature-rich than PostgreSQL |
| MongoDB | ACID requirements favor relational DB |

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

### Backend Language: Kotlin

**Chosen over Java because:**
- Null safety built into the type system (critical for healthcare data)
- More concise syntax, less boilerplate
- Data classes for domain models
- Sealed classes for representing business states
- Full interoperability with Java ecosystem
- First-class support in Spring Boot
- Coroutines for async operations (future)

### Framework: Spring Boot 3.2+

**Rationale:**
- Mature, battle-tested framework
- Excellent Kotlin support
- Comprehensive ecosystem (Security, Data JPA, Actuator)
- Strong community and documentation
- Native OpenAPI/Swagger integration
- Easy testing with MockK and Testcontainers

### Database: PostgreSQL 16+

**Chosen over alternatives because:**
- Robust ACID compliance
- Excellent JSON support (JSONB for flexible fields)
- Advanced features (partial indexes, CTEs, window functions)
- Mature, open-source, widely deployed
- Good support for temporal/bitemporal data patterns
- Excellent Spring Data JPA support

### Build System: Gradle with Kotlin DSL

**Rationale:**
- More flexible than Maven
- Kotlin DSL provides type-safe build scripts
- Better performance with build caching
- Multi-module support
- Incremental compilation

### API Documentation: SpringDoc OpenAPI 3.1

**Rationale:**
- Standard OpenAPI specification
- Automatic Swagger UI generation
- Code-first approach with annotations
- Support for Kotlin types

## Consequences

### Positive
- Modern, productive development experience
- Strong type safety reduces runtime errors
- Excellent tooling support (IntelliJ IDEA)
- Large talent pool familiar with Spring ecosystem
- Easy onboarding for Java developers

### Negative
- Team needs Kotlin training (if coming from Java)
- Some libraries may have Java-only documentation
- Kotlin compilation slightly slower than Java

### Risks
- Kotlin version compatibility with Spring Boot upgrades
- Mitigation: Pin versions, test upgrades in CI

## Alternatives Considered

| Alternative | Reason for Rejection |
|-------------|---------------------|
| Java 21 | More verbose, no null safety |
| Quarkus | Smaller ecosystem, less mature |
| Micronaut | Less Spring ecosystem integration |
| Node.js | Less suitable for complex business logic |
| Go | Less suitable for enterprise ORM patterns |
| MySQL | Less feature-rich than PostgreSQL |
| MongoDB | ACID requirements favor relational DB |

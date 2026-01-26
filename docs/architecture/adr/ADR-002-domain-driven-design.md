# ADR-002: Domain-Driven Design Architecture

## Status
**Accepted**

## Date
2024-01-01

## Context

The Swiss health insurance domain is complex with:
- Multiple bounded contexts (master data, contracts, billing, claims)
- Complex business rules (KVG/VVG regulations)
- Need for clear separation of concerns
- Long-term maintainability requirements

## Decision

We adopt Domain-Driven Design (DDD) principles with the following structure:

### Bounded Contexts

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   MASTERDATA    │  │    PRODUCT      │  │    CONTRACT     │
│   (Stammdaten)  │  │   (Produkte)    │  │   (Verträge)    │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ • Person        │  │ • Product       │  │ • Policy        │
│ • Household     │  │ • Tariff        │  │ • Coverage      │
│ • Address       │  │ • PremiumTable  │  │ • Mutation      │
└─────────────────┘  └─────────────────┘  └─────────────────┘
         │                   │                    │
         └───────────────────┼────────────────────┘
                             │
┌─────────────────┐  ┌───────┴─────────┐  ┌─────────────────┐
│    BILLING      │  │    PREMIUM      │  │    BENEFITS     │
│ (Fakturierung)  │  │  (Berechnung)   │  │  (Leistungen)   │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ • Invoice       │  │ • Calculator    │  │ • Claim         │
│ • Payment       │  │ • PremiumResult │  │ • Benefit       │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

### Layer Architecture per Module

```
┌─────────────────────────────────────────┐
│              API Layer                   │
│  • REST Controllers                      │
│  • Request/Response DTOs                 │
│  • OpenAPI Annotations                   │
├─────────────────────────────────────────┤
│          Application Layer               │
│  • Use Cases / Application Services      │
│  • Commands & Queries                    │
│  • Transaction Management                │
├─────────────────────────────────────────┤
│           Domain Layer                   │
│  • Entities & Aggregates                 │
│  • Value Objects                         │
│  • Domain Services                       │
│  • Repository Interfaces                 │
│  • Domain Events                         │
├─────────────────────────────────────────┤
│        Infrastructure Layer              │
│  • JPA Repository Implementations        │
│  • External Service Clients              │
│  • Database Configurations               │
└─────────────────────────────────────────┘
```

### Key Patterns

1. **Aggregates**: Policy is the aggregate root for coverages
2. **Value Objects**: Money, AhvNumber, LocalizedText
3. **Domain Events**: PersonCreated, CoverageChanged, InvoiceGenerated
4. **Repository Pattern**: Domain defines interfaces, infrastructure implements

### Package Structure

```
net.voytrex.govinda.{module}/
├── domain/
│   ├── model/          # Entities, Value Objects
│   ├── repository/     # Repository interfaces
│   ├── service/        # Domain services
│   └── event/          # Domain events
├── application/
│   ├── command/        # Commands (write operations)
│   ├── query/          # Queries (read operations)
│   └── service/        # Application services
├── infrastructure/
│   ├── persistence/    # JPA repositories
│   └── external/       # External service clients
└── api/
    ├── controller/     # REST controllers
    └── dto/            # Request/Response DTOs
```

## Consequences

### Positive
- Clear separation of concerns
- Domain logic isolated from infrastructure
- Testable business rules
- Easier to understand and maintain
- Supports independent module evolution

### Negative
- More initial structure/boilerplate
- Learning curve for DDD concepts
- Need discipline to maintain boundaries

### Risks
- Over-engineering simple operations
- Mitigation: Keep it pragmatic, don't force patterns

## References
- Evans, Eric. "Domain-Driven Design"
- Vernon, Vaughn. "Implementing Domain-Driven Design"

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
│ • Organization  │  │ • PricingTier   │  │ • Coverage      │
│ • Household     │  │ • Tariff        │  │ • Coverage      │
│ • Address       │  │ • PremiumTable  │  │ • Mutation      │
│                 │  │                 │  │ • Exemption     │
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

### Domain Extensions (Multi-Subscription)

Govinda is extended to support multiple regulatory domains while keeping core contexts stable:

- **HEALTHCARE** (KVG/VVG): existing rules and products
- **BROADCAST** (RTVG/BAKOM/ESTV): household and corporate fees
- **TELECOM** (commercial): plans, bundles, usage-based billing

Domain-specific rules live in dedicated modules/services, while **masterdata**, **product**, **contract**, and **billing** remain shared.

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
- Shared subscription core across regulated domains

### Negative
- More initial structure/boilerplate
- Learning curve for DDD concepts
- Need discipline to maintain boundaries

### Risks
- Over-engineering simple operations
- Mitigation: Keep it pragmatic, don't force patterns

## Amendments

- **2026-01-28**: Extended architecture to multi-domain subscription (broadcast/telecom) while keeping shared bounded contexts.

## References
- Evans, Eric. "Domain-Driven Design"
- Vernon, Vaughn. "Implementing Domain-Driven Design"

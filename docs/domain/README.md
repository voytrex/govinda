# Govinda Domain Model Documentation

This documentation provides a comprehensive overview of the Swiss health insurance domain model used in the Govinda ERP system. It serves both as a **developer reference** and as **educational material** for understanding the Swiss healthcare system.

## Quick Navigation

| Section | Description | Audience |
|---------|-------------|----------|
| [Concepts](./concepts/) | Swiss healthcare system fundamentals | Everyone |
| [Entities](./entities/) | Domain model entities and relationships | Developers |
| [Regulatory](./regulatory/) | BAG compliance and legal requirements | Compliance, Developers |
| [Glossary](./glossary/) | Multilingual terminology (DE/FR/IT/EN) | Everyone |
| [Roadmap](./roadmap/) | Implementation status and planned features | Developers |

---

## Learning Paths

### For End Users / Business Stakeholders

Understanding Swiss health insurance in order:

1. **[Swiss Healthcare System Overview](./concepts/swiss-healthcare-system.md)** - Start here
2. **[KVG - Mandatory Basic Insurance](./concepts/kvg-mandatory-insurance.md)** - The foundation
3. **[VVG - Supplementary Insurance](./concepts/vvg-supplementary-insurance.md)** - Optional coverage
4. **[Franchise System](./concepts/franchise-system.md)** - Annual deductibles
5. **[Premium Regions](./concepts/premium-regions.md)** - Why premiums differ by location
6. **[Age Groups](./concepts/age-groups.md)** - Premium categories by age
7. **[Insurance Models](./concepts/insurance-models.md)** - HMO, Hausarzt, Telmed
8. **[Glossary (German)](./glossary/glossary-de.md)** - Key terms explained

### For Developers

Technical documentation in order:

1. **[Swiss Healthcare System Overview](./concepts/swiss-healthcare-system.md)** - Domain context
2. **[Entity Overview](./entities/README.md)** - Domain model diagrams
3. **[Masterdata Entities](./entities/masterdata/)** - Person, Household, Address
4. **[Value Objects](./entities/value-objects/)** - Money, AhvNumber, LocalizedText
5. **[Product Entities](./entities/product/)** - Product, Tariff, PremiumEntry
6. **[Contract Entities](./entities/contract/)** - Policy, Coverage, Mutation
7. **[Regulatory Requirements](./regulatory/)** - Compliance rules

---

## Domain Model at a Glance

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         GOVINDA DOMAIN MODEL                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  MASTERDATA              PRODUCT                 CONTRACT               │
│  ───────────            ─────────               ──────────              │
│  ┌─────────┐           ┌─────────┐             ┌─────────┐             │
│  │ Person  │           │ Product │             │ Policy  │             │
│  │─────────│           │─────────│             │─────────│             │
│  │ AHV-Nr  │           │ KVG/VVG │             │ Holder  │             │
│  │ Name    │           │ Model   │             │ Status  │             │
│  │ Gender  │◄──────────│ Status  │◄────────────│ Billing │             │
│  │ Birth   │           └────┬────┘             └────┬────┘             │
│  └────┬────┘                │                       │                  │
│       │                     │                       │                  │
│       │               ┌─────▼─────┐           ┌─────▼─────┐            │
│  ┌────▼────┐          │  Tariff   │           │ Coverage  │            │
│  │ Address │          │───────────│           │───────────│            │
│  │─────────│          │ Version   │           │ Person    │            │
│  │ Canton  │          │ ValidFrom │◄──────────│ Product   │            │
│  │ Region  │◄─────────│ Premiums  │           │ Franchise │            │
│  └─────────┘          └───────────┘           │ Premium   │            │
│                                               └───────────┘            │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Key Concepts Summary

| Concept | German | Description |
|---------|--------|-------------|
| **KVG** | Krankenversicherungsgesetz | Mandatory basic health insurance (federal law) |
| **VVG** | Versicherungsvertragsgesetz | Supplementary insurance (private law) |
| **Franchise** | Franchise | Annual deductible (CHF 300-2500 for adults) |
| **Selbstbehalt** | Selbstbehalt | 10% co-payment after franchise (max CHF 700/year) |
| **Premium Region** | Prämienregion | BAG-defined geographic zones affecting premiums |
| **Age Group** | Altersgruppe | Child (0-18), Young Adult (19-25), Adult (26+) |

---

## Official Resources

### BAG - Federal Office of Public Health

| Resource | URL | Description |
|----------|-----|-------------|
| **BAG Main Site** | [bag.admin.ch](https://www.bag.admin.ch) | Official health authority |
| **Premium Comparison** | [priminfo.admin.ch](https://www.priminfo.admin.ch) | Compare insurance premiums |
| **KVG Full Text** | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de) | Health Insurance Act |
| **VVG Full Text** | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de) | Insurance Contract Act |
| **Premium Regions** | [bag.admin.ch/regionen](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html) | Region definitions |

> **Note**: URLs are subject to change. Last verified: 2026-01. If a link is broken, search for the topic on [bag.admin.ch](https://www.bag.admin.ch).

---

## Document Conventions

### Terminology

- **German terms** are used as primary reference (Swiss standard)
- **Code references** link to actual implementation: `ClassName` or `file.java:line`
- **Enums** are shown with their code values: `Gender.MALE("M")`

### Icons

| Icon | Meaning |
|------|---------|
| ✅ | Implemented in codebase |
| ⏳ | Planned for future implementation |
| ⚠️ | Important business rule or constraint |
| 📋 | Regulatory requirement |

### Updating This Documentation

When BAG regulations change:

1. Check the official [BAG announcements](https://www.bag.admin.ch/bag/de/home/das-bag/aktuell/medienmitteilungen.html)
2. Update relevant concept files in `/docs/domain/concepts/`
3. Update the glossary if new terms are introduced
4. Note the change date in the "Last Updated" section of affected files

---

## Implementation Status

| Module | Status | Documentation |
|--------|--------|---------------|
| **Masterdata** | ✅ ~50% | [Person](./entities/masterdata/person.md), [Household](./entities/masterdata/household.md), [Address](./entities/masterdata/address.md) |
| **Product** | ⏳ Planned | [Product](./entities/product/product.md), [Tariff](./entities/product/tariff.md) |
| **Contract** | ⏳ Planned | [Policy](./entities/contract/policy.md), [Coverage](./entities/contract/coverage.md) |
| **Premium** | ⏳ Future | - |
| **Billing** | ⏳ Future | - |

See [Roadmap](./roadmap/) for detailed implementation timeline.

---

*Last Updated: 2026-01-26*

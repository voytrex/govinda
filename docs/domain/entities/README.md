# Domain Model Entities

This section documents the domain model entities used in the Govinda ERP system. The documentation follows Domain-Driven Design (DDD) principles with clearly defined aggregates, entities, and value objects.

---

## Bounded Contexts Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         GOVINDA BOUNDED CONTEXTS                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌───────────────┐   ┌───────────────┐   ┌───────────────┐                │
│  │  MASTERDATA   │   │   PRODUCT     │   │   CONTRACT    │                │
│  │  (Stammdaten) │   │   (Produkte)  │   │   (Verträge)  │                │
│  │               │   │               │   │               │                │
│  │  ✅ ~50%      │   │  ⏳ Planned   │   │  ⏳ Planned   │                │
│  │               │   │               │   │               │                │
│  │ • Person      │   │ • Product     │   │ • Policy      │                │
│  │ • Household   │   │ • Tariff      │   │ • Coverage    │                │
│  │ • Organization│   │ • PricingTier │   │ • Exemption   │                │
│  │ • Address     │   │ • PremiumEntry│   │ • Mutation    │                │
│  │               │   │ • PremiumRegion│  │               │                │
│  └───────────────┘   └───────────────┘   └───────────────┘                │
│         │                   │                   │                          │
│         └───────────────────┴───────────────────┘                          │
│                             │                                              │
│                    ┌────────▼────────┐                                     │
│                    │     COMMON      │                                     │
│                    │ (Shared Kernel) │                                     │
│                    │                 │                                     │
│                    │ • Value Objects │                                     │
│                    │ • Enums         │                                     │
│                    │ • Interfaces    │                                     │
│                    └─────────────────┘                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Entity Relationship Diagram

### Masterdata Context

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        MASTERDATA ENTITIES                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────┐          ┌─────────────────────┐                │
│   │      PERSON         │          │     HOUSEHOLD       │                │
│   │   (Aggregate Root)  │          │   (Aggregate Root)  │                │
│   ├─────────────────────┤          ├─────────────────────┤                │
│   │ id: UUID            │          │ id: UUID            │                │
│   │ tenantId: UUID      │          │ tenantId: UUID      │                │
│   │ ahvNr: AhvNumber    │◄─────────│ name: String        │                │
│   │ lastName: String    │  through │ members: List       │                │
│   │ firstName: String   │  members │ version: long       │                │
│   │ dateOfBirth: Date   │          └──────────┬──────────┘                │
│   │ gender: Gender      │                     │                           │
│   │ maritalStatus       │                     │ 1:N                       │
│   │ nationality: String │                     │                           │
│   │ preferredLanguage   │          ┌──────────▼──────────┐                │
│   │ status: PersonStatus│          │  HOUSEHOLD_MEMBER   │                │
│   │ version: long       │          ├─────────────────────┤                │
│   └──────────┬──────────┘          │ id: UUID            │                │
│              │                     │ householdId: UUID   │                │
│              │ 1:N                 │ personId: UUID ─────┼───┐            │
│              │                     │ role: HouseholdRole │   │            │
│   ┌──────────▼──────────┐          │ validFrom: Date     │   │            │
│   │      ADDRESS        │          │ validTo: Date       │   │            │
│   ├─────────────────────┤          └─────────────────────┘   │            │
│   │ id: UUID            │                                    │            │
│   │ personId: UUID ◄────┼────────────────────────────────────┘            │
│   │ addressType         │                                                  │
│   │ street, houseNumber │                                                  │
│   │ postalCode, city    │                                                  │
│   │ canton: Canton      │                                                  │
│   │ premiumRegionId     │                                                  │
│   │ validFrom, validTo  │  ◄── Temporal Validity                          │
│   │ recordedAt          │  ◄── Bitemporal Tracking                        │
│   └─────────────────────┘                                                  │
│                                                                             │
│   ┌─────────────────────┐                                                  │
│   │   ORGANIZATION       │                                                  │
│   │   (Aggregate Root)  │                                                  │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ name: String        │                                                  │
│   │ uid: String         │                                                  │
│   │ type: OrganizationType                                                 │
│   │ vatRegistered: Bool │                                                  │
│   │ annualTurnover: Money                                                  │
│   └─────────────────────┘                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Product Context (Planned)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         PRODUCT ENTITIES                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────┐                                                  │
│   │      PRODUCT        │                                                  │
│   │   (Aggregate Root)  │                                                  │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ tenantId: UUID      │                                                  │
│   │ code: String        │                                                  │
│   │ category: KVG/VVG   │                                                  │
│   │ insuranceModel      │  ◄── Only for KVG                               │
│   │ name: LocalizedText │                                                  │
│   │ description         │                                                  │
│   │ status              │                                                  │
│   └──────────┬──────────┘                                                  │
│              │                                                              │
│              │ 1:N                                                          │
│              │                                                              │
│   ┌──────────▼──────────┐          ┌─────────────────────┐                │
│   │      TARIFF         │          │   PREMIUM_REGION    │                │
│   ├─────────────────────┤          ├─────────────────────┤                │
│   │ id: UUID            │          │ id: UUID            │                │
│   │ productId: UUID     │          │ code: String        │                │
│   │ version: String     │          │ canton: Canton      │                │
│   │ status: DRAFT/ACTIVE│          │ regionNumber: Int   │                │
│   │ validFrom, validTo  │          │ name: LocalizedText │                │
│   │ premiums: List      │          │ postalCodes: List   │                │
│   └──────────┬──────────┘          └──────────▲──────────┘                │
│              │                                │                            │
│              │ 1:N                            │                            │
│              │                                │                            │
│   ┌──────────▼──────────┐                     │                            │
│   │   PREMIUM_ENTRY     │─────────────────────┘                            │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ tariffId: UUID      │                                                  │
│   │ premiumRegionId     │                                                  │
│   │ ageGroup            │                                                  │
│   │ franchise (KVG)     │                                                  │
│   │ withAccident (KVG)  │                                                  │
│   │ gender (VVG)        │  ◄── Only for VVG with gender-based pricing     │
│   │ monthlyAmount: Money│                                                  │
│   └─────────────────────┘                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Contract Context (Planned)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         CONTRACT ENTITIES                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────┐                                                  │
│   │      POLICY         │                                                  │
│   │   (Aggregate Root)  │                                                  │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ tenantId: UUID      │                                                  │
│   │ policyNumber: String│                                                  │
│   │ policyholderId ─────┼───────────────────▶ Person                       │
│   │ status: PolicyStatus│                                                  │
│   │ billingAddressId    │                                                  │
│   │ billingFrequency    │                                                  │
│   │ preferredLanguage   │                                                  │
│   └──────────┬──────────┘                                                  │
│              │                                                              │
│              │ 1:N                                                          │
│              │                                                              │
│   ┌──────────▼──────────┐                                                  │
│   │     COVERAGE        │                                                  │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ policyId: UUID      │                                                  │
│   │ insuredPersonId ────┼───────────────────▶ Person                       │
│   │ productId ──────────┼───────────────────▶ Product                      │
│   │ tariffId            │                                                  │
│   │ status              │                                                  │
│   │ effectiveDate       │                                                  │
│   │ terminationDate     │                                                  │
│   │ franchise (KVG)     │                                                  │
│   │ withAccident (KVG)  │                                                  │
│   │ premiumRegionId     │                                                  │
│   │ ageGroup            │                                                  │
│   │ monthlyPremium      │                                                  │
│   └──────────┬──────────┘                                                  │
│              │                                                              │
│              │ 1:N                                                          │
│              │                                                              │
│   ┌──────────▼──────────┐                                                  │
│   │     MUTATION        │                                                  │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ coverageId: UUID    │                                                  │
│   │ mutationType        │                                                  │
│   │ status              │                                                  │
│   │ effectiveDate       │                                                  │
│   │ previousValue       │                                                  │
│   │ newValue            │                                                  │
│   │ reason: String      │                                                  │
│   │ createdBy: UUID     │                                                  │
│   └─────────────────────┘                                                  │
│                                                                             │
│   ┌─────────────────────┐                                                  │
│   │    EXEMPTION         │                                                  │
│   ├─────────────────────┤                                                  │
│   │ id: UUID            │                                                  │
│   │ subscriberId: UUID  │                                                  │
│   │ subscriberType      │                                                  │
│   │ domain              │                                                  │
│   │ type, reason, status│                                                  │
│   │ validFrom/validTo   │                                                  │
│   └─────────────────────┘                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Value Objects

### Common Value Objects

| Value Object | Description | Location |
|--------------|-------------|----------|
| [Money](./value-objects/money.md) | Monetary amounts with CHF rounding | `govinda-common` |
| [AhvNumber](./value-objects/ahv-number.md) | Swiss social security number | `govinda-common` |
| [LocalizedText](./value-objects/localized-text.md) | Multilingual text (DE/FR/IT/EN) | `govinda-common` |

---

## Enumerations

### Person Enums

| Enum | Values | Description |
|------|--------|-------------|
| `Gender` | MALE, FEMALE, OTHER | Biological/legal gender |
| `MaritalStatus` | SINGLE, MARRIED, DIVORCED, WIDOWED, REGISTERED_PARTNERSHIP, DISSOLVED_PARTNERSHIP | Legal marital status |
| `PersonStatus` | ACTIVE, DECEASED, EMIGRATED | Person's insurance status |
| `Language` | DE, FR, IT, EN | Preferred communication language |

### Address Enums

| Enum | Values | Description |
|------|--------|-------------|
| `AddressType` | MAIN, CORRESPONDENCE, BILLING | Purpose of address |
| `Canton` | ZH, BE, LU, ... (26 cantons) | Swiss canton |

### Household Enums

| Enum | Values | Description |
|------|--------|-------------|
| `HouseholdRole` | PRIMARY, PARTNER, CHILD | Role within household |

### Product Enums

| Enum | Values | Description |
|------|--------|-------------|
| `ProductType` | KVG, VVG | Insurance regulation type |
| `ProductCategory` | BASIC, HOSPITAL, DENTAL, ALTERNATIVE, TRAVEL, DAILY_ALLOWANCE | VVG product categories |
| `InsuranceModel` | STANDARD, HMO, HAUSARZT, TELMED | KVG insurance models |
| `ProductStatus` | ACTIVE, INACTIVE | Product availability |
| `TariffStatus` | DRAFT, ACTIVE, INACTIVE | Tariff lifecycle |
| `AgeGroup` | CHILD, YOUNG_ADULT, ADULT | Premium age categories |
| `Franchise` | CHF_0 to CHF_2500 | KVG deductible options |

### Contract Enums

| Enum | Values | Description |
|------|--------|-------------|
| `PolicyStatus` | QUOTE, PENDING, ACTIVE, SUSPENDED, CANCELLED | Policy lifecycle |
| `CoverageStatus` | ACTIVE, SUSPENDED, TERMINATED | Coverage lifecycle |
| `MutationType` | CREATE, UPDATE, CORRECTION, CANCELLATION | Type of change |
| `BillingFrequency` | MONTHLY, QUARTERLY, SEMI_ANNUAL, ANNUAL | Payment frequency |

---

## Temporal Data Model

The system implements **bitemporal history tracking** for compliance:

### Two Time Dimensions

| Dimension | Purpose | Fields |
|-----------|---------|--------|
| **Valid Time** | When fact is true in reality | `validFrom`, `validTo` |
| **Transaction Time** | When fact was recorded | `recordedAt`, `supersededAt` |

### History Pattern

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        BITEMPORAL HISTORY                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CURRENT STATE                                                             │
│   ─────────────                                                             │
│   validFrom: 2024-01-01   validTo: null (current)                          │
│   recordedAt: 2024-01-01  supersededAt: null (not replaced)                │
│                                                                             │
│   HISTORICAL STATE (superseded by correction)                               │
│   ──────────────────────────────────────────                               │
│   validFrom: 2024-01-01   validTo: null                                    │
│   recordedAt: 2023-12-01  supersededAt: 2024-01-01 (replaced!)            │
│                                                                             │
│   QUERY: "What was the state on 2024-06-15?"                               │
│   ─────────────────────────────────────────                                │
│   SELECT * FROM history                                                     │
│   WHERE validFrom <= '2024-06-15'                                          │
│     AND (validTo IS NULL OR validTo >= '2024-06-15')                       │
│     AND supersededAt IS NULL                                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Entity Documentation

### Masterdata (✅ Implemented)

- [Person](./masterdata/person.md) - Insured persons
- [Household](./masterdata/household.md) - Family groupings
- [Address](./masterdata/address.md) - Temporal addresses
- [Organization](./masterdata/organization.md) - Businesses and institutions

### Value Objects (✅ Implemented)

- [Money](./value-objects/money.md) - Monetary values
- [AhvNumber](./value-objects/ahv-number.md) - Swiss SSN
- [LocalizedText](./value-objects/localized-text.md) - Multilingual text

### Product (⏳ Planned)

- [Product](./product/product.md) - Insurance products
- [Tariff](./product/tariff.md) - Product versions
- [Premium Table](./product/premium-table.md) - Premium entries

### Contract (⏳ Planned)

- [Policy](./contract/policy.md) - Insurance contracts
- [Coverage](./contract/coverage.md) - Product subscriptions
- [Mutation](./contract/mutation.md) - Coverage changes
- [Exemption](./contract/exemption.md) - Fee exemptions and reductions

### Subscription (Cross-Domain)

- [Subscription](./subscription/subscription.md) - Generic subscription entity
- [Generic Subscription Model](../concepts/generic-subscription.md) - Cross-domain abstraction

---

## Code Locations

| Module | Package | Description |
|--------|---------|-------------|
| `govinda-common` | `net.voytrex.govinda.common.domain.model` | Shared value objects, enums |
| `govinda-masterdata` | `net.voytrex.govinda.masterdata.domain.model` | Person, Household, Address |
| `govinda-product` | `net.voytrex.govinda.product.domain.model` | Product, Tariff (planned) |
| `govinda-contract` | `net.voytrex.govinda.contract.domain.model` | Policy, Coverage (planned) |

---

*Last Updated: 2026-01-28*

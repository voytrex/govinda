# Govinda ERP - Domain Model Overview

## Executive Summary

Govinda is a multi-tenant ERP for Swiss subscription-based services, supporting:
- **Health Insurance** (KVG/VVG) - core focus
- **Broadcast Fees** (RTVG/BAKOM) - planned
- **Telecom Subscriptions** - future
- **Generic Subscriptions** - extensible

---

## Domain Model Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           GOVINDA DOMAIN MODEL                                   │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│   MASTERDATA CONTEXT              PRODUCT CONTEXT           CONTRACT CONTEXT     │
│   ══════════════════              ═══════════════           ════════════════     │
│                                                                                  │
│   ┌──────────────┐               ┌──────────────┐          ┌──────────────┐     │
│   │   Person     │               │   Product    │          │    Policy    │     │
│   │──────────────│               │──────────────│          │──────────────│     │
│   │ AHV-Nr       │               │ domain       │          │ policyHolder │     │
│   │ name         │               │ pricingModel │          │ billingPerson│     │
│   │ dateOfBirth  │               │ eligibleTypes│          │ status       │     │
│   │ circumstances│               └──────┬───────┘          └──────┬───────┘     │
│   └──────┬───────┘                      │                         │             │
│          │                       ┌──────▼───────┐          ┌──────▼───────┐     │
│   ┌──────▼───────┐               │   Tariff     │          │   Coverage   │     │
│   │  Household   │               │──────────────│          │──────────────│     │
│   │──────────────│               │ validFrom/To │          │ productId    │     │
│   │ type         │               │ premiumTable │          │ effectiveDate│     │
│   │ members[]    │               └──────────────┘          │ status       │     │
│   └──────┬───────┘                                         └──────┬───────┘     │
│          │                       ┌──────────────┐                 │             │
│   ┌──────▼───────┐               │ PricingTier  │          ┌──────▼───────┐     │
│   │ Organization │               │──────────────│          │  Exemption   │     │
│   │──────────────│               │ minTurnover  │          │──────────────│     │
│   │ UID          │               │ maxTurnover  │          │ reason       │     │
│   │ turnover     │               │ amount       │          │ validFrom/To │     │
│   │ type         │               └──────────────┘          └──────────────┘     │
│   └──────────────┘                                                              │
│                                                             ┌──────────────┐     │
│   ┌──────────────┐                                         │  Suspension  │     │
│   │BusinessPartner│                                         │──────────────│     │
│   │──────────────│                                         │ reason       │     │
│   │ partnerType  │                                         │ billingTreat │     │
│   │ category     │                                         └──────────────┘     │
│   └──────────────┘                                                              │
│                                                             ┌──────────────┐     │
│   ┌──────────────┐                                         │ PaymentArr.  │     │
│   │   Address    │                                         │──────────────│     │
│   └──────────────┘                                         │ payerType    │     │
│                                                            │ coverage%    │     │
│                                                            └──────────────┘     │
│                                                                                  │
│   BILLING CONTEXT                 DOMAIN-SPECIFIC CONTEXTS                      │
│   ═══════════════                 ════════════════════════                      │
│                                                                                  │
│   ┌──────────────┐               ┌───────────────────────────────────┐          │
│   │   Invoice    │               │ Healthcare    │ Broadcast│Telecom │          │
│   │──────────────│               │───────────────│──────────│────────│          │
│   │ positions[]  │               │ KvgRules      │ FeeRules │ (future)│          │
│   │ status       │               │ VvgRules      │ TierCalc │         │          │
│   └──────────────┘               │ PremiumCalc   │ Exempt   │         │          │
│                                  └───────────────────────────────────┘          │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Bounded Contexts

### 1. Masterdata Context
**Module**: `govinda-masterdata`

| Entity | Description | Status |
|--------|-------------|--------|
| Person | Individual with AHV-Nr, demographics | ✅ Implemented |
| PersonCircumstance | Circumstances affecting fees | 📋 Specified |
| Household | Group of persons at address | ✅ Implemented |
| HouseholdMember | Person's role in household | ✅ Implemented |
| Address | Location with validity period | ✅ Implemented |
| Organization | Legal entity (company, nonprofit) | 📋 Specified |
| BusinessPartner | Third-party payers, brokers | 📋 Specified |

**See**: [docs/domain/entities/masterdata/](./entities/masterdata/)

### 2. Product Context
**Module**: `govinda-product`

| Entity | Description | Status |
|--------|-------------|--------|
| Product | Insurance/subscription product | 📋 Specified |
| Tariff | Product version with validity | 📋 Specified |
| PremiumEntry | Premium by region/age/franchise | 📋 Specified |
| PricingTier | Tiered pricing (turnover-based) | 📋 Specified |

**See**: [docs/domain/entities/product/](./entities/product/)

### 3. Contract Context
**Module**: `govinda-contract`

| Entity | Description | Status |
|--------|-------------|--------|
| Policy | Insurance contract/subscription | 📋 Specified |
| Coverage | Active product subscription | 📋 Specified |
| Mutation | Coverage change tracking | 📋 Specified |
| Exemption | Fee exemption/reduction | 📋 Specified |
| Suspension | Temporary coverage pause | 📋 Specified |
| PaymentArrangement | Third-party payment setup | 📋 Specified |

**See**: [docs/domain/entities/contract/](./entities/contract/)

### 4. Billing Context
**Module**: `govinda-billing`

| Entity | Description | Status |
|--------|-------------|--------|
| Invoice | Billing document | ⏳ Planned |
| InvoicePosition | Line item | ⏳ Planned |
| Payment | Payment record | ⏳ Planned |

**See**: [docs/domain/entities/billing/](./entities/billing/)

---

## Service Domains

The system supports multiple regulatory/business domains:

| Domain | Regulatory Authority | Subscriber Unit | Pricing Model |
|--------|---------------------|-----------------|---------------|
| HEALTHCARE | BAG | Person | Region + Age + Franchise |
| BROADCAST | BAKOM | Household/Business | Flat / Tiered |
| TELECOM | Commercial | Person/Business | Variable |
| UTILITIES | Cantonal | Household | Usage-based |
| CUSTOM | Custom | Flexible | Flexible |

---

## Key Enumerations

### Core Enums (Implemented)
- Canton (26 Swiss cantons)
- Gender, MaritalStatus, PersonStatus
- Language, AddressType
- AgeGroup, Franchise, InsuranceModel
- HouseholdRole
- MutationType

### Extension Enums (Specified)
- **ServiceDomain**: HEALTHCARE, BROADCAST, TELECOM, UTILITIES, CUSTOM
- **SubscriberType**: INDIVIDUAL, PRIVATE_HOUSEHOLD, COLLECTIVE_HOUSEHOLD, CORPORATE_*
- **HouseholdType**: PRIVATE, ELDERLY_HOME, NURSING_HOME, PRISON, etc.
- **OrganizationType**: AG, GmbH, VEREIN, STIFTUNG, etc.
- **ExemptionReason**: EL_RECIPIENT, DEAF_BLIND, DIPLOMATIC_STATUS, etc.
- **SuspensionReason**: MILITARY_SERVICE, STUDY_ABROAD, MOVING, etc.
- **CircumstanceType**: REFUGEE, STUDENT, DISABLED, etc.

**Full specification**: [docs/planning/new-enums-specification.md](../planning/new-enums-specification.md)

---

## Cross-Domain Features

### Exemption Framework

Supports fee exemptions across all domains:

```
┌─────────────────────────────────────────────────────────┐
│                 EXEMPTION FRAMEWORK                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Person/Household/Organization                           │
│           │                                              │
│           ▼                                              │
│  ┌─────────────────┐     ┌─────────────────┐            │
│  │ Circumstance    │────►│   Exemption     │            │
│  │─────────────────│     │─────────────────│            │
│  │ EL_RECIPIENT    │     │ domain=BROADCAST│            │
│  │ DEAF_BLIND      │     │ type=FULL       │            │
│  │ DIPLOMATIC      │     │ validFrom/To    │            │
│  └─────────────────┘     └─────────────────┘            │
│                                                          │
│  Healthcare: PREMIUM_SUBSIDY (partial)                   │
│  Broadcast:  EL_RECIPIENT, DEAF_BLIND, DIPLOMATIC (full) │
│  Telecom:    STUDENT_DISCOUNT, SENIOR_DISCOUNT (partial) │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Suspension Framework

Supports temporary pauses:

```
┌─────────────────────────────────────────────────────────┐
│                 SUSPENSION FRAMEWORK                     │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Coverage/Subscription                                   │
│           │                                              │
│           ▼                                              │
│  ┌─────────────────┐                                    │
│  │   Suspension    │                                    │
│  │─────────────────│                                    │
│  │ reason          │  MILITARY_SERVICE, STUDY_ABROAD    │
│  │ type            │  FULL, PARTIAL, BILLING_ONLY       │
│  │ billingTreatment│  NO_BILLING, REDUCED_BILLING       │
│  │ effectiveFrom/To│                                    │
│  │ autoReactivate  │                                    │
│  └─────────────────┘                                    │
│                                                          │
│  Healthcare: Military, Study abroad, Hospitalization     │
│  Telecom:    Moving, Sabbatical, Military               │
│  Broadcast:  Generally not suspendable                   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Third-Party Payment

Supports complex payment arrangements:

```
┌─────────────────────────────────────────────────────────┐
│              THIRD-PARTY PAYMENT                         │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Coverage/Subscription                                   │
│           │                                              │
│           ▼                                              │
│  ┌─────────────────┐                                    │
│  │PaymentArrangement│                                    │
│  │─────────────────│                                    │
│  │ payerType       │  CANTON, EMPLOYER, BUSINESS_PARTNER│
│  │ payerId         │  (FK to payer entity)              │
│  │ arrangementType │  FULL, PARTIAL, FIXED_CONTRIBUTION │
│  │ coveragePercent │  or fixedAmount                    │
│  │ validFrom/To    │                                    │
│  └─────────────────┘                                    │
│                                                          │
│  Examples:                                               │
│  - Canton pays premium subsidy (Prämienverbilligung)    │
│  - Social services pays full premium (Sozialhilfe)       │
│  - Employer pays 50% of health insurance                │
│  - Institution pays broadcast fee for collective HH     │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## Implementation Roadmap

### Phase 1: Foundation
- ServiceDomain, SubscriberType, HouseholdType enums
- Household type extension
- Organization entity

### Phase 1A: Core Extensions (NEW)
- PersonCircumstance entity
- HouseholdMember extensions
- Suspension entity

### Phase 1B: Payment Framework (NEW)
- BusinessPartner entity
- PaymentArrangement entity

### Phase 2: Exemption Framework
- ExemptionType, ExemptionReason, ExemptionStatus enums
- Exemption entity
- Exemption validation rules

### Phase 3: Tiered Pricing
- PricingTier entity
- TierResolver service

### Phase 4: Broadcast Domain
- govinda-domain-broadcast module
- Broadcast-specific rules

### Phase 5: Telecom Domain
- govinda-domain-telecom module
- Contract terms, usage tracking

**Full plan**: [docs/planning/subscription-model-extension-plan.md](../planning/subscription-model-extension-plan.md)

---

## Key Documentation References

| Document | Location | Description |
|----------|----------|-------------|
| Gap Analysis | [docs/planning/domain-model-gap-analysis.md](../planning/domain-model-gap-analysis.md) | Identified gaps |
| Business Partner | [docs/planning/gaps/gap-01-business-partner.md](../planning/gaps/gap-01-business-partner.md) | Partner/payer model |
| Suspension | [docs/planning/gaps/gap-02-suspension.md](../planning/gaps/gap-02-suspension.md) | Suspension framework |
| Circumstances | [docs/planning/gaps/gap-03-person-circumstances.md](../planning/gaps/gap-03-person-circumstances.md) | Person circumstances |
| Enum Specs | [docs/planning/new-enums-specification.md](../planning/new-enums-specification.md) | Core enums |
| Enum Extensions | [docs/planning/new-enums-specification-extension.md](../planning/new-enums-specification-extension.md) | Additional enums |
| Radio/TV Fee | [docs/domain/concepts/radio-tv-fee.md](./concepts/radio-tv-fee.md) | RTVG domain knowledge |
| Extension Plan | [docs/planning/subscription-model-extension-plan.md](../planning/subscription-model-extension-plan.md) | Implementation roadmap |

---

## Swiss Regulatory Compliance

### Health Insurance (KVG/VVG)
- Mandatory for all Swiss residents
- Premium regions (BAG-defined)
- Franchise system
- Age-based premiums

### Broadcast Fee (RTVG)
- Mandatory for all households
- Exemptions: EL recipients, deaf-blind, diplomatic
- Collective households pay double
- Business tiers by turnover

### Data Protection (DSG)
- Bitemporal data tracking
- Audit trails
- Consent management

**See**: [docs/domain/regulatory/](./regulatory/)

---

*Last Updated: 2026-01-27*
*Version: 2.0 (Extended)*

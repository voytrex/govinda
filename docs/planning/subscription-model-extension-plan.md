# Govinda ERP - Generic Subscription Model Extension Plan

## Executive Summary

This document outlines the plan to extend the Govinda ERP from a Swiss health insurance-specific system to a generic subscription-based ERP capable of handling:

1. **Swiss Health Insurance** (KVG/VVG) - Current scope
2. **Swiss Radio/TV Fees** (RTVG/BAKOM) - New scope
3. **Telecommunication Services** (Mobile/Internet/TV) - Future scope
4. **Generic Subscription Services** - Any recurring fee model

---

## Table of Contents

1. [Business Context](#1-business-context)
2. [Research Findings](#2-research-findings)
3. [Domain Model Extension](#3-domain-model-extension)
4. [Implementation Phases](#4-implementation-phases)
5. [Feature Branches](#5-feature-branches)
6. [Risk Assessment](#6-risk-assessment)

---

## 1. Business Context

### Current State

Govinda ERP is designed for Swiss health insurance with:
- Person/Household/Address management
- Product/Tariff/Premium tables
- Policy/Coverage/Mutation tracking
- KVG-specific business rules (franchise, models, age groups)

### Target State

A generic subscription platform supporting:
- Multiple **regulatory domains** (health, telecom, media)
- Multiple **subscriber types** (individual, household, business)
- Flexible **pricing models** (flat, tiered, usage-based)
- Universal **exemption/reduction** framework
- Domain-specific **business rule engines**

### Value Proposition

| Benefit | Description |
|---------|-------------|
| **Market Expansion** | Same platform serves multiple Swiss regulatory domains |
| **Code Reuse** | Core subscription logic shared across domains |
| **Operational Efficiency** | Unified billing, payments, customer management |
| **Regulatory Expertise** | Swiss compliance built into the platform |

---

## 2. Research Findings

### 2.1 Swiss Radio/TV Fee (Serafe/BAKOM)

#### Legal Framework

| Aspect | Details |
|--------|---------|
| **Authority** | BAKOM (Federal Office of Communications) |
| **Collector** | Serafe AG (since 2019, mandate extended to 2034) |
| **Legal Basis** | RTVG (Radio and Television Act), Art. 69b Constitution |
| **Effective** | Device-independent fee since Jan 1, 2019 |

#### Fee Structure - Private Households

| Category | Annual Fee (CHF) | Notes |
|----------|------------------|-------|
| Standard Household | 335.00 | All adults jointly liable |
| Collective Household | 670.00 | Nursing homes, hostels, prisons, boarding schools |

#### Fee Structure - Businesses (ESTV)

Businesses subject to VAT with turnover >= CHF 500,000:

| Level | Turnover Range (CHF) | Annual Fee (CHF) |
|-------|----------------------|------------------|
| 1 | 500,000 - 749,999 | 160 |
| 2 | 750,000 - 1,199,999 | 235 |
| 3 | 1,200,000 - 1,699,999 | 325 |
| 4 | 1,700,000 - 2,499,999 | 460 |
| 5 | 2,500,000 - 3,599,999 | 645 |
| 6 | 3,600,000 - 5,099,999 | 905 |
| 7 | 5,100,000 - 7,299,999 | 1,270 |
| 8 | 7,300,000 - 10,399,999 | 1,785 |
| 9 | 10,400,000 - 14,999,999 | 2,505 |
| 10 | 15,000,000 - 22,999,999 | 3,315 |
| 11 | 23,000,000 - 32,999,999 | 4,935 |
| 12 | 33,000,000 - 49,999,999 | 6,925 |
| 13 | 50,000,000 - 89,999,999 | 9,725 |
| 14 | 90,000,000 - 179,999,999 | 13,665 |
| 15 | 180,000,000 - 399,999,999 | 19,170 |
| 16 | 400,000,000 - 699,999,999 | 26,915 |
| 17 | 700,000,000 - 999,999,999 | 37,790 |
| 18 | 1,000,000,000+ | 49,925 |

#### Exemptions

| Category | Requirement | Legal Basis |
|----------|-------------|-------------|
| **AHV/IV Supplement Recipients** | Receives annual supplementary benefits | RTVO Art. 61 |
| **Deaf-Blind Persons** | Medical certificate required, household-wide | RTVO Art. 61(4) |
| **Diplomatic Staff** | FDFA identity card holders | RTVO Art. 61 |

> **Note**: Since Jan 1, 2024, the "opting-out" option for device-free households was removed.

### 2.2 Comparison: Healthcare vs. Media Fees

| Aspect | Health Insurance (KVG) | Radio/TV Fee (Serafe) |
|--------|------------------------|------------------------|
| **Mandatory** | Yes (all residents) | Yes (all households) |
| **Subscriber** | Individual person | Household/Business |
| **Pricing** | Region + Age + Franchise + Model | Flat (household) or Tiered (business) |
| **Exemptions** | None (subsidies available) | AHV/IV recipients, deaf-blind |
| **Reductions** | Franchise choice, model choice | None for households |
| **Frequency** | Monthly/Quarterly/Semi/Annual | Annual (quarterly installments) |

### 2.3 Telecom Subscription Patterns

Commercial telecom subscriptions typically include:

| Component | Description |
|-----------|-------------|
| **Base Plan** | Fixed monthly fee (Mobile, Internet, TV) |
| **Add-ons** | Optional services (extra data, channels) |
| **Promotions** | Temporary discounts (first 12 months -50%) |
| **Bundles** | Combined services at reduced rate |
| **Usage Fees** | Per-unit charges (roaming, premium calls) |

---

## 3. Domain Model Extension

### 3.1 Core Abstractions

We propose the following core abstractions to generalize the subscription model:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    GENERIC SUBSCRIPTION MODEL                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   SUBSCRIBER                  SERVICE_DOMAIN              SUBSCRIPTION       │
│   ──────────                  ──────────────              ────────────       │
│   ┌─────────────┐            ┌─────────────┐            ┌─────────────┐     │
│   │ Subscriber  │            │  Domain     │            │Subscription │     │
│   │─────────────│            │─────────────│            │─────────────│     │
│   │ PERSON      │◄───────────│ HEALTHCARE  │◄───────────│ = Coverage  │     │
│   │ HOUSEHOLD   │            │ MEDIA       │            │ = Fee       │     │
│   │ BUSINESS    │            │ TELECOM     │            │ = Contract  │     │
│   └─────────────┘            └─────────────┘            └─────────────┘     │
│         │                           │                          │            │
│         │                           │                          │            │
│   ┌─────▼─────┐              ┌──────▼──────┐            ┌──────▼──────┐    │
│   │Subscriber │              │   Product   │            │ Pricing     │    │
│   │  Type     │              │─────────────│            │─────────────│    │
│   │───────────│              │ Name        │            │ FLAT        │    │
│   │INDIVIDUAL │              │ Rules       │            │ TIERED      │    │
│   │COLLECTIVE │              │ Constraints │            │ VARIABLE    │    │
│   │CORPORATE  │              └─────────────┘            │ PROMOTIONAL │    │
│   └───────────┘                                         └─────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 New/Extended Entities

#### SubscriberType (New Enum)

```java
public enum SubscriberType {
    INDIVIDUAL,          // Single person (current Person)
    PRIVATE_HOUSEHOLD,   // Family household (current Household)
    COLLECTIVE_HOUSEHOLD,// Nursing home, prison, hostel, asylum
    CORPORATE_SMALL,     // Business < 500k turnover
    CORPORATE_MEDIUM,    // Business 500k - 5M turnover
    CORPORATE_LARGE,     // Business > 5M turnover
    NONPROFIT,           // Non-profit organization
    PUBLIC_INSTITUTION   // Government, schools
}
```

#### ServiceDomain (New Enum)

```java
public enum ServiceDomain {
    HEALTHCARE,    // KVG/VVG - existing
    BROADCAST,     // RTVG/BAKOM (Radio/TV fee) - new
    TELECOM,       // Mobile/Internet/TV - future
    UTILITIES,     // Electricity/Gas/Water - future
    CUSTOM         // Generic subscription - future
}
```

#### Household Extension

```java
// Extend existing Household with type discrimination
public enum HouseholdType {
    PRIVATE,           // Standard family household
    SHARED,            // WG/Flatshare (multiple primaries)
    ELDERLY_HOME,      // Altersheim
    NURSING_HOME,      // Pflegeheim
    HOSTEL,            // Jugendherberge
    PRISON,            // Strafanstalt
    BOARDING_SCHOOL,   // Internat
    ASYLUM_CENTER,     // Asylunterkunft
    RELIGIOUS_COMMUNITY // Kloster
}

public class Household {
    // ... existing fields ...

    private HouseholdType type = HouseholdType.PRIVATE;  // NEW
    private Integer residentCount;  // NEW - for collective households
    private UUID institutionId;     // NEW - optional link to Organization
}
```

#### Organization (New Entity)

```java
@Entity
public class Organization {
    private UUID id;
    private UUID tenantId;

    private String name;
    private String uid;           // Swiss UID number (CHE-xxx.xxx.xxx)
    private OrganizationType type;

    // Business metrics for tiered pricing
    private Money annualTurnover;
    private Integer employeeCount;
    private Boolean vatRegistered;

    // Contact
    private UUID primaryContactId; // FK to Person
    private UUID billingAddressId; // FK to Address

    // Audit
    private Instant createdAt;
    private Instant updatedAt;
    private long version;
}

public enum OrganizationType {
    SOLE_PROPRIETORSHIP,    // Einzelunternehmen
    PARTNERSHIP,            // Kollektivgesellschaft
    LIMITED_COMPANY,        // GmbH
    STOCK_CORPORATION,      // AG
    COOPERATIVE,            // Genossenschaft
    ASSOCIATION,            // Verein
    FOUNDATION,             // Stiftung
    PUBLIC_INSTITUTION      // Öffentlich-rechtlich
}
```

#### Exemption/Reduction Framework

```java
@Entity
public class Exemption {
    private UUID id;
    private UUID subscriberId;         // Person, Household, or Organization
    private SubscriberType subscriberType;

    private ServiceDomain domain;      // Which service domain
    private ExemptionType type;        // What kind of exemption
    private ExemptionReason reason;    // Why exempt

    private LocalDate validFrom;
    private LocalDate validTo;         // null = indefinite

    private String certificateNumber;  // External reference
    private LocalDate verifiedAt;      // Last verification
    private UUID verifiedBy;

    private BigDecimal reductionPercent; // 0-100, null for full exemption
}

public enum ExemptionType {
    FULL,              // Complete exemption
    PARTIAL,           // Reduced fee
    TEMPORARY,         // Time-limited
    CONDITIONAL        // Subject to verification
}

public enum ExemptionReason {
    // Healthcare
    SUBSIDY_RECIPIENT,     // Premium subsidy (Prämienverbilligung)

    // Media (Serafe)
    AHV_IV_SUPPLEMENT,     // Receives EL (Ergänzungsleistungen)
    DEAF_BLIND,            // Medical condition
    DIPLOMATIC_STATUS,     // FDFA card holder

    // Telecom
    LOW_INCOME,            // Means-tested discount
    SENIOR_DISCOUNT,       // Age-based
    STUDENT_DISCOUNT,      // Education status

    // Business
    BELOW_THRESHOLD,       // Below turnover threshold
    NONPROFIT_STATUS,      // Tax-exempt organization

    // General
    PROMOTIONAL,           // Marketing campaign
    LOYALTY,               // Long-term customer
    BUNDLE_DISCOUNT        // Combined services
}
```

#### Generic Product Extension

```java
// Extend Product to support multiple domains
public class Product {
    // ... existing fields ...

    private ServiceDomain domain;              // NEW: HEALTHCARE, MEDIA, TELECOM
    private Set<SubscriberType> eligibleTypes; // NEW: Who can subscribe
    private PricingModel pricingModel;         // NEW: How pricing works
}

public enum PricingModel {
    FIXED,                // Single price for all
    AGE_BASED,            // Price varies by age (healthcare)
    REGION_BASED,         // Price varies by location (healthcare)
    TIER_BASED,           // Price varies by tier (business media fee)
    SUBSCRIBER_TYPE_BASED,// Price varies by subscriber type
    USAGE_BASED,          // Pay per use (telecom)
    COMPOSITE             // Combination of above
}
```

#### Tiered Pricing Table

```java
@Entity
public class PricingTier {
    private UUID id;
    private UUID productId;
    private UUID tariffId;

    private String tierCode;        // "TIER_1", "TIER_2", etc.
    private String tierName;        // LocalizedText

    // Qualification criteria
    private Money minTurnover;      // null = no minimum
    private Money maxTurnover;      // null = no maximum
    private Integer minEmployees;
    private Integer maxEmployees;

    // Pricing
    private Money monthlyAmount;
    private Money annualAmount;

    private LocalDate validFrom;
    private LocalDate validTo;
}
```

### 3.3 Module Structure

```
backend/
├── govinda-common/              # Shared kernel
│   ├── ServiceDomain
│   ├── SubscriberType
│   ├── PricingModel
│   ├── ExemptionType
│   └── ExemptionReason
│
├── govinda-masterdata/          # Extended masterdata
│   ├── Person (existing)
│   ├── Household (extended with HouseholdType)
│   ├── Address (existing)
│   └── Organization (NEW)
│
├── govinda-product/             # Generalized products
│   ├── Product (extended with ServiceDomain)
│   ├── Tariff (existing)
│   ├── PremiumEntry (existing - for healthcare)
│   └── PricingTier (NEW - for tiered pricing)
│
├── govinda-contract/            # Renamed to govinda-subscription?
│   ├── Policy (existing - or rename to Subscription?)
│   ├── Coverage (existing)
│   ├── Mutation (existing)
│   └── Exemption (NEW)
│
├── govinda-domain-healthcare/   # Healthcare-specific rules (NEW)
│   ├── KvgRules
│   ├── VvgRules
│   ├── FranchiseCalculator
│   └── PremiumRegionResolver
│
├── govinda-domain-broadcast/    # Broadcast fee rules (RTVG) (NEW)
│   ├── BroadcastFeeRules
│   ├── BusinessTierResolver
│   └── ExemptionValidator
│
├── govinda-billing/             # Invoice generation
│   └── (existing plans)
│
└── govinda-app/                 # Main application
```

### 3.4 Domain Model Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    EXTENDED DOMAIN MODEL                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  MASTERDATA (Extended)                                                       │
│  ─────────────────────                                                       │
│                                                                              │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                │
│  │   Person     │     │  Household   │     │ Organization │                │
│  │──────────────│     │──────────────│     │──────────────│                │
│  │ AHV-Nr       │◄────│ type         │     │ UID          │                │
│  │ Name         │     │ residentCount│     │ turnover     │                │
│  │ Birth        │     │ institutionId│────►│ employees    │                │
│  └──────┬───────┘     └──────┬───────┘     └──────┬───────┘                │
│         │                    │                    │                         │
│         └────────────────────┼────────────────────┘                         │
│                              │                                              │
│                              ▼                                              │
│                     ┌──────────────────┐                                    │
│                     │    Subscriber    │  (Abstract concept)                │
│                     │──────────────────│                                    │
│                     │ subscriberType   │                                    │
│                     │ subscriberId     │                                    │
│                     └────────┬─────────┘                                    │
│                              │                                              │
│  SUBSCRIPTION                │                                              │
│  ────────────                ▼                                              │
│                     ┌──────────────────┐     ┌──────────────────┐          │
│                     │  Subscription    │────►│    Product       │          │
│                     │──────────────────│     │──────────────────│          │
│                     │ subscriberId     │     │ domain           │          │
│                     │ productId        │     │ pricingModel     │          │
│                     │ status           │     │ eligibleTypes    │          │
│                     │ effectiveDate    │     └────────┬─────────┘          │
│                     │ monthlyAmount    │              │                     │
│                     └────────┬─────────┘              │                     │
│                              │                        │                     │
│                              │                        ▼                     │
│                     ┌────────▼─────────┐     ┌──────────────────┐          │
│                     │   Exemption      │     │   PricingTier    │          │
│                     │──────────────────│     │──────────────────│          │
│                     │ reason           │     │ minTurnover      │          │
│                     │ type             │     │ maxTurnover      │          │
│                     │ reductionPercent │     │ amount           │          │
│                     │ validFrom/To     │     └──────────────────┘          │
│                     └──────────────────┘                                    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Implementation Phases

### Phase 1: Foundation (Weeks 1-4)

**Goal**: Extend core model without breaking existing functionality

| Task | Description | Module |
|------|-------------|--------|
| 1.1 | Add `ServiceDomain` enum | govinda-common |
| 1.2 | Add `SubscriberType` enum | govinda-common |
| 1.3 | Add `HouseholdType` enum | govinda-common |
| 1.4 | Add `PricingModel` enum | govinda-common |
| 1.5 | Extend `Household` entity with type | govinda-masterdata |
| 1.6 | Create `Organization` entity | govinda-masterdata |
| 1.7 | Add `domain` field to `Product` | govinda-product |

**Migration Strategy**:
- Default `ServiceDomain.HEALTHCARE` for existing products
- Default `HouseholdType.PRIVATE` for existing households
- Default `SubscriberType.INDIVIDUAL` for existing persons

### Phase 2: Exemption Framework (Weeks 5-6)

**Goal**: Implement universal exemption/reduction system

| Task | Description | Module |
|------|-------------|--------|
| 2.1 | Add `ExemptionType` enum | govinda-common |
| 2.2 | Add `ExemptionReason` enum | govinda-common |
| 2.3 | Create `Exemption` entity | govinda-contract |
| 2.4 | Create `ExemptionRepository` | govinda-contract |
| 2.5 | Create `ExemptionService` | govinda-contract |
| 2.6 | Integrate with billing calculation | govinda-billing |

### Phase 3: Tiered Pricing (Weeks 7-8)

**Goal**: Support turnover-based pricing for businesses

| Task | Description | Module |
|------|-------------|--------|
| 3.1 | Create `PricingTier` entity | govinda-product |
| 3.2 | Create `PricingTierRepository` | govinda-product |
| 3.3 | Create `TierResolver` service | govinda-product |
| 3.4 | Integrate with premium calculation | govinda-premium |

### Phase 4: Broadcast Domain (Weeks 9-12)

**Goal**: Full RTVG/BAKOM support (Radio/TV fees)

| Task | Description | Module |
|------|-------------|--------|
| 4.1 | Create `govinda-domain-broadcast` module | NEW |
| 4.2 | Implement `BroadcastFeeRules` service | govinda-domain-broadcast |
| 4.3 | Implement business tier mapping | govinda-domain-broadcast |
| 4.4 | Implement exemption validation | govinda-domain-broadcast |
| 4.5 | Create broadcast-specific products | govinda-product |
| 4.6 | Create broadcast-specific use cases | govinda-domain-broadcast |

### Phase 5: Telecom Domain (Weeks 13-16) - Future

**Goal**: Support commercial telecom subscriptions

| Task | Description | Module |
|------|-------------|--------|
| 5.1 | Create `govinda-domain-telecom` module | NEW |
| 5.2 | Implement promotional pricing | govinda-product |
| 5.3 | Implement bundle pricing | govinda-product |
| 5.4 | Implement usage-based billing | govinda-billing |

---

## 5. Feature Branches

### Branch Naming Convention

```
feature/subscription-{phase}-{component}
```

### Planned Branches

| Branch | Phase | Description |
|--------|-------|-------------|
| `feature/subscription-1-enums` | 1 | New enumerations |
| `feature/subscription-1-household-type` | 1 | Household type extension |
| `feature/subscription-1-organization` | 1 | Organization entity |
| `feature/subscription-1-product-domain` | 1 | Product domain field |
| `feature/subscription-2-exemption` | 2 | Exemption framework |
| `feature/subscription-3-tiered-pricing` | 3 | Tiered pricing tables |
| `feature/subscription-4-broadcast-domain` | 4 | Broadcast domain module (RTVG) |
| `feature/subscription-5-telecom-domain` | 5 | Telecom domain module |

---

## 6. Risk Assessment

### Technical Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Breaking existing healthcare functionality | High | Medium | Comprehensive regression tests |
| Over-engineering generic model | Medium | High | Start simple, iterate |
| Performance impact from polymorphism | Medium | Low | Benchmark critical paths |
| Migration complexity | Medium | Medium | Incremental migration scripts |

### Business Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Regulatory changes | High | Medium | Design for extensibility |
| Scope creep | Medium | High | Strict phase gates |
| Incorrect fee calculations | High | Low | Extensive validation rules |

### Mitigation Strategies

1. **TDD Compliance**: Every change follows Red-Green-Refactor
2. **Feature Flags**: New functionality behind feature toggles
3. **Backward Compatibility**: Existing APIs remain unchanged
4. **Database Migrations**: Flyway scripts with rollback support

---

## Next Steps

1. Review and approve this plan
2. Create detailed specifications for Phase 1
3. Set up feature branches
4. Begin implementation with TDD

---

## References

### Swiss Regulations

- [BAKOM - Radio and Television Fee](https://www.bakom.admin.ch/en/electronic-media/radio-and-television-fee)
- [Serafe - Official Collection Agency](https://www.serafe.ch/en)
- [ESTV - Corporate Fee Tariffs](https://www.estv.admin.ch/estv/en/home/federal-taxes/corporate-fee-for-radio-and-television/tariff-categories.html)

### Domain Documentation

- [Govinda Domain Model](../domain/README.md)
- [Contract Use Cases](../use-cases/contract-use-cases.md)
- [Product Use Cases](../use-cases/product-use-cases.md)

---

*Created: 2026-01-27*
*Status: Draft - Pending Review*

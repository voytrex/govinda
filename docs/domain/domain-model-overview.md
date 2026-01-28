# Govinda ERP - Domain Model Overview

## Executive Summary

Govinda is a multi-tenant ERP for Swiss subscription-based services, supporting:
- **Health Insurance** (KVG/VVG) - core focus
- **Broadcast Fees** (RTVG/BAKOM) - planned
- **Telecom Subscriptions** - future
- **Generic Subscriptions** - extensible

---

## Domain Model Architecture

Govinda is organized into bounded contexts (masterdata, product, contract, billing). Each context owns its entities and rules, while shared concepts (e.g., exemptions) connect across domains.

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

---

## Cross-Domain Features

### Exemption Framework

Supports fee exemptions across domains. Circumstances determine exemption eligibility and scope.

### Suspension Framework

Supports temporary pauses of coverage or billing based on documented reasons and approval workflows.

### Third-Party Payment

Supports payment arrangements where a third party pays all or part of a subscription (e.g., canton subsidies, employers, social services).

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

## External References

- **KVG** (Krankenversicherungsgesetz)
- **KVV** (Krankenversicherungsverordnung)
- **VVG** (Versicherungsvertragsgesetz)
- **RTVG** (Radio- und Fernsehgesetz)
- **BAG** guidance and official tariff publications
- **BAKOM** fee rules and exemptions

---

*Last Updated: 2026-01-27*
*Version: 2.0 (Extended)*

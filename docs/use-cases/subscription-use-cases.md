# Generic Subscription Use Cases

## Overview

This document defines cross-domain subscription use cases that apply to healthcare, broadcast fees, and commercial subscriptions. Domain-specific rules (e.g., RTVG exemptions, KVG franchise) are handled in domain modules, while core subscription steps remain consistent.

---

## Use Case 1: Create Subscription

### Specification

**Actor**: Admin User or System

**Preconditions**:
- Subscriber exists (Person, Household, or Organization)
- Product exists and is ACTIVE
- Subscriber type is allowed for product

**Main Flow**:
1. System validates subscriber eligibility and product domain rules
2. System selects pricing model (fixed/tiered/usage/composite)
3. System calculates base fee
4. System creates subscription with ACTIVE or PENDING status
5. System schedules billing

**Postconditions**:
- Subscription created with base fee and billing schedule

**Business Rules**:
- Pricing rules depend on product pricing model
- Billing frequency may be constrained by domain (e.g., broadcast annual default)

---

## Use Case 2: Apply Reduction/Exemption

### Specification

**Actor**: User with exemption permissions

**Preconditions**:
- Subscription exists
- Exemption evidence available (if required)

**Main Flow**:
1. User submits exemption request
2. System validates reason against domain rules
3. Admin approves or rejects
4. If approved: system recalculates fee and updates billing

**Postconditions**:
- Exemption stored and reflected in net fee

**Business Rules**:
- Allowed reasons vary by service domain
- Full exemptions set net fee to zero

---

## Use Case 3: Generate Billing

### Specification

**Actor**: System (scheduled batch)

**Preconditions**:
- Active subscriptions exist
- Billing period is due

**Main Flow**:
1. System selects subscriptions due for billing
2. System applies valid exemptions
3. System generates invoice with line items
4. System sends invoice to billing module

**Postconditions**:
- Invoice created and ready for collection

---

## Mapping to Domain Rules

| Domain | Subscriber | Pricing Model | Billing |
|--------|------------|---------------|---------|
| Healthcare | Person | Region/Age/Franchise | Monthly/Quarterly/Semi/Annual |
| Broadcast | Household / Organization | Fixed / Tiered (turnover) | Annual default; quarterly option for households |
| Telecom | Person / Organization | Fixed + Usage | Monthly |

---

## Sources

- Generic subscription model: [docs/domain/concepts/generic-subscription.md](../domain/concepts/generic-subscription.md)
- Broadcast rules: [docs/domain/concepts/radio-tv-fee.md](../domain/concepts/radio-tv-fee.md)
- Billing rules: [docs/domain/concepts/billing-and-payments.md](../domain/concepts/billing-and-payments.md)

---

*Last Updated: 2026-01-28*

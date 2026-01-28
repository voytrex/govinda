# Swiss Service Domain Concepts

This section explains the fundamental concepts of Swiss regulated services covered by the Govinda ERP. The documentation is designed for both end-users learning about Swiss regulations and developers implementing the domain model.

## Service Domains

| Domain | Regulator | Description |
|--------|-----------|-------------|
| **Healthcare** | BAG | Mandatory (KVG) and supplementary (VVG) health insurance |
| **Broadcast** | BAKOM | Radio and television fees (RTVG) |
| **Telecom** | BAKOM | Telecommunications services (future) |

**Cross-domain**: [Generic Subscription Model](./generic-subscription.md)

---

## Healthcare Domain

## Learning Order

For the best understanding, read these documents in order:

| # | Document | Duration | Description |
|---|----------|----------|-------------|
| 1 | [Swiss Healthcare System](./swiss-healthcare-system.md) | 10 min | System overview and stakeholders |
| 2 | [KVG - Mandatory Insurance](./kvg-mandatory-insurance.md) | 15 min | Basic insurance everyone must have |
| 3 | [VVG - Supplementary Insurance](./vvg-supplementary-insurance.md) | 10 min | Optional additional coverage |
| 4 | [Franchise System](./franchise-system.md) | 10 min | Annual deductibles explained |
| 5 | [Premium Regions](./premium-regions.md) | 5 min | Why location affects premiums |
| 6 | [Age Groups](./age-groups.md) | 5 min | Premium categories by age |
| 7 | [Insurance Models](./insurance-models.md) | 10 min | HMO, Hausarzt, Telmed options |
| 8 | [Billing and Payments](./billing-and-payments.md) | 5 min | Payment frequencies and subsidies |

## Quick Reference

### The Two Pillars

| Pillar | Name | Legal Basis | Required? |
|--------|------|-------------|-----------|
| **Basic** | Grundversicherung | KVG (federal law) | ✅ Mandatory |
| **Supplementary** | Zusatzversicherung | VVG (private law) | ❌ Optional |

### Cost Sharing Overview

```
Medical Costs
     │
     ▼
┌─────────────────────────────────────────┐
│ 1. FRANCHISE (Jahresfranchise)          │
│    Insured pays 100% until reached      │
│    Adults: CHF 300 - 2,500              │
│    Children: CHF 0 - 600                │
└─────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────┐
│ 2. SELBSTBEHALT (Co-payment)            │
│    Insured pays 10% of remaining costs  │
│    Maximum: CHF 700/year (adults)       │
│             CHF 350/year (children)     │
└─────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────┐
│ 3. INSURANCE COVERAGE                   │
│    Insurer pays remaining costs         │
└─────────────────────────────────────────┘
```

### Premium Factors

| Factor | KVG Impact | VVG Impact |
|--------|------------|------------|
| Premium Region | ✅ Yes | ✅ Yes |
| Age Group | ✅ Yes | ✅ Yes |
| Franchise | ✅ Yes | ❌ No |
| Accident Inclusion | ✅ Yes | ❌ No |
| Insurance Model | ✅ Yes | ❌ No |
| Gender | ❌ No (unisex) | ✅ Optional |

---

## Official Resources

- [BAG - Krankenversicherung](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung.html)
- [Priminfo - Premium Comparison](https://www.priminfo.admin.ch)
- [KVG Law Text](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de)

---

## Broadcast Domain (RTVG/BAKOM)

Swiss radio and television fees are mandatory for all households and certain businesses.

| # | Document | Duration | Description |
|---|----------|----------|-------------|
| 1 | [Radio/TV Fee (RTVG)](./radio-tv-fee.md) | 15 min | Complete guide to Swiss broadcast fees |

### Quick Reference - Broadcast Fees

| Subscriber Type | Annual Fee (CHF) | Collector |
|-----------------|------------------|-----------|
| Private Household | 335.00 | Serafe AG |
| Collective Household | 670.00 | Serafe AG |
| Business (Tier 1-18) | 160 - 49,925 | ESTV |

**Billing**: Annual default; quarterly option available for private households.

### Exemptions Available

| Exemption | Requirement | Duration |
|-----------|-------------|----------|
| EL Recipients | AHV/IV supplementary benefits + application | While eligible |
| Deaf-Blind | Deaf-blind person + no other fee-liable person in household | While condition applies |
| Diplomatic | FDFA data exchange (Ordipro) | During status |

### Business Fee Tiers

```
Turnover >= CHF 500,000 → Fee liability begins
Turnover < CHF 500,000  → Exempt from corporate fee

Tier determination based on total turnover (excl. VAT)
18 tiers from CHF 160 to CHF 49,925 per year
```

---

## Official Resources

### Healthcare
- [BAG - Krankenversicherung](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung.html)
- [Priminfo - Premium Comparison](https://www.priminfo.admin.ch)
- [KVG Law Text](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de)

### Media (Serafe)
- [Serafe Official](https://www.serafe.ch/en)
- [BAKOM Fee Info](https://www.bakom.admin.ch/bakom/en/homepage/electronic-media/radio-and-television-fee.html)
- [ESTV Corporate Fee](https://www.estv.admin.ch/estv/en/home/federal-taxes/corporate-fee-for-radio-and-television.html)

---

*Last Updated: 2026-01-28*

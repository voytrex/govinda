# Swiss Healthcare Concepts

This section explains the fundamental concepts of the Swiss healthcare system. The documentation is designed for both end-users learning about Swiss health insurance and developers implementing the domain model.

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

*Last Updated: 2026-01-26*

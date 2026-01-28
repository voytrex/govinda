# Cost Sharing (Kostenbeteiligung)

## Overview

**Cost sharing** (Kostenbeteiligung) is the patient's financial participation in healthcare costs under KVG. It consists of two components: the **Franchise** (deductible) and the **Selbstbehalt** (co-payment).

> **German**: Kostenbeteiligung
> **French**: Participation aux coûts
> **Italian**: Partecipazione ai costi

---

## The Two Components

Cost sharing is calculated per person and year:

- **Franchise**: annual deductible paid first by the patient.
- **Selbstbehalt**: co-payment after the franchise is exhausted.
- **Annual reset**: limits reset at year start or coverage start.
- **Exemptions**: maternity and specific legal cases waive cost sharing.

---

## Cost Sharing Structure

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         COST SHARING STRUCTURE                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  FRANCHISE (Jahresfranchise)                                                │
│  • Patient pays 100% until exhausted                                        │
│  • Higher franchise = lower premium                                         │
│                                                                             │
│  SELBSTBEHALT (Co-payment)                                                  │
│  • 10% of remaining costs                                                   │
│  • Annual maximum applies                                                   │
│                                                                             │
│  INSURER PAYS                                                               │
│  • 100% of remaining costs after franchise + Selbstbehalt                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Annual Limits by Category

### Adults (18+)

| Component | Options | Annual Maximum |
|-----------|---------|----------------|
| **Franchise** | CHF 300, 500, 1000, 1500, 2000, 2500 | Selected amount |
| **Selbstbehalt** | 10% after franchise | **CHF 700** |
| **Combined Max** | Franchise + Selbstbehalt | CHF 300 + 700 = **CHF 1,000** (min) to CHF 2,500 + 700 = **CHF 3,200** (max) |

### Children (0-17)

| Component | Options | Annual Maximum |
|-----------|---------|----------------|
| **Franchise** | CHF 0, 100, 200, 300, 400, 500, 600 | Selected amount |
| **Selbstbehalt** | 10% after franchise | **CHF 350** |
| **Combined Max** | Franchise + Selbstbehalt | CHF 0 + 350 = **CHF 350** (min) to CHF 600 + 350 = **CHF 950** (max) |

---

## Calculation Logic (Conceptual)

1. Apply **franchise** to the approved amount until the deductible is exhausted.
2. Apply **Selbstbehalt** (10%) to the remaining amount, capped at the annual maximum.
3. Patient share = franchise applied + Selbstbehalt applied.
4. Insurer pays the remainder.

---

## Worked Example

```
Adult, CHF 1,500 franchise, CHF 800 already used.
Approved claim: CHF 1,000

Franchise remaining: CHF 700
Selbstbehalt remaining: CHF 700

Patient pays: CHF 700 (franchise) + CHF 30 (10% of remaining CHF 300) = CHF 730
Insurer pays: CHF 270
```

---

## Exemptions and Special Cases

### Full Exemptions

- **Maternity** (from week 13 to 8 weeks postnatal)
- Specific services defined by regulation

### Family Cap (Children)

Children's Selbstbehalt is capped per family (2× child max), so after the cap is reached, additional child co-payments stop for the year.

---

## Timing Rules

- **Service date** determines which annual account is used.
- **Coverage start** sets the first tracking period (no pro‑rating of limits).
- **Yearly reset** applies at the start of the calendar year.

---

## Legal References

- **KVG Art. 64** (cost sharing)
- **KVV Art. 103** (children's cost sharing)
- **BAG** regulations and annual premium guidance

---

## Related Documentation

- [Franchise System](./franchise-system.md) - Detailed franchise options
- [CostSharingAccount Entity](../entities/contract/cost-sharing-account.md) - Account tracking
- [Claim Entity](../entities/claims/claim.md) - Claim with cost sharing
- [KVG Mandatory Insurance](./kvg-mandatory-insurance.md) - Insurance context

---

*Last Updated: 2026-01-28*

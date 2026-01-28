# CostSharingAccount Entity

## Overview

The **CostSharingAccount** (Kostenbeteiligungskonto) tracks a person's annual franchise and Selbstbehalt usage for KVG coverage.

> **German**: Kostenbeteiligungskonto, Franchisekonto
> **Module**: `govinda-contract`
> **Status**: Planned

**Resets**: January 1st each year (or coverage start date if later)

---

## Key Data

- Coverage period (annual tracking)
- Franchise selection and usage
- Selbstbehalt usage and caps
- Cumulative patient share and insurer share

---

## Business Rules (Conceptual)

- Applies franchise before Selbstbehalt for approved claims.
- Resets annually and on coverage start.
- Special cases (maternity, accident) can waive or alter cost sharing.

---

## Annual Limits (Conceptual)

- **Adults**: franchise CHF 300–2,500; Selbstbehalt max CHF 700
- **Children**: franchise CHF 0–600; Selbstbehalt max CHF 350
- **Family cap**: children’s Selbstbehalt capped at 2× child max

---

## Timing Rules

- **Service date** determines the year of the account
- **Coverage start** begins the first tracking period
- **Yearly reset** applies on Jan 1 (no pro‑rating)

---

## Legal References

- **KVG Art. 64** (cost sharing)
- **KVV Art. 103** (children’s cost sharing)
- **BAG** guidance on franchise options and limits

---

## Related Documentation

- [Coverage](./coverage.md) - KVG coverage with franchise
- [Claim](../claims/claim.md) - Individual claim with cost sharing
- [Cost Sharing Concept](../../concepts/cost-sharing.md) - Business rules

---

*Last Updated: 2026-01-28*

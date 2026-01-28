# PersonCircumstance Entity

## Overview

The **PersonCircumstance** entity tracks circumstances that affect a person's insurance fees, exemptions, or eligibility.

> **German**: Personenumstand
> **Module**: `govinda-masterdata`
> **Status**: ⏳ Planned

**Key Insight**: Circumstances are separate from PersonStatus (lifecycle). A person can have multiple concurrent circumstances.

**Examples**:
- EL recipient (EL-Bezüger) - broadcast fee exemption
- Deaf-blind (Taubblind) - broadcast fee exemption
- Refugee (Flüchtling) - special fee handling
- Military service (Militärdienst) - suspension eligibility

---

## Key Data

- Circumstance type and category
- Validity period and verification status
- Supporting evidence and issuing authority

---

## Business Rules (Conceptual)

- Multiple circumstances can be active at the same time.
- Circumstances influence eligibility for exemptions or suspensions.
- Some circumstances require periodic re-verification.

---

## Examples and Effects

- **EL recipient**: often grants RTVG exemption for the household
- **Deaf‑blind**: may grant full RTVG exemption
- **Refugee status**: affects billing and documentation needs
- **Military service**: enables suspension requests

---

## Verification

- Verification status must be set before applying exemptions.
- Re‑verification intervals depend on the circumstance type.
- Evidence is retained for audit and compliance.

---

## Legal References

- **RTVG** exemptions linked to verified circumstances
- **KVG/KVV** rules for eligibility and mandatory insurance
- **Cantonal decisions** that confirm status

---

## Related Documentation

- [Person](./person.md) - Parent entity
- [Exemption](../contract/exemption.md) - Resulting exemptions
- [Radio/TV Fee Concept](../../concepts/radio-tv-fee.md) - Exemption rules

---

*Last Updated: 2026-01-28*

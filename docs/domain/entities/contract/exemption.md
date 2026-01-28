# Exemption Entity

## Overview

The **Exemption** (Befreiung) entity tracks exemptions from mandatory fees or insurance obligations.

> **German**: Befreiung, Gebührenbefreiung
> **Module**: `govinda-contract`
> **Status**: Planned

**Common Uses**: RTVG (broadcast fee) exemptions for EL recipients, deaf-blind persons, diplomatic immunity

---

## Key Data

- Target (person or household) and fee type
- Exemption reason and verification status
- Validity period and current status
- Supporting evidence and external references

---

## Business Rules (Conceptual)

- Exemptions can be full or partial depending on reason and fee.
- Verification and validity determine whether billing is suppressed.
- Exemptions may be revoked or expire based on lifecycle events.

---

## Typical Scenarios

- **RTVG**: EL recipients, deaf/blind, diplomatic status
- **KVG**: military service, equivalent foreign coverage
- **Institutional living**: collective billing arrangements

---

## Verification and Evidence

- Evidence (certificates, decisions) is required for most exemptions.
- Validity periods define when the exemption applies.
- Revocation ends billing suppression immediately.

---

## Legal References

- **RTVG** exemption rules (households and persons)
- **KVG/KVV** exemptions for mandatory insurance
- **Cantonal authority** decisions and certificates

---

## Related Documentation

- [PersonCircumstance](../masterdata/person-circumstance.md) - Source circumstances
- [Household](../masterdata/household.md) - Household entity
- [RTVG Concept](../../concepts/radio-tv-fee.md) - Broadcast fee rules
- [Subscription](../subscription/subscription.md) - Cross-domain subscription entity

---

*Last Updated: 2026-01-28*

# Suspension Entity

## Overview

The **Suspension** entity represents a temporary pause of insurance coverage or billing.

> **German**: Sistierung
> **Module**: `govinda-contract`
> **Status**: ⏳ Planned

**Common Scenarios**:
- Military service (Militärdienst)
- Study abroad (Auslandstudium)
- Extended foreign travel
- Moving/relocation transition

---

## Key Data

- Coverage and person reference
- Suspension reason and type
- Effective dates and duration
- Billing treatment during suspension
- Status and reactivation behavior

---

## Business Rules (Conceptual)

- Suspension may require documentation and approval depending on reason.
- Billing treatment depends on the suspension type and reason.
- Suspensions can end on a date or be manually reactivated.

---

## Typical Reasons

- **Military/Civil service** with official orders
- **Study abroad** with enrollment confirmation
- **Temporary relocation** with evidence of residence
- **Long-term care** with medical or institutional proof

---

## Billing Treatment

- **No billing** when coverage is fully paused
- **Reduced billing** when only parts of coverage are suspended
- **Deferred billing** when payments resume at reactivation

---

## Legal References

- **KVG/KVV** provisions for suspension and coverage obligations
- **Cantonal rules** for documentation requirements

---

## Related Documentation

- [Coverage](./coverage.md) - Parent entity
- [PersonCircumstance](../masterdata/person-circumstance.md) - Circumstances

---

*Last Updated: 2026-01-28*

# Claim Entity

## Overview

The **Claim** (Leistung) entity represents an approved healthcare service with cost sharing applied.

> **German**: Leistung, Leistungsabrechnung
> **Module**: `govinda-claims` (planned)
> **Status**: ⏳ Planned

**Flow**: Provider Invoice → Validation → Approval → **Claim** → Cost Sharing → Payment

---

## Key Data

- Claim identifier and source invoice reference
- Patient and coverage reference
- Provider information (GLN, name)
- Service details (date, tariff type/code, diagnosis)
- Amounts (invoiced, approved, patient share, insurer share)
- Payment model (tiers payant vs tiers garant)
- Status and medical flags (maternity, accident)

---

## Business Rules (Conceptual)

- Cost sharing is applied after approval and depends on franchise usage and Selbstbehalt.
- Maternity claims are exempt from cost sharing.
- Payment model determines whether the insurer pays the provider directly.

---

## Lifecycle and Status

- **Pending**: created from approved invoice lines
- **Processing**: cost sharing and validations applied
- **Approved/Adjusted/Rejected**: final validation outcome
- **Ready for payment**: payment initiated
- **Paid/Closed**: settled and archived

---

## Relationships

- **Source**: `ProviderInvoice` and its line items
- **Coverage**: the active policy/coverage at service date
- **Cost Sharing**: updates `CostSharingAccount` for the service year

---

## Compliance and References

- **KVG/KVV** provisions for benefit processing and cost sharing
- **BAG** tariff rules (TARMED/TARDOC/SwissDRG)
- **Forum Datenaustausch** invoice standards for inbound data

---

## Related Documentation

- [ProviderInvoice](./provider-invoice.md) - Source invoice
- [CostSharingAccount](../contract/cost-sharing-account.md) - Franchise tracking
- [PatientInvoice](./patient-invoice.md) - Patient bill
- [Cost Sharing Concept](../../concepts/cost-sharing.md) - Business rules

---

*Last Updated: 2026-01-28*

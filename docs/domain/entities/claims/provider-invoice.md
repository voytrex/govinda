# ProviderInvoice Entity

## Overview

The **ProviderInvoice** (Rechnungseingang) represents an invoice received from a healthcare provider for services rendered.

> **German**: Rechnungseingang, Leistungserbringerrechnung
> **Module**: `govinda-claims` (planned)
> **Status**: Planned

**Flow**: Provider submits invoice (XML/EDI) -> Validation -> Approval -> Claims generated

---

## Key Data

- Provider, patient, and coverage references
- Invoice dates, treatment period, and treatment type
- Tariff positions and amounts per line
- Submission method and processing status
- Validation outcome and claim linkage

---

## Processing Summary

- Validate technical format and patient/coverage eligibility.
- Validate medical and tariff content; approve, adjust, or reject lines.
- Generate claims for approved lines and apply cost sharing.

---

## Typical Outcomes

- **Approved**: all lines accepted
- **Adjusted**: some lines corrected or reduced
- **Rejected**: invoice/line not payable

---

## Relationships

- **Provider** identified by GLN and master data
- **Patient/Coverage** matched by insurance identifiers
- **Claims** generated per approved invoice line

---

## Compliance and References

- **Forum Datenaustausch** XML standard for invoice exchange
- **BAG** tariff rules for validation and pricing
- **KVG/KVV** billing and benefit processing requirements

---

## Related Documentation

- [Claim](./claim.md) - Generated claims
- [PatientInvoice](./patient-invoice.md) - Cost sharing invoice to patient
- [Claims Processing Concept](../../concepts/claims-processing.md) - Business rules

---

*Last Updated: 2026-01-28*

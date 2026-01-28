# Claims Processing (Leistungsabrechnung)

## Overview

**Claims processing** covers the end-to-end workflow from receiving a healthcare provider invoice to paying benefits and billing the patient for their cost sharing.

> **German**: Leistungsabrechnung, Leistungsverarbeitung
> **French**: Traitement des prestations
> **Italian**: Elaborazione delle prestazioni

---

## High-Level Process Flow

Claims processing covers:

- Inbound provider invoices (XML/EDI/manual)
- Validation (technical, business, medical plausibility)
- Approval and claim creation
- Cost sharing calculation and billing
- Payment to provider or reimbursement to patient

```
RECEIVE → VALIDATE → APPROVE → CLAIM CREATED → COST SHARING → PAYMENT
```

---

## Payment Models

Tiers payant means the insurer pays the provider directly; tiers garant means the patient pays first and is reimbursed. The payment model determines the billing and settlement path.

| Model | Primary Payer | Typical Use |
|-------|---------------|-------------|
| **Tiers payant** | Insurer → Provider | Hospital/inpatient, high costs |
| **Tiers garant** | Patient → Provider | Ambulatory, smaller claims |

---

## Invoice Channels

Invoices arrive via Forum Datenaustausch (XML), EDI, or manual entry. Channel drives the required validations and exception handling.

- **Forum Datenaustausch XML**: standard electronic exchange
- **EDI**: legacy batch processing
- **Manual**: paper/PDF capture with extra review

---

## Validation Stages

Validation happens in stages: technical format checks, business rules (provider/coverage/tariff), and medical plausibility.

### Stage 1: Technical
- XML schema and required fields
- GLN/AHV formats and date consistency

### Stage 2: Business
- Provider authorization and tariff validity
- Coverage active for treatment date
- Duplicate invoice detection

### Stage 3: Medical Plausibility
- Rule engine checks for combinations, quantities, age/gender fit

---

## Tariff Systems (Overview)

- **TARMED** (ambulatory, until replaced)
- **TARDOC** (ambulatory successor)
- **SwissDRG** (inpatient, case-based)

These tariffs define codes, prices, and validation constraints.

---

## Status Outcomes

- **Approved**: accepted as submitted
- **Adjusted**: reduced or corrected amounts
- **Rejected**: invoice/line not payable

---

## Error Handling (Typical)

| Error | Meaning | Typical Resolution |
|------|---------|--------------------|
| GLN not found | Provider unknown | Verify provider registry |
| Coverage inactive | No active policy | Check coverage dates |
| Tariff invalid | Code not valid | Correct or reject |
| Duplicate | Already processed | Reject or merge |

---

## Legal and Industry References

- **KVG/KVV** requirements for benefit processing
- **BAG** tariff guidance and premium region rules
- **Forum Datenaustausch** (XML invoice standard)
- **SwissDRG / TARMED / TARDOC** tariff governance

---

## Related Documentation

- [ProviderInvoice Entity](../entities/claims/provider-invoice.md) - Invoice details
- [Claim Entity](../entities/claims/claim.md) - Claim details
- [PatientInvoice Entity](../entities/claims/patient-invoice.md) - Patient billing
- [Cost Sharing](./cost-sharing.md) - Cost sharing rules
- [CostSharingAccount Entity](../entities/contract/cost-sharing-account.md) - Tracking

---

*Last Updated: 2026-01-28*

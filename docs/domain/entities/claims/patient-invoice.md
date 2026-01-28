# PatientInvoice Entity

## Overview

The **PatientInvoice** (Patientenrechnung) represents a bill sent to the patient for their cost sharing (franchise and Selbstbehalt).

> **German**: Patientenrechnung, Kostenbeteiligung
> **Module**: `govinda-claims` (planned)
> **Status**: Planned

**Flow**: Claims with patient share -> PatientInvoice generated -> Payment or Dunning

---

## Key Data

- Patient, coverage, and billing period
- Totals for franchise, Selbstbehalt, and patient share
- Payment references and delivery method
- Status, paid/open balance, and dunning stage
- Optional third-party payer contribution

---

## Processing Summary

- Aggregate approved claims with patient share into an invoice period.
- Generate payment references and deliver the invoice.
- Track incoming payments and trigger dunning when overdue.

---

## Lifecycle

- **Draft**: invoice prepared, not sent
- **Sent**: delivered via postal/eBill/portal
- **Partially paid / Paid**: settlement progress
- **Overdue / Dunning**: reminders and collection steps
- **Written off**: uncollectible after process

---

## Third-Party Payments

Patient invoices can be partially or fully covered by:

- Cantonal premium subsidies (IPV)
- Social services or employers
- Institutional arrangements

---

## Compliance and References

- **Swiss QR-bill** standards for payment references
- **CAMT.054** for payment reconciliation
- **Debt collection** procedures under Swiss law

---

## Related Documentation

- [Claim](./claim.md) - Source claims
- [CostSharingAccount](../contract/cost-sharing-account.md) - Franchise tracking
- [BusinessPartner](../masterdata/business-partner.md) - IPV payers
- [Dunning Concept](../../concepts/dunning-collection.md) - Collection process

---

*Last Updated: 2026-01-28*

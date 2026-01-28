# BusinessPartner Entity

## Overview

The **BusinessPartner** entity represents third parties who interact with the insurance system as payers, intermediaries, or service providers.

> **German**: Geschäftspartner
> **Module**: `govinda-masterdata`
> **Status**: ⏳ Planned

**Examples**:
- Cantons paying premium subsidies (IPV)
- Social services (Sozialhilfe) paying premiums
- Employers contributing to employee premiums
- Brokers and agents

---

## Key Data

- Partner type (canton, employer, broker, social services, etc.)
- Role in payments (payer, subsidizer, intermediary)
- Legal identity and contact channels
- Banking details for payments
- Lifecycle status

---

## Relationships

- One business partner can be linked to many payment arrangements.
- Used as third-party payer for coverages or households.

---

## Typical Roles

- **Payer**: covers premiums or invoices on behalf of a person/household
- **Subsidizer**: contributes part of the premium (e.g., canton)
- **Intermediary**: brokers or agents
- **Provider**: service provider for claims or billing

---

## Reference Data

- **UID** and **GLN** identifiers for Swiss entities
- Banking details for settlements and refunds
- Official addresses for notifications

---

## Legal References

- **Swiss UID register** for legal entity identification
- **GLN standards** for healthcare providers
- **Cantonal subsidy programs** for IPV payments

---

## Related Documentation

- [PaymentArrangement](../contract/payment-arrangement.md) - Payment setup
- [PremiumSubsidy](../contract/premium-subsidy.md) - IPV tracking
- [Person](./person.md) - Insured person

---

*Last Updated: 2026-01-28*

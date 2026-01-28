# ADR-004: Generic Subscription Core

## Status
**Accepted**

## Date
2026-01-28

## Context

Govinda is expanding beyond Swiss healthcare into other regulated subscription domains (broadcast fees, telecom). These domains share recurring billing, subscriber identity, pricing, and exemption/reduction concepts, but differ in legal rules and pricing models.

We need a shared subscription core that:
- Preserves domain-specific rules (KVG/VVG, RTVG/RTVO, commercial terms)
- Avoids duplication of billing, exemptions, and subscriber handling
- Keeps DDD boundaries clear and maintainable

## Decision

We introduce a **generic subscription core** across domains:

- **Shared abstractions**:
  - Subscriber (Person, Household, Organization)
  - Subscription (recurring fee obligation)
  - Product (domain + pricing model)
  - PricingModel (fixed, tiered, usage-based, composite)
  - Exemption/Reduction (applied to subscription)

- **Domain rules stay in domain modules**:
  - Healthcare: franchise, cost sharing, premium regions
  - Broadcast: household vs corporate fee rules, legal exemptions
  - Telecom: usage-based charges, commercial promotions

- **Mapping**:
  - Healthcare Policy/Coverage maps to Subscription for billing and reduction handling
  - Broadcast household/corporate fees map to Subscription for uniform billing

## Consequences

### Positive
- Reuse of billing, exemption, and subscriber logic across domains
- Consistent modeling for new subscription services
- Clear separation between core and domain-specific rules

### Negative
- Requires careful mapping from existing healthcare entities to generic subscription concepts
- Additional complexity in product configuration (domain + pricing model + eligible subscriber types)

### Risks
- Overgeneralization could obscure domain-specific invariants
- Mitigation: keep domain services explicit and validate domain rules at boundaries

## Alternatives Considered

| Alternative | Reason for Rejection |
|-------------|----------------------|
| Separate subscription systems per domain | Duplicates billing/exemption logic |
| Full event sourcing for subscriptions | Too heavy for current scope |
| Keep only healthcare-specific contracts | Blocks broadcast/telecom expansion |

## References

- [Generic Subscription Model](../../domain/concepts/generic-subscription.md)
- [Domain Model Overview](../../domain/domain-model-overview.md)
- [Broadcast Fee Concept](../../domain/concepts/radio-tv-fee.md)

# Subscription Entity

## Overview

The **Subscription** entity represents a recurring fee obligation for a subscriber (person, household, or organization). It provides a generic contract model for multiple domains while allowing domain-specific rules to remain in their respective modules.

> **Module**: `govinda-contract` (or future `govinda-subscription`)
> **Status**: Planned

---

## Entity Definition

```java
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID subscriberId; // Person, Household, or Organization

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriberType subscriberType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceDomain serviceDomain;

    @Column(nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingFrequency billingFrequency;

    @Embedded
    private Money baseAnnualAmount;  // before reductions

    @Embedded
    private Money reductionAmount;   // total reductions applied

    @Embedded
    private Money netAnnualAmount;   // after reductions

    @Column
    private String pricingTierCode;  // e.g., TIER_1 (tiered pricing)

    @Column
    private String promotionCode;    // optional promotion reference

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `subscriberId` | UUID | ✅ | Person/Household/Organization |
| `subscriberType` | SubscriberType | ✅ | INDIVIDUAL, PRIVATE_HOUSEHOLD, COLLECTIVE_HOUSEHOLD, CORPORATE |
| `serviceDomain` | ServiceDomain | ✅ | HEALTHCARE, BROADCAST, TELECOM |
| `productId` | UUID | ✅ | Product reference |
| `status` | SubscriptionStatus | ✅ | ACTIVE, SUSPENDED, TERMINATED |
| `effectiveDate` | LocalDate | ✅ | Start date |
| `terminationDate` | LocalDate | ❌ | End date |
| `billingFrequency` | BillingFrequency | ✅ | Domain-dependent |
| `baseAnnualAmount` | Money | ✅ | Base annual fee |
| `reductionAmount` | Money | ❌ | Total reductions applied |
| `netAnnualAmount` | Money | ✅ | Net annual fee after reductions |
| `pricingTierCode` | String | ❌ | Tier identifier for tiered pricing |
| `promotionCode` | String | ❌ | Promotion applied to subscription |

---

## SubscriptionStatus Enum

```java
public enum SubscriptionStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    TERMINATED
}
```

---

## Domain Mapping

| Domain | Subscription Mapping |
|--------|----------------------|
| Healthcare | Policy + Coverage per insured person |
| Broadcast | Household or Organization fee |
| Telecom | Service contract per customer |

---

## Billing Constraints (By Domain)

| Domain | Allowed Frequencies | Notes |
|--------|---------------------|-------|
| Healthcare | Monthly / Quarterly / Semi-Annual / Annual | Discounts may apply |
| Broadcast | Annual (default) | Quarterly option for households; corporate fees annual |
| Telecom | Monthly | Usage-based line items possible |

---

## Pricing and Reductions

- `baseAnnualAmount` is derived from the pricing model and product/tariff.
- `pricingTierCode` captures turnover-based tiers (broadcast corporate fees).
- `promotionCode` references marketing or commercial discounts.
- `reductionAmount` and `netAnnualAmount` reflect applied exemptions/reductions.

---

## Subscription Adjustments (Planned)

Adjustments capture time-bound reductions or surcharges applied to a subscription, such as promotions, loyalty discounts, or one-time credits.

```java
@Entity
@Table(name = "subscription_adjustments")
public class SubscriptionAdjustment {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType type; // DISCOUNT, SURCHARGE, CREDIT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentReason reason;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column
    private LocalDate validTo; // null = open-ended

    @Column
    private String promotionCode;
}
```

### AdjustmentType Enum

```java
public enum AdjustmentType {
    DISCOUNT,
    SURCHARGE,
    CREDIT
}
```

### AdjustmentReason Enum (Code-Only)

```java
public enum AdjustmentReason {
    PROMOTIONAL,
    LOYALTY,
    BUNDLE_DISCOUNT,
    EMPLOYEE_DISCOUNT,
    HARDSHIP,
    MANUAL_CREDIT
}
```

> **i18n note**: Enum values are code-only. User-facing labels must be resolved via `MessageSource`.

---

## Related Documentation

- [Generic Subscription Model](../../concepts/generic-subscription.md)
- [Exemption Entity](./exemption.md)
- [Product Entity](../product/product.md)
- [Billing and Payments](../../concepts/billing-and-payments.md)
- [Radio/TV Fee (RTVG)](../../concepts/radio-tv-fee.md)

---

*Last Updated: 2026-01-28*

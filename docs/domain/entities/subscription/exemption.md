# Exemption Entity

## Overview

The **Exemption** entity represents reductions or waivers of subscription fees based on regulatory, promotional, or circumstantial conditions. This is a cross-domain entity that supports exemptions for healthcare, media fees, and other subscription services.

> **German**: Befreiung / Ermässigung
> **Module**: `govinda-contract` (or `govinda-subscription`)
> **Status**: Planned

---

## Entity Definition

```java
@Entity
@Table(name = "exemptions")
public class Exemption {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    // Subscriber reference (polymorphic)
    @Column(nullable = false)
    private UUID subscriberId;          // Person, Household, or Organization ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriberType subscriberType;

    // Scope
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceDomain domain;       // HEALTHCARE, BROADCAST, TELECOM

    @Column
    private UUID subscriptionId;        // Specific subscription, or null for all

    // Exemption details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExemptionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExemptionReason reason;

    @Column(precision = 5, scale = 2)
    private BigDecimal reductionPercent; // 0-100, null for full exemption

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "fixed_reduction_amount"))
    private Money fixedReductionAmount;  // Alternative: fixed amount off

    // Validity
    @Column(nullable = false)
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;          // null = indefinite

    // Verification
    @Column
    private String certificateNumber;   // External reference (EL number, etc.)

    @Column
    private String certificateIssuer;   // Issuing authority

    @Column
    private LocalDate certificateDate;  // Issue date

    @Column
    private LocalDate verifiedAt;       // Last verification

    @Column
    private UUID verifiedBy;            // User who verified

    @Column
    private LocalDate nextVerificationDue; // When to re-verify

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExemptionStatus status = ExemptionStatus.PENDING;

    @Column
    private String rejectionReason;

    // Audit
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private UUID createdBy;

    @Version
    private long version;
}
```

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | Auto | Unique identifier |
| `tenantId` | UUID | Yes | Multi-tenant isolation |
| `subscriberId` | UUID | Yes | Reference to Person/Household/Organization |
| `subscriberType` | SubscriberType | Yes | Type of subscriber |
| `domain` | ServiceDomain | Yes | Which service domain |
| `subscriptionId` | UUID | No | Specific subscription (null = all) |
| `type` | ExemptionType | Yes | Full/Partial/Temporary |
| `reason` | ExemptionReason | Yes | Why exempt |
| `reductionPercent` | BigDecimal | No | Percentage reduction (0-100) |
| `fixedReductionAmount` | Money | No | Fixed amount reduction |
| `validFrom` | LocalDate | Yes | When exemption starts |
| `validTo` | LocalDate | No | When exemption ends (null = indefinite) |
| `certificateNumber` | String | No | External reference |
| `certificateIssuer` | String | No | Issuing authority |
| `certificateDate` | LocalDate | No | Certificate issue date |
| `verifiedAt` | LocalDate | No | Last verification date |
| `verifiedBy` | UUID | No | Verifying user |
| `nextVerificationDue` | LocalDate | No | Re-verification deadline |
| `status` | ExemptionStatus | Yes | Current status |
| `rejectionReason` | String | No | Why rejected |
| `createdAt` | Instant | Auto | Creation timestamp |
| `updatedAt` | Instant | Auto | Last modification |
| `createdBy` | UUID | No | Creating user |
| `version` | long | Auto | Optimistic locking |

---

## ExemptionType Enum

```java
public enum ExemptionType {
    FULL,           // 100% exemption, no fee
    PARTIAL,        // Reduced fee (percentage or fixed amount)
    TEMPORARY,      // Time-limited exemption
    CONDITIONAL     // Subject to ongoing verification
}
```

| Type | Description | Example |
|------|-------------|---------|
| `FULL` | Complete waiver | Deaf-blind Serafe exemption |
| `PARTIAL` | Reduced amount | 50% student discount |
| `TEMPORARY` | Time-limited | 12-month promotional |
| `CONDITIONAL` | Requires verification | EL recipient (3-year cycles) |

---

## ExemptionReason Enum

```java
public enum ExemptionReason {

    // Healthcare (KVG/VVG)
    PREMIUM_SUBSIDY,

    // Media (Serafe/BAKOM)
    AHV_IV_SUPPLEMENT,
    DEAF_BLIND,
    DIPLOMATIC_STATUS,

    // Telecom
    LOW_INCOME,
    SENIOR_DISCOUNT,
    STUDENT_DISCOUNT,
    DISABILITY_DISCOUNT,

    // Business
    BELOW_THRESHOLD,
    NONPROFIT_STATUS,
    STARTUP_DISCOUNT,

    // General/Promotional
    PROMOTIONAL,
    LOYALTY,
    BUNDLE_DISCOUNT,
    REFERRAL,
    EMPLOYEE_DISCOUNT,
    HARDSHIP
}
```

> **i18n note**: Enum values are code-only. User-facing translations must be resolved via `MessageSource` using translation keys (per project i18n rules).

---

## ExemptionStatus Enum

```java
public enum ExemptionStatus {
    PENDING,        // Awaiting verification
    APPROVED,       // Active and valid
    REJECTED,       // Application denied
    EXPIRED,        // Past validTo date
    SUSPENDED,      // Temporarily suspended
    REVOKED         // Permanently cancelled
}
```

---

## Key Behaviors

### Check if Active

```java
public boolean isActive() {
    return status == ExemptionStatus.APPROVED &&
           isValidOn(LocalDate.now());
}

public boolean isValidOn(LocalDate date) {
    boolean afterStart = !date.isBefore(validFrom);
    boolean beforeEnd = validTo == null || !date.isAfter(validTo);
    return afterStart && beforeEnd;
}
```

### Calculate Reduction

```java
public Money calculateReduction(Money originalAmount) {
    if (type == ExemptionType.FULL) {
        return originalAmount; // Full exemption
    }

    if (fixedReductionAmount != null) {
        return fixedReductionAmount.min(originalAmount);
    }

    if (reductionPercent != null) {
        return originalAmount.multiply(reductionPercent.divide(new BigDecimal("100")));
    }

    return Money.ZERO;
}

public Money applyTo(Money originalAmount) {
    Money reduction = calculateReduction(originalAmount);
    return originalAmount.subtract(reduction);
}
```

### Verification Required

```java
public boolean requiresVerification() {
    if (type != ExemptionType.CONDITIONAL) {
        return false;
    }
    if (nextVerificationDue == null) {
        return false;
    }
    return !LocalDate.now().isBefore(nextVerificationDue);
}
```

### Approve Exemption

```java
public void approve(UUID approvedBy) {
    if (status != ExemptionStatus.PENDING) {
        throw new IllegalStateException("Can only approve pending exemptions");
    }
    this.status = ExemptionStatus.APPROVED;
    this.verifiedAt = LocalDate.now();
    this.verifiedBy = approvedBy;
    this.updatedAt = Instant.now();

    // Set next verification for conditional exemptions
    if (type == ExemptionType.CONDITIONAL && reason == ExemptionReason.AHV_IV_SUPPLEMENT) {
        this.nextVerificationDue = LocalDate.now().plusYears(3);
    }
}
```

### Reject Exemption

```java
public void reject(UUID rejectedBy, String reason) {
    if (status != ExemptionStatus.PENDING) {
        throw new IllegalStateException("Can only reject pending exemptions");
    }
    this.status = ExemptionStatus.REJECTED;
    this.rejectionReason = reason;
    this.verifiedAt = LocalDate.now();
    this.verifiedBy = rejectedBy;
    this.updatedAt = Instant.now();
}
```

---

## Domain-Specific Rules

### Broadcast (RTVG) Fee Exemptions

```java
public class BroadcastExemptionRules {

    public void validateForBroadcast(Exemption exemption) {
        // Only certain reasons allowed for broadcast domain
        Set<ExemptionReason> allowedReasons = Set.of(
            ExemptionReason.AHV_IV_SUPPLEMENT,
            ExemptionReason.DEAF_BLIND,
            ExemptionReason.DIPLOMATIC_STATUS
        );

        if (!allowedReasons.contains(exemption.getReason())) {
            throw new ValidationException("Invalid exemption reason for broadcast domain");
        }

        // EL requires certificate
        if (exemption.getReason() == ExemptionReason.AHV_IV_SUPPLEMENT) {
            if (exemption.getCertificateNumber() == null) {
                throw new ValidationException("EL certificate number required");
            }
        }

        // Deaf-blind requires medical certificate
        if (exemption.getReason() == ExemptionReason.DEAF_BLIND) {
            if (exemption.getCertificateIssuer() == null) {
                throw new ValidationException("Medical certificate issuer required");
            }
        }

        // Deaf-blind exemption must cover entire household
        if (exemption.getReason() == ExemptionReason.DEAF_BLIND &&
            exemption.getSubscriberType() == SubscriberType.PRIVATE_HOUSEHOLD) {
            // All household members must be deaf-blind
            validateAllMembersDeafBlind(exemption.getSubscriberId());
        }
    }
}
```

### Healthcare Exemptions

```java
public class HealthcareExemptionRules {

    public void validateForHealthcare(Exemption exemption) {
        // Only premium subsidy allowed for healthcare
        if (exemption.getReason() != ExemptionReason.PREMIUM_SUBSIDY) {
            throw new ValidationException("Only premium subsidy exemptions for healthcare");
        }

        // Must be partial exemption (subsidies reduce, not eliminate)
        if (exemption.getType() == ExemptionType.FULL) {
            throw new ValidationException("Healthcare exemptions cannot be full");
        }

        // Must specify reduction amount or percentage
        if (exemption.getReductionPercent() == null &&
            exemption.getFixedReductionAmount() == null) {
            throw new ValidationException("Must specify reduction amount");
        }
    }
}
```

---

## API Examples

### Apply for Exemption

```http
POST /api/v1/exemptions
Content-Type: application/json

{
  "subscriberId": "household-uuid",
  "subscriberType": "PRIVATE_HOUSEHOLD",
  "domain": "BROADCAST",
  "type": "FULL",
  "reason": "AHV_IV_SUPPLEMENT",
  "validFrom": "2026-01-01",
  "certificateNumber": "EL-2026-123456",
  "certificateIssuer": "Ausgleichskasse Zürich",
  "certificateDate": "2025-12-15"
}
```

### Response

```json
{
  "id": "exemption-uuid",
  "subscriberId": "household-uuid",
  "subscriberType": "PRIVATE_HOUSEHOLD",
  "domain": "BROADCAST",
  "type": "FULL",
  "reason": "AHV_IV_SUPPLEMENT",
  "status": "PENDING",
  "validFrom": "2026-01-01",
  "validTo": null,
  "certificateNumber": "EL-2026-123456",
  "certificateIssuer": "Ausgleichskasse Zürich",
  "certificateDate": "2025-12-15",
  "createdAt": "2026-01-27T10:30:00Z"
}
```

### Approve Exemption

```http
POST /api/v1/exemptions/{id}/approve
```

### Response

```json
{
  "id": "exemption-uuid",
  "status": "APPROVED",
  "verifiedAt": "2026-01-27",
  "nextVerificationDue": "2029-01-27"
}
```

### Calculate Fee with Exemptions

```http
GET /api/v1/subscriptions/{id}/calculate-fee
```

### Response

```json
{
  "subscriptionId": "subscription-uuid",
  "originalAmount": {
    "amount": 335.00,
    "currency": "CHF"
  },
  "exemptions": [
    {
      "exemptionId": "exemption-uuid",
      "reason": "AHV_IV_SUPPLEMENT",
      "reduction": {
        "amount": 335.00,
        "currency": "CHF"
      }
    }
  ],
  "finalAmount": {
    "amount": 0.00,
    "currency": "CHF"
  }
}
```

---

## Database Schema

```sql
CREATE TABLE exemptions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    subscriber_id UUID NOT NULL,
    subscriber_type VARCHAR(30) NOT NULL,
    domain VARCHAR(20) NOT NULL,
    subscription_id UUID,
    type VARCHAR(20) NOT NULL,
    reason VARCHAR(30) NOT NULL,
    reduction_percent DECIMAL(5, 2),
    fixed_reduction_amount DECIMAL(18, 2),
    valid_from DATE NOT NULL,
    valid_to DATE,
    certificate_number VARCHAR(100),
    certificate_issuer VARCHAR(255),
    certificate_date DATE,
    verified_at DATE,
    verified_by UUID,
    next_verification_due DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_reduction CHECK (
        type = 'FULL' OR
        reduction_percent IS NOT NULL OR
        fixed_reduction_amount IS NOT NULL
    ),
    CONSTRAINT chk_reduction_percent CHECK (
        reduction_percent IS NULL OR
        (reduction_percent >= 0 AND reduction_percent <= 100)
    )
);

CREATE INDEX idx_exemptions_subscriber ON exemptions(subscriber_id, subscriber_type);
CREATE INDEX idx_exemptions_domain ON exemptions(domain);
CREATE INDEX idx_exemptions_status ON exemptions(status);
CREATE INDEX idx_exemptions_validity ON exemptions(valid_from, valid_to);
CREATE INDEX idx_exemptions_verification ON exemptions(next_verification_due) WHERE status = 'APPROVED';
```

---

## Test Cases

### Validation Tests

```java
@Test
@DisplayName("should require certificate for EL exemption")
void should_requireCertificate_when_reasonIsAhvIvSupplement() {
    // Arrange
    var exemption = ExemptionFixture.createMediaExemption()
        .reason(ExemptionReason.AHV_IV_SUPPLEMENT)
        .certificateNumber(null)
        .build();

    // Act & Assert
    assertThatThrownBy(() -> broadcastExemptionRules.validateForBroadcast(exemption))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("certificate number required");
}
```

### Calculation Tests

```java
@Test
@DisplayName("should calculate full exemption")
void should_returnZero_when_exemptionIsFull() {
    // Arrange
    var exemption = ExemptionFixture.createFull();
    var originalAmount = Money.chf(335);

    // Act
    var result = exemption.applyTo(originalAmount);

    // Assert
    assertThat(result).isEqualTo(Money.ZERO);
}

@Test
@DisplayName("should calculate percentage reduction")
void should_reduceByPercent_when_percentageProvided() {
    // Arrange
    var exemption = ExemptionFixture.createWithPercent(50);
    var originalAmount = Money.chf(100);

    // Act
    var result = exemption.applyTo(originalAmount);

    // Assert
    assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
}
```

---

## Related Documentation

- [Subscription Model Extension Plan](../../planning/subscription-model-extension-plan.md)
- [Radio/TV Fee (RTVG)](../../concepts/radio-tv-fee.md)
- [Household Entity](./masterdata/household.md)
- [Organization Entity](./masterdata/organization.md)

---

*Last Updated: 2026-01-28*

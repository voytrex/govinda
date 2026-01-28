# Broadcast Fee Use Cases (RTVG/BAKOM)

## Overview

This document defines the use cases for managing Swiss radio and television fees (Radio- und Fernsehgebühr) in the Govinda ERP system. These use cases cover both household fees and corporate fees (collected by ESTV).

## Bounded Context: Broadcast Fee Management

### Domain Model

- **BroadcastSubscription**: The fee obligation for a household or business
- **Household**: Private or collective household (subscriber unit)
- **Organization**: Business entity (subscriber unit for corporate fee)
- **Exemption**: Fee waiver or reduction based on qualifying conditions

### Key Concepts

| Term | German | Description |
|------|--------|-------------|
| Household Fee | Haushaltsabgabe | Annual fee for private/collective households |
| Corporate Fee | Unternehmensabgabe | Tiered fee for VAT-registered businesses |
| Exemption | Befreiung | Full or partial fee waiver |
| EL Recipient | EL-Bezüger | Person receiving supplementary benefits |
| Collective Household | Kollektivhaushalt | Institution housing multiple persons |

### Business Rules

1. All Swiss households are liable for the fee (no opt-out since 2024)
2. One fee per household, not per person
3. All adult household members are jointly liable
4. Collective households are billed at CHF 670 per year
5. Businesses with turnover >= CHF 500,000 pay tiered corporate fee (VAT-registered)
6. EL recipients can apply for exemption
7. Deaf-blind persons can apply for exemption if no other fee-liable person lives in the same household
8. Diplomatic staff are exempt via FDFA data exchange

#### Source-Backed Rules

- Fee amounts and household liability: BAKOM/Serafe
- Corporate fee threshold and tiers: ESTV
- Opt-out ended 2024: Serafe
- Exemptions (EL, deaf-blind, diplomatic): Serafe

#### Assumptions (To Verify)

- Sole proprietors pay both household and corporate fees when liable.

---

## Use Case 1: Create Household Broadcast Subscription

### Specification

**Actor**: System (automatic) or User with admin permissions

**Preconditions**:
- Household exists in masterdata
- No existing active broadcast subscription for household

**Main Flow**:
1. System determines household type (private/collective)
2. System calculates annual fee based on type
3. System checks for existing exemptions
4. System creates subscription with ACTIVE status
5. System schedules annual billing (default) or quarterly installments (optional)
6. System returns created subscription

**Alternative Flows**:

**1a. Household already has active subscription**:
- System returns 409 Conflict

**1b. Household qualifies for exemption**:
- System applies exemption before creating subscription
- Fee may be CHF 0.00

**Postconditions**:
- Subscription is created with calculated fee
- Billing schedule created
- All adult members linked to subscription

**Business Rules**:
- Private household: CHF 335.00/year
- Collective household: CHF 670.00/year
- Billing: annual by default; optional quarterly invoices (admin fee applies)

### Request/Response

**Request**: `POST /api/v1/broadcast/subscriptions`
```json
{
  "householdId": "uuid",
  "effectiveDate": "2026-01-01"
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "householdId": "uuid",
  "householdType": "PRIVATE",
  "status": "ACTIVE",
  "effectiveDate": "2026-01-01",
  "annualFee": {
    "amount": 335.00,
    "currency": "CHF"
  },
  "exemptions": [],
  "netAnnualFee": {
    "amount": 335.00,
    "currency": "CHF"
  },
  "billingSchedule": [
    {"period": "ANNUAL", "dueDate": "2026-03-31", "amount": 335.00}
  ],
  "createdAt": "2026-01-27T10:30:00Z"
}
```

---

## Use Case 2: Create Corporate Broadcast Subscription

### Specification

**Actor**: System (automatic from VAT register) or Admin User

**Preconditions**:
- Organization exists in masterdata
- Organization is VAT-registered
- Organization turnover >= CHF 500,000
- No existing active corporate subscription

**Main Flow**:
1. System retrieves organization turnover
2. System determines fee tier from turnover
3. System calculates annual fee
4. System creates subscription with ACTIVE status
5. System schedules annual billing
6. System returns created subscription

**Alternative Flows**:

**2a. Organization not VAT-registered**:
- System returns 400 Bad Request (not liable)

**2b. Turnover below threshold**:
- System returns 400 Bad Request (not liable)

**2c. Turnover not available**:
- System returns 400 Bad Request (turnover required)

**Postconditions**:
- Subscription is created with tier-based fee
- Annual billing scheduled
- Tier recorded for audit

**Business Rules**:
- Turnover-based tiered pricing (18 levels)
- Turnover = total without VAT (including exports)
- Fee liability begins year after exceeding threshold
- Billing by ESTV (not Serafe)

### Request/Response

**Request**: `POST /api/v1/broadcast/corporate-subscriptions`
```json
{
  "organizationId": "uuid",
  "fiscalYear": 2026,
  "annualTurnover": {
    "amount": 2500000,
    "currency": "CHF"
  }
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "organizationId": "uuid",
  "organizationName": "Muster AG",
  "status": "ACTIVE",
  "fiscalYear": 2026,
  "turnover": {
    "amount": 2500000.00,
    "currency": "CHF"
  },
  "tier": 5,
  "tierRange": {
    "min": 2500000,
    "max": 3599999
  },
  "annualFee": {
    "amount": 645.00,
    "currency": "CHF"
  },
  "billingSchedule": [
    {"dueDate": "2026-03-31", "amount": 645.00}
  ],
  "createdAt": "2026-01-27T10:30:00Z"
}
```

---

## Use Case 3: Apply for EL Exemption

### Specification

**Actor**: User with exemption permissions

**Preconditions**:
- Household or person exists
- EL certificate available
- No existing approved exemption for same reason

**Main Flow**:
1. User provides EL certificate details
2. System validates certificate format
3. System creates exemption with PENDING status
4. Admin reviews and approves
5. System applies exemption to subscription
6. System schedules re-verification if required by authority

**Alternative Flows**:

**3a. Invalid certificate format**:
- System returns 400 Bad Request

**3b. Certificate already used**:
- System returns 409 Conflict

**3c. Admin rejects**:
- System sets status to REJECTED with reason

**Postconditions**:
- Exemption record created
- If approved: subscription fee reduced to CHF 0.00
- Re-verification scheduled if required

**Business Rules**:
- EL = Ergänzungsleistungen (supplementary benefits)
- Exemption requires application with proof
- Re-verification cadence per authority guidance

### Request/Response

**Request**: `POST /api/v1/broadcast/exemptions`
```json
{
  "subscriberId": "household-uuid",
  "subscriberType": "PRIVATE_HOUSEHOLD",
  "reason": "AHV_IV_SUPPLEMENT",
  "validFrom": "2026-01-01",
  "certificateNumber": "EL-2026-123456",
  "certificateIssuer": "Ausgleichskasse Zürich",
  "certificateDate": "2025-12-15"
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "subscriberId": "household-uuid",
  "reason": "AHV_IV_SUPPLEMENT",
  "type": "FULL",
  "status": "PENDING",
  "validFrom": "2026-01-01",
  "validTo": null,
  "certificateNumber": "EL-2026-123456",
  "createdAt": "2026-01-27T10:30:00Z"
}
```

---

## Use Case 4: Apply for Deaf-Blind Exemption

### Specification

**Actor**: User with exemption permissions

**Preconditions**:
- Household exists
- Medical certificate available for the deaf-blind person
- No other fee-liable person lives in the same household

**Main Flow**:
1. User provides medical certificates for the deaf-blind person
2. System validates household composition per exemption rule
3. System creates exemption with PENDING status
4. Admin reviews medical documentation
5. Admin approves exemption
6. System applies full exemption to subscription

**Alternative Flows**:

**4a. Other fee-liable person in household**:
- System returns 400 Bad Request
- Message: "Exemption not applicable while another fee-liable person lives in the household"

**4b. Missing medical certificates**:
- System returns 400 Bad Request

**4c. Admin rejects**:
- System sets status to REJECTED with reason

**Postconditions**:
- Exemption record created
- If approved: subscription fee reduced to CHF 0.00
- Periodic verification may be required

**Business Rules**:
- Deaf-blind condition required
- Household must have no other fee-liable person
- Medical certificates from authorized providers

### Request/Response

**Request**: `POST /api/v1/broadcast/exemptions`
```json
{
  "subscriberId": "household-uuid",
  "subscriberType": "PRIVATE_HOUSEHOLD",
  "reason": "DEAF_BLIND",
  "validFrom": "2026-01-01",
  "certificates": [
    {
      "personId": "person-1-uuid",
      "certificateNumber": "MED-2026-001",
      "certificateIssuer": "Dr. med. Hans Müller",
      "certificateDate": "2025-11-20"
    },
    {
      "personId": "person-2-uuid",
      "certificateNumber": "MED-2026-002",
      "certificateIssuer": "Dr. med. Hans Müller",
      "certificateDate": "2025-11-20"
    }
  ]
}
```

---

## Use Case 5: Approve Exemption

### Specification

**Actor**: Admin User with exemption approval permissions

**Preconditions**:
- Exemption exists with PENDING status
- Documentation has been reviewed

**Main Flow**:
1. Admin reviews exemption request
2. Admin verifies documentation
3. Admin approves exemption
4. System updates exemption status to APPROVED
5. System recalculates subscription fee
6. System schedules next verification (if conditional)

**Alternative Flows**:

**5a. Documentation insufficient**:
- Admin requests additional documents
- Exemption remains PENDING

**5b. Admin rejects**:
- System sets status to REJECTED
- Reason recorded

**Postconditions**:
- Exemption status is APPROVED
- Subscription fee updated
- Verification date set (if applicable)
- Audit trail created

### Request/Response

**Request**: `POST /api/v1/broadcast/exemptions/{id}/approve`
```json
{
  "notes": "EL certificate verified with Ausgleichskasse"
}
```

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "status": "APPROVED",
  "verifiedAt": "2026-01-27",
  "verifiedBy": "admin-uuid",
  "nextVerificationDue": "2027-01-27",
  "subscriptionUpdated": {
    "subscriptionId": "sub-uuid",
    "previousFee": 335.00,
    "newFee": 0.00
  }
}
```

---

## Use Case 6: Verify Exemption (Re-verification)

### Specification

**Actor**: System (scheduled) or Admin User

**Preconditions**:
- Exemption exists with APPROVED status
- Next verification date has passed

**Main Flow**:
1. System identifies exemptions due for verification
2. System sends verification request to subscriber
3. Subscriber provides updated documentation
4. Admin verifies documentation
5. If still valid: extend exemption, schedule next check
6. If no longer valid: revoke exemption, reactivate fee

**Alternative Flows**:

**6a. No response within deadline**:
- System suspends exemption
- Fee reinstated

**6b. Documentation shows condition ended**:
- Admin revokes exemption
- Fee reinstated from next billing period

**Postconditions**:
- Exemption extended or revoked
- Subscription fee updated accordingly
- Next verification scheduled (if extended)

### Request/Response

**Request**: `POST /api/v1/broadcast/exemptions/{id}/verify`
```json
{
  "stillValid": true,
  "updatedCertificateNumber": "EL-2026-789012",
  "certificateDate": "2026-12-15"
}
```

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "status": "APPROVED",
  "verifiedAt": "2027-01-27",
  "nextVerificationDue": "2028-01-27"
}
```

---

## Use Case 7: Get Household Fee Status

### Specification

**Actor**: Authenticated User

**Preconditions**:
- User has `media:read` permission

**Main Flow**:
1. User requests fee status for household
2. System retrieves subscription
3. System retrieves any active exemptions
4. System calculates current fee
5. System returns comprehensive status

### Request/Response

**Request**: `GET /api/v1/broadcast/households/{householdId}/fee-status`

**Response**: `200 OK`
```json
{
  "householdId": "uuid",
  "householdName": "Familie Müller",
  "householdType": "PRIVATE",
  "members": [
    {"personId": "uuid", "name": "Hans Müller", "role": "PRIMARY"},
    {"personId": "uuid", "name": "Anna Müller", "role": "PARTNER"}
  ],
  "subscription": {
    "id": "uuid",
    "status": "ACTIVE",
    "effectiveDate": "2024-01-01",
    "annualFee": {
      "amount": 335.00,
      "currency": "CHF"
    }
  },
  "exemptions": [],
  "netAnnualFee": {
    "amount": 335.00,
    "currency": "CHF"
  },
  "currentBillingPeriod": {
    "period": "ANNUAL-2026",
    "amount": 335.00,
    "status": "DUE",
    "dueDate": "2026-03-31"
  },
  "paymentHistory": [
    {"period": "ANNUAL-2025", "amount": 335.00, "paidDate": "2025-12-15"}
  ]
}
```

---

## Use Case 8: Update Organization Turnover

### Specification

**Actor**: Admin User or System (from VAT filing)

**Preconditions**:
- Organization exists
- Corporate subscription may or may not exist

**Main Flow**:
1. User provides new turnover figure
2. System updates organization record
3. System determines new tier
4. If tier changed: system adjusts subscription
5. If newly above threshold: system creates subscription
6. If newly below threshold: system terminates subscription

**Alternative Flows**:

**8a. Turnover decreases below threshold**:
- System terminates corporate subscription
- No fee liability next year

**8b. Turnover increases to new tier**:
- System adjusts fee for next billing period

**Postconditions**:
- Organization turnover updated
- Subscription tier adjusted if changed
- Fee amount updated for next period

### Request/Response

**Request**: `PATCH /api/v1/organizations/{id}/turnover`
```json
{
  "annualTurnover": {
    "amount": 5500000,
    "currency": "CHF"
  },
  "fiscalYear": 2025
}
```

**Response**: `200 OK`
```json
{
  "organizationId": "uuid",
  "previousTurnover": 2500000.00,
  "newTurnover": 5500000.00,
  "previousTier": 5,
  "newTier": 7,
  "previousFee": 645.00,
  "newFee": 1270.00,
  "effectiveFrom": "2026-01-01"
}
```

---

## Use Case 9: Handle Household Change

### Specification

**Actor**: System (triggered by masterdata change)

**Preconditions**:
- Person moves to new address
- Address change processed in masterdata

**Main Flow**:
1. System detects address change
2. System determines if new household or join existing
3. System updates subscription membership
4. If leaving: remove from old subscription
5. If joining: add to new subscription
6. If creating new: create new subscription

**Scenarios**:

**9a. Person moves within same household**:
- No subscription change

**9b. Person moves to new household (new address)**:
- Create new subscription for new household
- Remove from old household subscription

**9c. Person joins existing household**:
- Add to existing household subscription
- Remove from old household subscription

**9d. Household member dies**:
- Remove from subscription
- If last adult: terminate subscription

**Postconditions**:
- Subscription membership reflects current household
- Old household subscription updated
- New household subscription created/updated

---

## Use Case 10: Generate Billing

### Specification

**Actor**: System (scheduled batch)

**Preconditions**:
- Active subscriptions exist
- Billing period due

**Main Flow**:
1. System identifies subscriptions due for billing
2. System applies any active exemptions
3. System calculates net fee
4. System generates invoice
5. System sends to billing module

**Business Rules**:
- Household fees: quarterly billing
- Corporate fees: annual billing
- Exemptions applied before invoice generation
- Joint liability for all adult household members

### Batch Job

```
Quarterly Household Billing (Q1):
1. SELECT subscriptions WHERE type = 'HOUSEHOLD' AND status = 'ACTIVE'
2. FOR EACH subscription:
   a. Calculate net fee (apply exemptions)
   b. Generate invoice for CHF {netFee/4}
   c. Set due date = end of quarter
3. Send invoices to billing module
```

---

## Domain Model Summary

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                   BROADCAST FEE DOMAIN MODEL                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐     ┌──────────────────┐     ┌──────────────┐           │
│  │  Household   │────►│BroadcastSubscription│◄────│ Organization │           │
│  │──────────────│     │──────────────────│     │──────────────│           │
│  │ type         │     │ subscriberId     │     │ turnover     │           │
│  │ members      │     │ subscriberType   │     │ tier         │           │
│  │ institutionId│     │ annualFee        │     │ vatRegistered│           │
│  └──────────────┘     │ status           │     └──────────────┘           │
│                       │ billingSchedule  │                                 │
│                       └────────┬─────────┘                                 │
│                                │                                           │
│                                │ 1:N                                       │
│                                ▼                                           │
│                       ┌──────────────────┐                                 │
│                       │    Exemption     │                                 │
│                       │──────────────────│                                 │
│                       │ reason           │                                 │
│                       │ type             │                                 │
│                       │ status           │                                 │
│                       │ validFrom/To     │                                 │
│                       │ certificate      │                                 │
│                       └──────────────────┘                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Enumerations

### HouseholdType (Extended)

- `PRIVATE` - Standard private household
- `SHARED` - WG/Flatshare (single household for billing)
- `COLLECTIVE` - Institution-run household (collective fee)

### BroadcastSubscriptionStatus

- `PENDING` - Created, not yet billed
- `ACTIVE` - Active, being billed
- `EXEMPT` - Active but fully exempt
- `SUSPENDED` - Temporarily suspended
- `TERMINATED` - Ended

### FeeTier (Corporate)

18 tiers based on turnover (see [Radio/TV Fee Concept](../domain/concepts/radio-tv-fee.md))

---

## Sources

- BAKOM fee overview: https://www.bakom.admin.ch/bakom/en/homepage/electronic-media/radio-and-television-fee.html
- Serafe fee overview: https://www.serafe.ch/en/the-fee/fee-overview/
- Serafe exemptions (EL, deaf-blind, diplomatic): https://www.serafe.ch/en/exemption-from-the-fee/
- ESTV corporate fee overview: https://www.estv.admin.ch/estv/en/home/federal-taxes/corporate-fee-for-radio-and-television.html
- ESTV tariff categories: https://www.estv.admin.ch/estv/en/home/federal-taxes/corporate-fee-for-radio-and-television/tariff-categories.html

---

## Permission Model

| Permission | Description |
|------------|-------------|
| `broadcast:read` | View subscriptions and fee status |
| `broadcast:write` | Create/update subscriptions |
| `broadcast:exemption:read` | View exemption requests |
| `broadcast:exemption:write` | Create exemption requests |
| `broadcast:exemption:approve` | Approve/reject exemptions |

---

## Error Handling

| Error | HTTP Status | Error Code |
|-------|-------------|------------|
| Household not found | 404 | `HOUSEHOLD_NOT_FOUND` |
| Organization not found | 404 | `ORGANIZATION_NOT_FOUND` |
| Subscription exists | 409 | `SUBSCRIPTION_EXISTS` |
| Not fee liable | 400 | `NOT_FEE_LIABLE` |
| Turnover required | 400 | `TURNOVER_REQUIRED` |
| Invalid exemption reason | 400 | `INVALID_EXEMPTION_REASON` |
| Certificate required | 400 | `CERTIFICATE_REQUIRED` |
| Other fee-liable person in household | 400 | `HOUSEHOLD_HAS_FEE_LIABLE_PERSON` |
| Exemption not pending | 409 | `EXEMPTION_NOT_PENDING` |

---

## Integration Points

### With Masterdata
- Household lookup and membership
- Organization lookup and turnover
- Person details for exemptions
- Address for household determination

### With Billing
- Invoice generation
- Payment tracking
- Dunning process

### With Exemption Module
- Exemption application
- Verification workflow
- Fee calculation

---

*Last updated: 2026-01-28*

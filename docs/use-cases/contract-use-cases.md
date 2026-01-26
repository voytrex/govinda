# Contract Management Use Cases

## Overview

This document defines the use cases for contract management in the Govinda ERP system. Contracts (Policies) link insured persons to insurance products with specific coverages.

## Bounded Context: Contract Management

### Domain Model

- **Policy**: The main contract entity - an agreement between insurer and policyholder
- **Coverage**: A specific insurance coverage within a policy (links to a product/tariff)
- **Mutation**: A change to a policy or coverage (new, change, termination)
- **Policyholder**: The person responsible for the policy (may differ from insured)

### Key Concepts

| Term | German | Description |
|------|--------|-------------|
| Policy | Police/Vertrag | The insurance contract |
| Coverage | Deckung | A specific product coverage |
| Policyholder | Versicherungsnehmer (VN) | Contract holder, responsible for payment |
| Insured Person | Versicherte Person (VP) | Person receiving coverage |
| Effective Date | Gültig ab | When coverage begins |
| Termination Date | Gültig bis | When coverage ends |

### Business Rules

1. A policy belongs to exactly one policyholder
2. A policy can cover multiple insured persons (family policy)
3. Each insured person can have multiple coverages (KVG + VVG)
4. KVG coverage is mandatory for Swiss residents
5. Each person can have only ONE active KVG coverage at any time
6. VVG coverages are optional and multiple allowed
7. Coverage changes follow mutation rules (effective dates)
8. KVG changes allowed: annually on 01.01, or mid-year for moves/life events
9. VVG changes depend on product terms
10. Coverages inherit premium from tariff based on person's parameters

---

## Use Case 1: Create Policy

### Specification

**Actor**: User with contract permissions

**Preconditions**:
- User has `contract:write` permission
- Policyholder (Person) exists in master data
- Insured persons exist in master data

**Main Flow**:
1. User provides policyholder ID
2. User specifies billing address (optional, defaults to policyholder address)
3. User specifies billing frequency (monthly/quarterly/semi-annual/annual)
4. System creates policy with DRAFT status
5. System returns created policy

**Alternative Flows**:

**1a. Policyholder not found**:
- System returns 404 Not Found

**Postconditions**:
- Policy is created with DRAFT status
- Policy has no coverages yet
- Policy is not yet active

**Business Rules**:
- Policyholder must be an adult (18+)
- Policy number is auto-generated (tenant-specific sequence)
- Policy starts in DRAFT until first coverage is added and activated

### Request/Response

**Request**: `POST /api/v1/contracts/policies`
```json
{
  "policyholderId": "uuid",
  "billingAddressId": "uuid",
  "billingFrequency": "MONTHLY",
  "preferredLanguage": "DE"
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "policyNumber": "POL-2024-000001",
  "policyholderId": "uuid",
  "status": "DRAFT",
  "billingFrequency": "MONTHLY",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

---

## Use Case 2: Add Coverage to Policy

### Specification

**Actor**: User with contract permissions

**Preconditions**:
- User has `contract:write` permission
- Policy exists
- Insured person exists
- Product/Tariff is active
- For KVG: person has no other active KVG coverage at effective date

**Main Flow**:
1. User specifies policy ID, insured person, product, effective date
2. For KVG: user specifies franchise and accident inclusion
3. System validates no KVG overlap (if KVG product)
4. System calculates premium based on person parameters
5. System creates coverage with PENDING status
6. System returns created coverage with premium

**Alternative Flows**:

**2a. Policy not found**:
- System returns 404 Not Found

**2b. Person not found**:
- System returns 404 Not Found

**2c. Product/Tariff not active**:
- System returns 400 Bad Request

**2d. KVG overlap detected**:
- System returns 409 Conflict with existing coverage info

**2e. No premium found for parameters**:
- System returns 400 Bad Request (premium region or age group issue)

**Postconditions**:
- Coverage is created with PENDING status
- Premium is calculated and stored
- Coverage awaits activation

**Business Rules**:
- Premium calculated from: tariff + region (from person's address) + age group + franchise
- KVG effective dates: usually 01.01, except for special cases
- VVG effective dates: depend on product terms
- Coverage is PENDING until policy is activated

### Request/Response

**Request**: `POST /api/v1/contracts/policies/{policyId}/coverages`
```json
{
  "insuredPersonId": "uuid",
  "productId": "uuid",
  "effectiveDate": "2024-01-01",
  "franchise": "F_300",
  "withAccident": true
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "policyId": "uuid",
  "insuredPersonId": "uuid",
  "productId": "uuid",
  "status": "PENDING",
  "effectiveDate": "2024-01-01",
  "terminationDate": null,
  "franchise": "F_300",
  "withAccident": true,
  "premium": {
    "monthlyAmount": 450.50,
    "tariffId": "uuid",
    "premiumRegion": "ZH-1",
    "ageGroup": "ADULT"
  }
}
```

---

## Use Case 3: Activate Policy

### Specification

**Actor**: User with contract permissions

**Preconditions**:
- User has `contract:write` permission
- Policy exists and is in DRAFT status
- Policy has at least one coverage

**Main Flow**:
1. User requests policy activation
2. System validates policy has coverages
3. System changes policy status to ACTIVE
4. System changes all PENDING coverages to ACTIVE
5. System creates activation mutation record
6. System returns updated policy

**Alternative Flows**:

**3a. Policy has no coverages**:
- System returns 400 Bad Request

**3b. Policy not in DRAFT status**:
- System returns 409 Conflict

**Postconditions**:
- Policy status is ACTIVE
- All coverages are ACTIVE
- Mutation record created for audit

### Request/Response

**Request**: `POST /api/v1/contracts/policies/{policyId}/activate`

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "policyNumber": "POL-2024-000001",
  "status": "ACTIVE",
  "activatedAt": "2024-01-15T10:30:00Z",
  "coverages": [
    {
      "id": "uuid",
      "status": "ACTIVE",
      "effectiveDate": "2024-01-01"
    }
  ]
}
```

---

## Use Case 4: Change Coverage (Mutation)

### Specification

**Actor**: User with contract permissions

**Preconditions**:
- User has `contract:write` permission
- Coverage exists and is ACTIVE
- Change is allowed per business rules

**Main Flow**:
1. User specifies coverage ID and change type
2. User provides new values and effective date
3. System validates change is allowed
4. System creates mutation record
5. System schedules change for effective date
6. System returns mutation confirmation

**Change Types**:
- `FRANCHISE_CHANGE` - Change KVG franchise (01.01 only)
- `MODEL_CHANGE` - Change KVG model (01.01 only)
- `ADDRESS_CHANGE` - Person moved (affects premium region)
- `TERMINATION` - End coverage

**Alternative Flows**:

**4a. Change not allowed at date**:
- System returns 400 Bad Request with reason

**4b. KVG change outside allowed period**:
- System returns 400 Bad Request

**Postconditions**:
- Mutation record created
- Change scheduled for effective date
- Premium recalculated if applicable

**Business Rules**:
- KVG franchise/model changes: only effective 01.01
- KVG termination: requires proof of new coverage (competitor)
- VVG changes: per product terms (usually 30-day notice)
- Address changes: immediate effect on premium region

### Request/Response

**Request**: `POST /api/v1/contracts/coverages/{coverageId}/mutations`
```json
{
  "mutationType": "FRANCHISE_CHANGE",
  "effectiveDate": "2025-01-01",
  "newFranchise": "F_2500"
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "coverageId": "uuid",
  "mutationType": "FRANCHISE_CHANGE",
  "status": "SCHEDULED",
  "effectiveDate": "2025-01-01",
  "previousValue": "F_300",
  "newValue": "F_2500",
  "newMonthlyPremium": 380.00,
  "createdAt": "2024-11-15T10:30:00Z"
}
```

---

## Use Case 5: Terminate Coverage

### Specification

**Actor**: User with contract permissions

**Preconditions**:
- User has `contract:write` permission
- Coverage exists and is ACTIVE
- For KVG: termination requirements met

**Main Flow**:
1. User specifies coverage ID and termination date
2. For KVG: user provides reason and proof of new coverage
3. System validates termination is allowed
4. System creates termination mutation
5. System schedules coverage end date
6. System returns confirmation

**Alternative Flows**:

**5a. KVG termination without valid reason**:
- System returns 400 Bad Request

**5b. Termination date in the past**:
- System returns 400 Bad Request

**Postconditions**:
- Coverage has scheduled termination date
- Mutation record created
- If last coverage on policy, policy termination scheduled

**Business Rules**:
- KVG termination requires: proof of new insurer OR leaving Switzerland
- KVG notice period: by end of November for 01.01 termination
- VVG termination: per product terms
- Policy terminates when all coverages terminated

### Request/Response

**Request**: `POST /api/v1/contracts/coverages/{coverageId}/terminate`
```json
{
  "terminationDate": "2024-12-31",
  "reason": "INSURER_CHANGE",
  "newInsurerName": "CSS",
  "confirmationNumber": "CSS-2024-12345"
}
```

---

## Use Case 6: Get Policy Details

### Specification

**Actor**: Authenticated user

**Preconditions**:
- User has `contract:read` permission

**Main Flow**:
1. User requests policy by ID or policy number
2. System retrieves policy with all coverages
3. System returns policy details

### Request/Response

**Request**: `GET /api/v1/contracts/policies/{policyId}`

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "policyNumber": "POL-2024-000001",
  "status": "ACTIVE",
  "policyholder": {
    "id": "uuid",
    "firstName": "Hans",
    "lastName": "Müller",
    "ahvNumber": "756.1234.5678.97"
  },
  "billingFrequency": "MONTHLY",
  "billingAddress": { ... },
  "coverages": [
    {
      "id": "uuid",
      "insuredPerson": {
        "id": "uuid",
        "firstName": "Hans",
        "lastName": "Müller"
      },
      "product": {
        "id": "uuid",
        "code": "KVG_STANDARD_2024",
        "name": "Grundversicherung Standard"
      },
      "status": "ACTIVE",
      "effectiveDate": "2024-01-01",
      "terminationDate": null,
      "franchise": "F_300",
      "withAccident": true,
      "monthlyPremium": 450.50
    }
  ],
  "totalMonthlyPremium": 450.50
}
```

---

## Use Case 7: Search Policies

### Specification

**Actor**: Authenticated user

**Preconditions**:
- User has `contract:read` permission

**Main Flow**:
1. User provides search criteria
2. System searches policies matching criteria
3. System returns paginated results

**Search Criteria**:
- Policy number
- Policyholder name / AHV number
- Insured person name / AHV number
- Status (DRAFT, ACTIVE, TERMINATED)
- Product category (KVG, VVG)

### Request/Response

**Request**: `GET /api/v1/contracts/policies?insuredPersonAhv=756.1234.5678.97&status=ACTIVE`

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": "uuid",
      "policyNumber": "POL-2024-000001",
      "status": "ACTIVE",
      "policyholderName": "Hans Müller",
      "coverageCount": 2,
      "totalMonthlyPremium": 535.50
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1
}
```

---

## Use Case 8: Get Person's Active Coverages

### Specification

**Actor**: Authenticated user

**Preconditions**:
- User has `contract:read` permission

**Main Flow**:
1. User requests coverages for a person ID
2. System retrieves all active coverages for person
3. System returns coverage list

**Business Rules**:
- Returns coverages across all policies
- Only returns ACTIVE coverages
- Includes premium information

### Request/Response

**Request**: `GET /api/v1/contracts/persons/{personId}/coverages?status=ACTIVE`

**Response**: `200 OK`
```json
{
  "personId": "uuid",
  "personName": "Hans Müller",
  "coverages": [
    {
      "id": "uuid",
      "policyId": "uuid",
      "policyNumber": "POL-2024-000001",
      "product": {
        "code": "KVG_STANDARD_2024",
        "category": "KVG",
        "name": "Grundversicherung Standard"
      },
      "effectiveDate": "2024-01-01",
      "monthlyPremium": 450.50
    },
    {
      "id": "uuid",
      "policyId": "uuid",
      "policyNumber": "POL-2024-000001",
      "product": {
        "code": "SPITAL_HALBPRIVAT",
        "category": "VVG",
        "name": "Spital halbprivat"
      },
      "effectiveDate": "2024-01-01",
      "monthlyPremium": 85.00
    }
  ],
  "totalMonthlyPremium": 535.50
}
```

---

## Domain Model Summary

```
┌─────────────────────────────────────────────────────────────┐
│                         Policy                               │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ tenantId: UUID                                               │
│ policyNumber: String (unique per tenant)                    │
│ policyholderId: UUID (FK to Person)                         │
│ status: PolicyStatus                                         │
│ billingAddressId: UUID (FK to Address)                      │
│ billingFrequency: BillingFrequency                          │
│ preferredLanguage: Language                                  │
│ activatedAt: Instant?                                        │
│ terminatedAt: Instant?                                       │
│ coverages: List<Coverage>                                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1:N
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Coverage                              │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ policyId: UUID                                               │
│ insuredPersonId: UUID (FK to Person)                        │
│ productId: UUID (FK to Product)                             │
│ tariffId: UUID (FK to Tariff)                               │
│ status: CoverageStatus                                       │
│ effectiveDate: LocalDate                                     │
│ terminationDate: LocalDate?                                  │
│ franchise: Franchise? (KVG only)                            │
│ withAccident: Boolean? (KVG only)                           │
│ premiumRegionId: UUID                                        │
│ ageGroup: AgeGroup (at effectiveDate)                       │
│ monthlyPremium: Money                                        │
│ mutations: List<Mutation>                                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1:N
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Mutation                              │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ coverageId: UUID                                             │
│ mutationType: MutationType                                   │
│ status: MutationStatus                                       │
│ effectiveDate: LocalDate                                     │
│ previousValue: String?                                       │
│ newValue: String?                                            │
│ reason: String?                                              │
│ createdAt: Instant                                           │
│ processedAt: Instant?                                        │
│ createdBy: UUID                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Enumerations

### PolicyStatus
- `DRAFT` - Policy created, not yet active
- `ACTIVE` - Policy is active with valid coverages
- `SUSPENDED` - Policy temporarily suspended (e.g., non-payment)
- `TERMINATED` - Policy has ended

### CoverageStatus
- `PENDING` - Coverage added but policy not activated
- `ACTIVE` - Coverage is active
- `TERMINATED` - Coverage has ended

### MutationType
- `NEW` - New coverage created
- `FRANCHISE_CHANGE` - Franchise changed
- `MODEL_CHANGE` - Insurance model changed
- `ADDRESS_CHANGE` - Address changed (premium region update)
- `TERMINATION` - Coverage terminated
- `REACTIVATION` - Coverage reactivated

### MutationStatus
- `SCHEDULED` - Change scheduled for future date
- `PROCESSED` - Change has been applied
- `CANCELLED` - Change was cancelled

### BillingFrequency
- `MONTHLY` - 12 invoices per year
- `QUARTERLY` - 4 invoices per year
- `SEMI_ANNUAL` - 2 invoices per year
- `ANNUAL` - 1 invoice per year

### TerminationReason (KVG)
- `INSURER_CHANGE` - Switching to another insurer
- `LEAVING_SWITZERLAND` - Moving abroad
- `DEATH` - Insured person deceased
- `DUPLICATE` - Duplicate coverage cleanup

---

## Permission Model

| Permission | Description |
|------------|-------------|
| `contract:read` | View policies and coverages |
| `contract:write` | Create/update policies and coverages |

---

## Error Handling

| Error | HTTP Status | Error Code |
|-------|-------------|------------|
| Policy not found | 404 | `POLICY_NOT_FOUND` |
| Coverage not found | 404 | `COVERAGE_NOT_FOUND` |
| Person not found | 404 | `PERSON_NOT_FOUND` |
| Product not found | 404 | `PRODUCT_NOT_FOUND` |
| KVG coverage overlap | 409 | `KVG_OVERLAP` |
| Invalid termination | 400 | `INVALID_TERMINATION` |
| Policy not in valid state | 409 | `INVALID_POLICY_STATE` |
| Coverage not in valid state | 409 | `INVALID_COVERAGE_STATE` |
| Mutation not allowed | 400 | `MUTATION_NOT_ALLOWED` |
| Premium not found | 400 | `PREMIUM_NOT_FOUND` |

---

## Integration Points

### With Master Data
- Person lookup for policyholder and insured persons
- Address lookup for billing and premium region determination

### With Product
- Product/Tariff lookup for coverage creation
- Premium calculation from tariff tables

### With Premium (future)
- Premium recalculation on mutations
- Annual premium adjustment

### With Billing (future)
- Invoice generation based on coverages
- Payment tracking

---

*Last updated: 2026-01-26*

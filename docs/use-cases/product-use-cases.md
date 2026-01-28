# Product Management Use Cases

## Overview

This document defines the use cases for product management in the Govinda ERP system. Products represent subscription offerings (healthcare, broadcast, telecom) with their associated tariffs and pricing rules.

## Bounded Context: Product Catalog

### Domain Model

- **Product**: A subscription product in a service domain
- **Tariff**: A specific version of a product with temporal validity
- **PremiumTable**: Premium amounts by region, age group, and franchise
- **PremiumRegion**: BAG-defined premium regions (1-3 per canton)
- **AgeGroup**: Age brackets for premium calculation (0-18, 19-25, 26+)
- **PricingTier**: Tiered prices (e.g., turnover-based fees)

### Product Categories

| Category | Description | Regulation |
|----------|-------------|------------|
| KVG | Mandatory basic insurance (Grundversicherung) | Federal law |
| VVG | Supplementary insurance (Zusatzversicherung) | Private law |

### KVG Insurance Models (In Scope)

| Model | Code | Description |
|-------|------|-------------|
| Standard | STANDARD | Free choice of provider |
| HMO | HMO | Health Maintenance Organization |
| Hausarzt | HAUSARZT | Family doctor model |
| Telmed | TELMED | Telemedicine first contact |

### VVG Products (In Scope)

| Product | Code | Description |
|---------|------|-------------|
| Spital Allgemein | SPITAL_ALLGEMEIN | General ward hospital coverage |
| Spital Halbprivat | SPITAL_HALBPRIVAT | Semi-private ward |
| Spital Privat | SPITAL_PRIVAT | Private ward |

### Non-Healthcare Products (Examples)

| Domain | Product | Pricing Model | Eligible Subscribers |
|--------|---------|---------------|----------------------|
| Broadcast | Household fee | FIXED | PRIVATE_HOUSEHOLD, COLLECTIVE_HOUSEHOLD |
| Broadcast | Corporate fee | TIERED | CORPORATE |
| Telecom | Mobile plan | FIXED + USAGE | INDIVIDUAL, CORPORATE |

### Business Rules

1. Each product has a ServiceDomain (HEALTHCARE, BROADCAST, TELECOM, CUSTOM)
2. Healthcare products must have a category (KVG or VVG)
3. KVG products must have an insurance model
4. VVG products do not have insurance models
4. Tariffs have validity periods (validFrom, validTo)
5. Only one tariff per product can be active at any time
6. Premium tables are linked to tariffs, not products directly
7. **KVG premiums vary by: region, age group, franchise, accident inclusion (unisex by law)**
8. **VVG premiums vary by: region, age group, gender (risk-based pricing allowed)**
9. Age groups: Children (0-18), Young Adults (19-25), Adults (26+)
10. Premium regions are defined by BAG per canton

### Gender in Premium Calculation

| Product Type | Gender-Based Premiums | Legal Basis |
|--------------|----------------------|-------------|
| **KVG** | ❌ Not allowed | Art. 61 KVG - unisex tariffs mandatory since 2013 |
| **VVG** | ✅ Allowed | Private law - risk-based pricing permitted |

> **Note**: For VVG products, insurers may differentiate premiums by gender based on actuarial risk assessment. This is common for hospital supplementary insurance where statistical differences in healthcare utilization exist.

---

## Use Case 1: Create Product

### Specification

**Actor**: Admin User

**Preconditions**:
- User has `product:write` permission
- Product code does not already exist

**Main Flow**:
1. User provides product details (code, name, service domain, pricing model)
2. User selects eligible subscriber types
3. For healthcare products: user specifies category (KVG/VVG)
4. For KVG products: user specifies insurance model
5. System validates product data
6. System creates product with INACTIVE status
7. System returns created product

**Alternative Flows**:

**1a. Product code already exists**:
- System returns 409 Conflict

**1b. Invalid category/model combination**:
- System returns 400 Bad Request

**Postconditions**:
- Product is created with INACTIVE status
- Product has no tariffs yet

**Business Rules**:
- Product code must be unique within tenant
- Product names must be provided in all 4 languages (DE, FR, IT, EN)
- ServiceDomain and PricingModel are required
- Eligible subscriber types are required
- KVG products require insurance model
- VVG products must not have insurance model

### Request/Response

**Request**: `POST /api/v1/products`
```json
{
  "code": "KVG_STANDARD_2024",
  "serviceDomain": "HEALTHCARE",
  "category": "KVG",
  "pricingModel": "REGION_AGE",
  "eligibleSubscriberTypes": ["INDIVIDUAL"],
  "insuranceModel": "STANDARD",
  "name": {
    "de": "Grundversicherung Standard",
    "fr": "Assurance de base Standard",
    "it": "Assicurazione base Standard",
    "en": "Basic Insurance Standard"
  },
  "description": {
    "de": "Freie Arztwahl...",
    "fr": "Libre choix du médecin...",
    "it": "Libera scelta del medico...",
    "en": "Free choice of doctor..."
  }
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "code": "KVG_STANDARD_2024",
  "serviceDomain": "HEALTHCARE",
  "category": "KVG",
  "pricingModel": "REGION_AGE",
  "eligibleSubscriberTypes": ["INDIVIDUAL"],
  "insuranceModel": "STANDARD",
  "status": "INACTIVE",
  "name": { ... },
  "description": { ... },
  "createdAt": "2024-01-15T10:30:00Z"
}
```

---

## Use Case 2: Create Tariff for Product

### Specification

**Actor**: Admin User

**Preconditions**:
- User has `product:write` permission
- Product exists
- No overlapping active tariff for the validity period

**Main Flow**:
1. User specifies product ID and tariff validity period
2. System validates no overlap with existing tariffs
3. System creates tariff with DRAFT status
4. System returns created tariff

**Alternative Flows**:

**2a. Product not found**:
- System returns 404 Not Found

**2b. Validity period overlaps existing tariff**:
- System returns 409 Conflict

**2c. validFrom is after validTo**:
- System returns 400 Bad Request

**Postconditions**:
- Tariff is created with DRAFT status
- Tariff has no premium entries yet

**Business Rules**:
- Tariff validity periods cannot overlap for same product
- validFrom must be before validTo
- Tariff inherits product's category and insurance model
- Tariffs start as DRAFT, must be explicitly activated

### Request/Response

**Request**: `POST /api/v1/products/{productId}/tariffs`
```json
{
  "validFrom": "2024-01-01",
  "validTo": "2024-12-31",
  "version": "2024-V1"
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "productId": "uuid",
  "version": "2024-V1",
  "status": "DRAFT",
  "validFrom": "2024-01-01",
  "validTo": "2024-12-31",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

---

## Use Case 3: Define Premium Table Entry

### Specification

**Actor**: Admin User

**Preconditions**:
- User has `product:write` permission
- Tariff exists and is in DRAFT status
- Premium entry for this combination does not exist

**Main Flow**:
1. User specifies tariff ID and premium parameters
2. For KVG: region, age group, franchise, accident inclusion, amount
3. For VVG: region, age group, gender (optional), amount
4. System validates parameters
5. System creates premium entry
6. System returns created entry

**Alternative Flows**:

**3a. Tariff not found**:
- System returns 404 Not Found

**3b. Tariff not in DRAFT status**:
- System returns 409 Conflict (cannot modify active tariff)

**3c. Premium entry already exists for combination**:
- System returns 409 Conflict

**3d. Invalid premium region**:
- System returns 400 Bad Request

**Postconditions**:
- Premium entry is stored in the tariff

**Business Rules**:
- Premium amounts are in CHF
- Premium amounts must be positive
- KVG requires: region, ageGroup, franchise, withAccident (no gender - unisex by law)
- VVG requires: region, ageGroup, gender (optional, for risk-based pricing)
- Premium regions must be valid BAG regions
- If VVG product uses gender-based pricing, entries for both MALE and FEMALE must exist

### Request/Response (KVG)

**Request**: `POST /api/v1/tariffs/{tariffId}/premiums`
```json
{
  "premiumRegionId": "uuid",
  "ageGroup": "ADULT",
  "franchise": "F_300",
  "withAccident": true,
  "monthlyAmount": 450.50
}
```

### Request/Response (VVG)

**Request**: `POST /api/v1/tariffs/{tariffId}/premiums`
```json
{
  "premiumRegionId": "uuid",
  "ageGroup": "ADULT",
  "gender": "FEMALE",
  "monthlyAmount": 85.00
}
```

> **Note**: For VVG products with unisex pricing, omit the `gender` field. For gender-differentiated products, create separate entries for MALE and FEMALE.

---

## Use Case 4: Bulk Import Premium Table

### Specification

**Actor**: Admin User

**Preconditions**:
- User has `product:write` permission
- Tariff exists and is in DRAFT status

**Main Flow**:
1. User uploads CSV/JSON with all premium entries
2. System validates all entries
3. System replaces existing premium table (if any)
4. System returns import summary

**Alternative Flows**:

**4a. Validation errors in data**:
- System returns 400 Bad Request with error details
- No data is imported (all-or-nothing)

**4b. Tariff not in DRAFT status**:
- System returns 409 Conflict

**Postconditions**:
- All premium entries are stored
- Previous entries (if any) are replaced

**Business Rules**:
- Import is transactional (all-or-nothing)
- All required combinations must be provided
- KVG: all regions × age groups × franchises × accident (unisex)
- VVG (unisex): all regions × age groups
- VVG (gender-based): all regions × age groups × genders

### Request/Response

**Request**: `POST /api/v1/tariffs/{tariffId}/premiums/import`
```json
{
  "entries": [
    {
      "premiumRegionCode": "ZH-1",
      "ageGroup": "ADULT",
      "franchise": "F_300",
      "withAccident": true,
      "monthlyAmount": 450.50
    },
    ...
  ]
}
```

**Response**: `200 OK`
```json
{
  "imported": 1296,
  "tariffId": "uuid"
}
```

---

## Use Case 5: Activate Tariff

### Specification

**Actor**: Admin User

**Preconditions**:
- User has `product:write` permission
- Tariff exists and is in DRAFT status
- Tariff has complete premium table

**Main Flow**:
1. User requests tariff activation
2. System validates premium table completeness
3. System changes tariff status to ACTIVE
4. System activates parent product if INACTIVE
5. System returns updated tariff

**Alternative Flows**:

**5a. Premium table incomplete**:
- System returns 400 Bad Request with missing entries

**5b. Tariff not in DRAFT status**:
- System returns 409 Conflict

**Postconditions**:
- Tariff status is ACTIVE
- Product status is ACTIVE (if was INACTIVE)
- Tariff is available for contract creation

**Business Rules**:
- All premium combinations must exist before activation
- Activating tariff automatically activates product
- Only ACTIVE tariffs can be used in contracts

### Request/Response

**Request**: `POST /api/v1/tariffs/{tariffId}/activate`

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "status": "ACTIVE",
  "activatedAt": "2024-01-15T10:30:00Z"
}
```

---

## Use Case 6: Get Product Catalog

### Specification

**Actor**: Any authenticated user

**Preconditions**:
- User has `product:read` permission

**Main Flow**:
1. User requests product list with optional filters
2. System returns paginated list of products
3. Each product includes current active tariff (if any)

**Query Parameters**:
- `serviceDomain`: Filter by domain (HEALTHCARE, BROADCAST, TELECOM)
- `category`: Filter by KVG or VVG (healthcare only)
- `insuranceModel`: Filter by model (KVG only)
- `status`: Filter by ACTIVE/INACTIVE
- `page`, `size`: Pagination

### Request/Response

**Request**: `GET /api/v1/products?category=KVG&status=ACTIVE`

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": "uuid",
      "code": "KVG_STANDARD_2024",
      "category": "KVG",
      "insuranceModel": "STANDARD",
      "status": "ACTIVE",
      "name": { "de": "...", "fr": "...", "it": "...", "en": "..." },
      "currentTariff": {
        "id": "uuid",
        "version": "2024-V1",
        "validFrom": "2024-01-01",
        "validTo": "2024-12-31"
      }
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 4
}
```

---

## Use Case 7: Get Premium for Parameters

### Specification

**Actor**: Any authenticated user

**Preconditions**:
- User has `product:read` permission

**Main Flow**:
1. User provides product ID and premium parameters
2. System finds active tariff for current date
3. System looks up premium for parameters
4. System returns monthly premium amount

**Alternative Flows**:

**7a. No active tariff for date**:
- System returns 404 Not Found

**7b. No premium entry for parameters**:
- System returns 404 Not Found

**7c. Invalid parameters for product type**:
- System returns 400 Bad Request

### Request/Response

**Request (KVG)**: `GET /api/v1/products/{productId}/premium?postalCode=8001&birthDate=1985-03-15&franchise=F_300&withAccident=true`

**Request (VVG with gender)**: `GET /api/v1/products/{productId}/premium?postalCode=8001&birthDate=1985-03-15&gender=FEMALE`

**Response**: `200 OK`
```json
{
  "productId": "uuid",
  "tariffId": "uuid",
  "tariffVersion": "2024-V1",
  "premiumRegion": {
    "code": "ZH-1",
    "name": "Zürich Region 1"
  },
  "ageGroup": "ADULT",
  "franchise": "F_300",
  "withAccident": true,
  "monthlyAmount": 450.50,
  "annualAmount": 5406.00
}
```

---

## Use Case 8: Deactivate Product

### Specification

**Actor**: Admin User

**Preconditions**:
- User has `product:write` permission
- Product exists
- No active contracts use this product

**Main Flow**:
1. User requests product deactivation
2. System checks for active contracts
3. System deactivates all tariffs
4. System sets product status to INACTIVE
5. System returns updated product

**Alternative Flows**:

**8a. Active contracts exist**:
- System returns 409 Conflict with contract count

**Postconditions**:
- Product status is INACTIVE
- All tariffs are INACTIVE
- Product cannot be used for new contracts

---

## Domain Model Summary

```
┌─────────────────────────────────────────────────────────────┐
│                        Product                               │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ tenantId: UUID                                               │
│ code: String (unique per tenant)                            │
│ category: ProductCategory (KVG, VVG)                        │
│ insuranceModel: InsuranceModel? (KVG only)                  │
│ status: ProductStatus (ACTIVE, INACTIVE)                    │
│ name: LocalizedText                                          │
│ description: LocalizedText                                   │
│ tariffs: List<Tariff>                                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1:N
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         Tariff                               │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ productId: UUID                                              │
│ version: String                                              │
│ status: TariffStatus (DRAFT, ACTIVE, INACTIVE)              │
│ validFrom: LocalDate                                         │
│ validTo: LocalDate                                           │
│ premiums: List<PremiumEntry>                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1:N
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      PremiumEntry                            │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ tariffId: UUID                                               │
│ premiumRegionId: UUID                                        │
│ ageGroup: AgeGroup (CHILD, YOUNG_ADULT, ADULT)              │
│ franchise: Franchise? (KVG only: F_300..F_2500)             │
│ withAccident: Boolean? (KVG only)                           │
│ gender: Gender? (VVG only, for risk-based pricing)          │
│ monthlyAmount: Money                                         │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     PremiumRegion                            │
├─────────────────────────────────────────────────────────────┤
│ id: UUID                                                     │
│ code: String (e.g., "ZH-1", "BE-2")                         │
│ canton: Canton                                               │
│ regionNumber: Integer (1, 2, or 3)                          │
│ name: LocalizedText                                          │
│ postalCodes: List<String>                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## Enumerations

### ProductCategory
- `KVG` - Mandatory basic insurance
- `VVG` - Supplementary insurance

### InsuranceModel (KVG only)
- `STANDARD` - Free choice of provider
- `HMO` - Health Maintenance Organization
- `HAUSARZT` - Family doctor model
- `TELMED` - Telemedicine first contact

### ProductStatus
- `ACTIVE` - Available for new contracts
- `INACTIVE` - Not available for new contracts

### TariffStatus
- `DRAFT` - Being prepared, can be edited
- `ACTIVE` - Live, used for premium calculation
- `INACTIVE` - Archived, not used

### AgeGroup
- `CHILD` - 0-18 years
- `YOUNG_ADULT` - 19-25 years
- `ADULT` - 26+ years

### Franchise (KVG only)
- `F_0` - CHF 0 (children only)
- `F_300` - CHF 300
- `F_500` - CHF 500
- `F_1000` - CHF 1000
- `F_1500` - CHF 1500
- `F_2000` - CHF 2000
- `F_2500` - CHF 2500

---

## Permission Model

| Permission | Description |
|------------|-------------|
| `product:read` | View products, tariffs, premiums |
| `product:write` | Create/update/delete products and tariffs |

---

## Error Handling

| Error | HTTP Status | Error Code |
|-------|-------------|------------|
| Product not found | 404 | `PRODUCT_NOT_FOUND` |
| Tariff not found | 404 | `TARIFF_NOT_FOUND` |
| Premium not found | 404 | `PREMIUM_NOT_FOUND` |
| Product code exists | 409 | `PRODUCT_CODE_DUPLICATE` |
| Tariff overlap | 409 | `TARIFF_OVERLAP` |
| Cannot modify active tariff | 409 | `TARIFF_NOT_MODIFIABLE` |
| Incomplete premium table | 400 | `PREMIUM_TABLE_INCOMPLETE` |
| Has active contracts | 409 | `HAS_ACTIVE_CONTRACTS` |
| Invalid category/model | 400 | `INVALID_PRODUCT_CONFIG` |

---

*Last updated: 2026-01-28*

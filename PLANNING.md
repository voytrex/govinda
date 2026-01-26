# Govinda ERP - Development Planning & Findings

## Current State Assessment

### Implementation Status

| Module | Status | Completion | Notes |
|--------|--------|------------|-------|
| **govinda-common** | Active | ~70% | Auth, i18n, exceptions, value objects |
| **govinda-masterdata** | Active | ~50% | Person domain model done, persistence WIP |
| **govinda-product** | Placeholder | 0% | Empty module |
| **govinda-contract** | Placeholder | 0% | Empty module |
| **govinda-premium** | Placeholder | 0% | Empty module |
| **govinda-billing** | Placeholder | 0% | Empty module |
| **govinda-app** | Active | ~80% | Main app, needs integration |

### Documented Use Cases

| Use Case | Documented | Implemented | Tested |
|----------|------------|-------------|--------|
| UC1: Authenticate User | Yes | Partial | Partial |
| UC2: Validate JWT Token | Yes | Yes | Yes |
| UC3: Validate Tenant Access | Yes | Partial | Partial |
| UC4: Check Permission | Yes | Yes | Partial |
| UC5: Get User's Tenants | Yes | Yes | Partial |
| UC6: Get Current User Info | Yes | Yes | Partial |

### Bounded Contexts (from ADR-002)

```
MASTERDATA (Stammdaten)     PRODUCT (Produkte)        CONTRACT (Vertraege)
- Person                    - Product                 - Policy
- Household                 - Tariff                  - Coverage
- Address                   - PremiumTable            - Mutation

BILLING (Fakturierung)      PREMIUM (Berechnung)      BENEFITS (Leistungen)
- Invoice                   - Calculator              - Claim
- Payment                   - PremiumResult           - Benefit
```

---

## Gaps & Missing Pieces

### High Priority (MVP Blockers)

1. **Master Data Persistence Layer**
   - `JpaPersonRepository` - exists but needs completion
   - `JpaHouseholdRepository` - not implemented
   - `JpaAddressRepository` - not implemented
   - Integration tests missing

2. **Authentication Completion**
   - Tenant validation middleware not fully integrated
   - Missing integration tests for multi-tenant flows
   - No token refresh mechanism

3. **Product Module (0%)**
   - No use cases documented
   - No domain model
   - Critical for MVP - can't have contracts without products

4. **Contract Module (0%)**
   - No use cases documented
   - No domain model
   - Core business capability

### Medium Priority

5. **Premium Calculation Module (0%)**
   - No use cases documented
   - Depends on Product and Contract modules

6. **Test Coverage Gaps**
   - PersonController integration tests
   - Full auth flow E2E tests
   - Repository integration tests with Testcontainers

### Lower Priority (Post-MVP)

7. **Billing Module (0%)**
8. **Benefits Module (0%)** - not in current module list
9. **External Integrations** (SASIS, Sumex)

---

## Development Roadmap

### Phase 1: MVP - Person Management (Current)

**Goal**: Complete vertical slice for person/household/address management

| Task | Status | Priority |
|------|--------|----------|
| Complete JpaPersonRepository | DONE | P1 |
| Implement JpaHouseholdRepository | DONE | P1 |
| Implement JpaAddressRepository | DONE | P1 |
| PersonService unit tests | DONE | P1 |
| PersonRepository integration tests | DONE (needs Docker) | P1 |
| PersonController tests | DONE (needs Java 21) | P1 |
| HouseholdController (new) | TODO | P2 |
| E2E: Create person with household | TODO | P2 |

### Phase 2: Use Case Documentation

**Goal**: Document use cases before implementing Product/Contract modules

| Document | Status | Priority |
|----------|--------|----------|
| Product use cases (product-use-cases.md) | DONE | P1 |
| Contract use cases (contract-use-cases.md) | DONE | P1 |
| Premium calculation use cases | TODO | P2 |
| Billing use cases | TODO | P3 |

### Phase 3: Product Module Implementation

*(After use cases documented)*

```
1. Product domain model (TDD)
2. Tariff domain model (TDD)
3. PremiumTable domain model (TDD)
4. JPA persistence layer
5. ProductController + API
```

### Phase 4: Contract Module Implementation

*(After Product module)*

```
1. Policy domain model (TDD)
2. Coverage domain model (TDD)
3. Mutation domain model (TDD)
4. JPA persistence layer
5. ContractController + API
```

---

## Decisions Made

| Decision | Answer | Date |
|----------|--------|------|
| MVP Scope | Person management only | 2026-01-26 |
| Next Focus | Document Product/Contract use cases first | 2026-01-26 |

## Questions to Resolve

1. **Product Module Scope**:
   - Which KVG models? (Standard, HMO, Hausarzt, Telmed)
   - Which VVG products for initial catalog?
   - Premium table structure - by region/age/franchise?

2. **Contract Module Scope**:
   - Policy lifecycle states?
   - Mutation types to support?
   - Coverage start/end rules?

3. **External Integrations Timeline**:
   - When do we need SASIS integration?
   - Is Sumex XML required for MVP?

4. **Multi-Tenant Testing**:
   - Do we need proper test data setup scripts?
   - Should we create a dev tenant seeder?

5. **Authentication Gaps**:
   - Is token refresh needed for MVP?
   - Password reset flow - when?

---

## Technical Debt

| Item | Severity | Location |
|------|----------|----------|
| Checkstyle config not in SCM | Low | `/checkstyle.xml` (untracked) |
| SpotBugs exclude not in SCM | Low | `/spotbugs-exclude.xml` (untracked) |
| Some unstaged changes | Medium | See git status |
| Missing repository integration tests | High | masterdata module |

---

## Files Changed (Unstaged)

From `git status`:
- `backend/govinda-app/src/main/resources/application.yml`
- `backend/govinda-common/src/main/java/.../api/AuthController.java`
- `backend/govinda-common/src/main/java/.../api/ErrorDetail.java`
- `backend/govinda-common/src/main/java/.../api/ErrorResponse.java`
- `backend/govinda-common/src/main/java/.../api/LoginRequest.java`
- `backend/govinda-common/src/main/java/.../api/LoginResponse.java`
- `backend/govinda-common/src/main/java/.../api/OpenApiConfig.java`
- `backend/pom.xml`

**Untracked**:
- `checkstyle.xml`
- `spotbugs-exclude.xml`

---

*Last updated: 2026-01-26*

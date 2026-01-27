# Domain Model Gap Analysis

## Analysis Date: 2026-01-27

This document identifies gaps in the current domain model based on requirements analysis for:
- Health insurance, Broadcast (BAKOM), Telecom subscriptions
- Household types, Private/Legal entities
- Business partners, Third-party payers
- Suspension mechanisms (military, moving, etc.)
- Exemption rules (deaf/blind, etc.)

---

## 1. Current State Summary

### Existing Entities (Implemented)
| Entity | Module | Status |
|--------|--------|--------|
| Person | masterdata | ✅ Done |
| Household | masterdata | ✅ Done |
| HouseholdMember | masterdata | ✅ Done |
| Address | masterdata | ✅ Done |

### Planned Entities (Specified)
| Entity | Module | Status |
|--------|--------|--------|
| Organization | masterdata | 📋 Specified |
| Product | product | 📋 Specified |
| Tariff | product | 📋 Specified |
| Policy | contract | 📋 Specified |
| Coverage | contract | 📋 Specified |
| Exemption | contract | 📋 Specified |
| PricingTier | product | 📋 Specified |

### Planned Enums (Specified)
- ServiceDomain, SubscriberType, HouseholdType, PricingModel
- OrganizationType, OrganizationStatus
- ExemptionType, ExemptionReason, ExemptionStatus

---

## 2. Identified Gaps

### GAP-01: Business Partner / Payer Entity
**Priority**: HIGH

**Problem**: No entity to represent third parties who pay on behalf of subscribers:
- Social services (Sozialhilfe) paying fees for welfare recipients
- Employers paying health insurance premiums
- Cantons paying premium subsidies (Prämienverbilligung)
- Insurance companies paying for insured persons
- Family members paying for dependents

**Impact**: Cannot model who actually pays vs. who is the beneficiary

**See**: [Gap Analysis - Business Partner](./gaps/gap-01-business-partner.md)

---

### GAP-02: Suspension / Sistierung Framework
**Priority**: HIGH

**Problem**: No mechanism to temporarily pause subscriptions:
- Military service (Militärdienst) - up to several months
- Civil protection service (Zivilschutz)
- Moving/relocation transition periods
- Hospitalization / long-term care
- Extended foreign travel (>3 months)
- Study abroad
- Imprisonment

**Impact**: Cannot correctly model fee pauses and prorated billing

**See**: [Gap Analysis - Suspension](./gaps/gap-02-suspension.md)

---

### GAP-03: Person Status / Circumstances Extensions
**Priority**: MEDIUM

**Problem**: Current PersonStatus only covers: ACTIVE, DECEASED, EMIGRATED

Missing statuses/circumstances that affect fees:
- REFUGEE / ASYLUM_SEEKER - special fee handling
- MILITARY_SERVICE - suspension eligibility
- SOCIAL_WELFARE_RECIPIENT - third-party payment
- DIPLOMATIC_STAFF - exemption eligibility
- STUDENT - discount eligibility
- DISABLED_DEAF / DISABLED_BLIND - exemption eligibility

**Impact**: Cannot automatically apply correct rules based on person circumstances

**See**: [Gap Analysis - Person Circumstances](./gaps/gap-03-person-circumstances.md)

---

### GAP-04: Third-Party Payment Tracking
**Priority**: HIGH

**Problem**: No way to track when fees are paid by someone other than subscriber:
- Split payments (partial third-party)
- Payment guarantees
- Invoice routing to payer
- Payment history per payer

**Impact**: Cannot bill correct party or track subsidies correctly

**See**: [Gap Analysis - Third-Party Payments](./gaps/gap-04-third-party-payments.md)

---

### GAP-05: Exemption Verification Workflow
**Priority**: MEDIUM

**Problem**: ExemptionStatus exists but no supporting entities for:
- Document upload/storage
- Verification workflow steps
- Expiry notifications
- Re-verification scheduling
- Audit trail of decisions

**Impact**: Manual processes required, no compliance audit trail

**See**: [Gap Analysis - Exemption Workflow](./gaps/gap-05-exemption-workflow.md)

---

### GAP-06: Household Member Circumstances
**Priority**: MEDIUM

**Problem**: HouseholdMember only tracks role (PRIMARY, PARTNER, CHILD)

Missing:
- Member's disability status (for exemption calculations)
- Member's fee liability status
- Member's income contribution status
- Member's temporary absence (travel, military, study)

**Impact**: Cannot determine household-level exemption eligibility automatically

**See**: [Gap Analysis - Household Member](./gaps/gap-06-household-member.md)

---

### GAP-07: Telecom-Specific Models
**Priority**: LOW (Future)

**Problem**: Telecom requires additional concepts:
- Contract terms (minimum duration, notice period)
- Device bundles (phone + plan)
- Usage tracking (data, minutes)
- Roaming zones
- Number portability

**See**: [Gap Analysis - Telecom Models](./gaps/gap-07-telecom-models.md)

---

### GAP-08: Reductions, Surcharges & Payment Incentives
**Priority**: HIGH

**Problem**: No support for premium/fee adjustments:
- **IPV** (Individuelle Prämienverbilligung) - cantonal premium subsidies
- **Skonto** - prepayment discounts (1-2% for annual payment)
- **Malus** - late enrollment surcharge (KVG Art. 5: 30-50%, max 5 years)
- Other reductions (family, model, accident exclusion)

**Impact**: Cannot calculate correct net premiums, missing legal compliance for KVG Art. 5

**See**: [Gap Analysis - Reductions & Surcharges](./gaps/gap-08-reductions-surcharges.md)

---

## 3. Gap Resolution Approach

### Recommended Priority Order

1. **Phase 1A - Core Extensions** (before Phase 1)
   - GAP-03: PersonCircumstance entity
   - GAP-06: HouseholdMember extensions
   - GAP-02: Suspension entity

2. **Phase 1B - Payment Framework** (with Phase 2)
   - GAP-01: BusinessPartner entity
   - GAP-04: PaymentArrangement entity

3. **Phase 2A - Workflow** (with Phase 2)
   - GAP-05: ExemptionDocument, VerificationTask

4. **Phase 5+ - Telecom**
   - GAP-07: Contract terms, usage, devices

---

## 4. Cross-Reference Matrix

| Gap | Health | Broadcast | Telecom | Entities Needed |
|-----|--------|-----------|---------|-----------------|
| GAP-01 Business Partner | ✓ | ✓ | ✓ | BusinessPartner, PartnerType |
| GAP-02 Suspension | ✓ | - | ✓ | Suspension, SuspensionReason |
| GAP-03 Person Circumstances | ✓ | ✓ | - | PersonCircumstance, CircumstanceType |
| GAP-04 Third-Party Payment | ✓ | ✓ | - | PaymentArrangement, PayerType |
| GAP-05 Exemption Workflow | ✓ | ✓ | ✓ | ExemptionDocument, VerificationTask |
| GAP-06 Household Member | - | ✓ | - | (extend HouseholdMember) |
| GAP-07 Telecom | - | - | ✓ | Contract, DeviceBundle, UsageRecord |
| GAP-08 Reductions/Surcharges | ✓ | - | ✓ | PremiumAdjustment, EnrollmentSurcharge, PremiumSubsidy |

---

## 5. Next Steps

1. Create detailed specifications for each gap (./gaps/ folder)
2. Update new-enums-specification.md with additional enums
3. Update subscription-model-extension-plan.md with new phases
4. Review with stakeholders
5. Prioritize implementation

---

*Status: Draft*
*Author: Domain Analysis*

# Cost Sharing (Kostenbeteiligung)

## Overview

**Cost sharing** (Kostenbeteiligung) is the patient's financial participation in healthcare costs under KVG. It consists of two components: the **Franchise** (deductible) and the **Selbstbehalt** (co-payment).

> **German**: Kostenbeteiligung
> **French**: Participation aux coûts
> **Italian**: Partecipazione ai costi

---

## The Two Components

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         COST SHARING STRUCTURE                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  FRANCHISE (Jahresfranchise)                                        │   │
│  │  ─────────────────────────────                                      │   │
│  │  • Annual deductible: CHF 300 - 2,500 (adults)                     │   │
│  │  • Patient pays 100% until exhausted                               │   │
│  │  • Higher franchise = lower monthly premium                         │   │
│  │  • Resets every January 1st                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   ↓                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  SELBSTBEHALT (Co-payment)                                          │   │
│  │  ─────────────────────────────                                      │   │
│  │  • 10% of costs after franchise                                    │   │
│  │  • Annual maximum: CHF 700 (adults) / CHF 350 (children)           │   │
│  │  • Resets every January 1st                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                   ↓                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  FULL COVERAGE                                                       │   │
│  │  ─────────────────────────────                                      │   │
│  │  • Insurer pays 100% of remaining costs                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Annual Limits by Category

### Adults (18+)

| Component | Options | Annual Maximum |
|-----------|---------|----------------|
| **Franchise** | CHF 300, 500, 1000, 1500, 2000, 2500 | Selected amount |
| **Selbstbehalt** | 10% after franchise | **CHF 700** |
| **Combined Max** | Franchise + Selbstbehalt | CHF 300 + 700 = **CHF 1,000** (min) to CHF 2,500 + 700 = **CHF 3,200** (max) |

### Children (0-17)

| Component | Options | Annual Maximum |
|-----------|---------|----------------|
| **Franchise** | CHF 0, 100, 200, 300, 400, 500, 600 | Selected amount |
| **Selbstbehalt** | 10% after franchise | **CHF 350** |
| **Combined Max** | Franchise + Selbstbehalt | CHF 0 + 350 = **CHF 350** (min) to CHF 600 + 350 = **CHF 950** (max) |

---

## Calculation Algorithm

```
INPUT: approvedAmount, isMaternity, isAccident, account

# Step 1: Check exemptions
IF isMaternity THEN
    RETURN (franchiseApplied=0, selbstbehaltApplied=0, insurerPays=approvedAmount)
END IF

# Step 2: Calculate remaining capacity
franchiseRemaining = account.franchiseAmount - account.franchiseUsed
selbstbehaltRemaining = account.selbstbehaltMax - account.selbstbehaltUsed

# Step 3: Apply franchise (100%)
franchiseApplied = MIN(approvedAmount, franchiseRemaining)
afterFranchise = approvedAmount - franchiseApplied

# Step 4: Apply Selbstbehalt (10%, capped)
IF afterFranchise > 0 THEN
    tenPercent = afterFranchise * 0.10
    selbstbehaltApplied = MIN(tenPercent, selbstbehaltRemaining)
ELSE
    selbstbehaltApplied = 0
END IF

# Step 5: Calculate final amounts
patientShare = franchiseApplied + selbstbehaltApplied
insurerPays = approvedAmount - patientShare

# Step 6: Update account
account.franchiseUsed += franchiseApplied
account.selbstbehaltUsed += selbstbehaltApplied

RETURN (franchiseApplied, selbstbehaltApplied, patientShare, insurerPays)
```

---

## Worked Examples

### Example 1: First Claim of Year

```
Patient: Adult with CHF 1,500 franchise (nothing used yet)
Claim: CHF 800 approved

Franchise remaining: CHF 1,500 - 0 = CHF 1,500
Selbstbehalt remaining: CHF 700 - 0 = CHF 700

Step 1: Apply franchise
  franchiseApplied = MIN(800, 1500) = CHF 800

Step 2: Apply Selbstbehalt
  afterFranchise = 800 - 800 = CHF 0
  selbstbehaltApplied = CHF 0

RESULT:
  Patient pays: CHF 800 (franchise)
  Insurer pays: CHF 0

Account after:
  franchiseUsed: CHF 800
  selbstbehaltUsed: CHF 0
```

### Example 2: Franchise Exhausted Mid-Claim

```
Patient: Adult with CHF 1,500 franchise (CHF 1,200 used)
Claim: CHF 2,000 approved

Franchise remaining: CHF 1,500 - 1,200 = CHF 300
Selbstbehalt remaining: CHF 700 - 0 = CHF 700

Step 1: Apply franchise
  franchiseApplied = MIN(2000, 300) = CHF 300

Step 2: Apply Selbstbehalt
  afterFranchise = 2000 - 300 = CHF 1,700
  tenPercent = 1700 * 0.10 = CHF 170
  selbstbehaltApplied = MIN(170, 700) = CHF 170

RESULT:
  Patient pays: CHF 300 + 170 = CHF 470
  Insurer pays: CHF 1,530

Account after:
  franchiseUsed: CHF 1,500 (exhausted)
  selbstbehaltUsed: CHF 170
```

### Example 3: All Cost Sharing Exhausted

```
Patient: Adult with CHF 300 franchise (exhausted)
         Selbstbehalt: CHF 700 used (exhausted)
Claim: CHF 5,000 approved

Franchise remaining: CHF 0
Selbstbehalt remaining: CHF 0

RESULT:
  Patient pays: CHF 0
  Insurer pays: CHF 5,000

Account unchanged (already at maximum)
```

### Example 4: Maternity (Exempt)

```
Patient: Adult, claim marked as maternity
Claim: CHF 8,000 approved

Maternity exemption applies regardless of account status.

RESULT:
  Patient pays: CHF 0
  Insurer pays: CHF 8,000

Account unchanged (maternity doesn't count toward limits)
```

---

## Exemptions from Cost Sharing

### Full Exemptions

| Situation | Franchise | Selbstbehalt | Legal Basis |
|-----------|-----------|--------------|-------------|
| **Maternity** | Exempt | Exempt | Art. 64 Abs. 7 KVG |
| Prenatal care (from week 13) | Exempt | Exempt | Art. 64 Abs. 7 KVG |
| Childbirth | Exempt | Exempt | Art. 64 Abs. 7 KVG |
| Postnatal (8 weeks) | Exempt | Exempt | Art. 64 Abs. 7 KVG |

### Partial Exemptions

| Situation | Franchise | Selbstbehalt | Notes |
|-----------|-----------|--------------|-------|
| Specific preventive services | Reduced | Reduced | Per BAG list |
| Some chronic disease programs | Normal | Reduced 10% → 20% | Disease management |

---

## Family Cost Sharing

### Individual Tracking

Each family member has their own cost sharing account:

```
Household: Müller Family
├── Thomas (Adult)
│   └── CostSharingAccount: Franchise CHF 2,500, Selbstbehalt max CHF 700
├── Anna (Adult)
│   └── CostSharingAccount: Franchise CHF 300, Selbstbehalt max CHF 700
└── Max (Child, age 8)
    └── CostSharingAccount: Franchise CHF 0, Selbstbehalt max CHF 350
```

### Family Selbstbehalt Cap

The total Selbstbehalt for children in a family is capped at 2x individual child maximum:

```
Family with 3 children:
  Individual max per child: CHF 350
  Family cap: 2 × CHF 350 = CHF 700

If Child 1 pays CHF 350 and Child 2 pays CHF 350:
  Total = CHF 700 (cap reached)
  Child 3 pays CHF 0 Selbstbehalt for rest of year
```

---

## Timing and Resets

### Annual Reset

```
December 31, 2025                    January 1, 2026
────────────────────────────────────────────────────────────
Account Status (Year End)         Account Status (Year Start)
─────────────────────────         ──────────────────────────
franchiseUsed: CHF 2,500          franchiseUsed: CHF 0
selbstbehaltUsed: CHF 700         selbstbehaltUsed: CHF 0
franchiseExhausted: true          franchiseExhausted: false
selbstbehaltExhausted: true       selbstbehaltExhausted: false
```

### Mid-Year Coverage Start

If coverage starts mid-year, cost sharing applies from that date:

```
Coverage start: July 1, 2026
Period: July 1 - December 31, 2026

Full franchise and Selbstbehalt limits apply for this partial year.
(No pro-rating of limits)
```

---

## Service Date vs. Invoice Date

Cost sharing is applied based on **service date**, not invoice date:

```
Service Date: December 15, 2025
Invoice Date: January 10, 2026
Processing Date: January 20, 2026

Cost sharing applies to 2025 account (based on service date)
```

---

## Code Reference

### CostSharingService

```java
@Service
public class CostSharingService {

    public CostSharingResult applyCostSharing(Claim claim) {
        if (claim.isMaternity()) {
            return CostSharingResult.exempt(claim.getApprovedAmount());
        }

        CostSharingAccount account = accountRepository
            .findByPersonAndCoverageAndYear(
                claim.getPersonId(),
                claim.getCoverageId(),
                claim.getServiceDate().getYear()
            ).orElseThrow();

        Money franchiseRemaining = account.getFranchiseAmount()
            .subtract(account.getFranchiseUsed());
        Money selbstbehaltRemaining = account.getSelbstbehaltMax()
            .subtract(account.getSelbstbehaltUsed());

        // Apply franchise
        Money franchiseApplied = Money.min(
            claim.getApprovedAmount(),
            franchiseRemaining
        );

        Money afterFranchise = claim.getApprovedAmount()
            .subtract(franchiseApplied);

        // Apply Selbstbehalt
        Money tenPercent = afterFranchise.multiply(new BigDecimal("0.10"));
        Money selbstbehaltApplied = Money.min(tenPercent, selbstbehaltRemaining);

        // Calculate final amounts
        Money patientShare = franchiseApplied.add(selbstbehaltApplied);
        Money insurerPays = claim.getApprovedAmount().subtract(patientShare);

        // Update account
        account.setFranchiseUsed(account.getFranchiseUsed().add(franchiseApplied));
        account.setSelbstbehaltUsed(account.getSelbstbehaltUsed().add(selbstbehaltApplied));
        accountRepository.save(account);

        return new CostSharingResult(
            franchiseApplied,
            selbstbehaltApplied,
            patientShare,
            insurerPays
        );
    }
}
```

---

## Legal References

| Article | Topic |
|---------|-------|
| **Art. 64 KVG** | Cost sharing regulations |
| Art. 64 Abs. 2 KVG | Franchise amounts |
| Art. 64 Abs. 3 KVG | Selbstbehalt rate (10%) |
| Art. 64 Abs. 4 KVG | Selbstbehalt maximum |
| **Art. 64 Abs. 7 KVG** | Maternity exemption |
| Art. 103 KVV | Children's cost sharing |

---

## Related Documentation

- [Franchise System](./franchise-system.md) - Detailed franchise options
- [CostSharingAccount Entity](../entities/contract/cost-sharing-account.md) - Account tracking
- [Claim Entity](../entities/claims/claim.md) - Claim with cost sharing
- [KVG Mandatory Insurance](./kvg-mandatory-insurance.md) - Insurance context

---

*Last Updated: 2026-01-28*

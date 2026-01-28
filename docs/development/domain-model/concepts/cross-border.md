# Cross-Border Cases (Grenzüberschreitende Fälle)

## Overview

**Cross-border cases** involve healthcare coverage for persons living or working across Swiss borders. This includes cross-border workers (Grenzgänger), residents in special territories, and persons with coverage coordination under EU/EFTA agreements.

> **German**: Grenzüberschreitende Fälle, Grenzgänger
> **French**: Cas transfrontaliers, Frontaliers
> **Italian**: Casi transfrontalieri, Frontalieri

---

## Categories of Cross-Border Situations

### 1. Cross-Border Workers (Grenzgänger)

Persons who work in Switzerland but live in a neighboring country:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           GRENZGÄNGER                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  RESIDENCE                              WORK                                │
│  ─────────                              ────                                │
│  • Germany (DE)                         • Switzerland (CH)                  │
│  • France (FR)                          • Employment with Swiss employer    │
│  • Italy (IT)                           • Subject to Swiss social security  │
│  • Austria (AT)                                                             │
│                                                                             │
│  INSURANCE OPTIONS (Optionsrecht)                                           │
│  ─────────────────────────────────                                          │
│  1. Swiss KVG insurance (default)                                           │
│  2. Exemption to use home country insurance                                 │
│     (must apply within 3 months of starting work)                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2. Posted Workers (Entsandte Arbeitnehmer)

Temporarily assigned to Switzerland from another country:

| Situation | Insurance | Duration |
|-----------|-----------|----------|
| Posted from EU/EFTA | Home country | Max 24 months |
| Posted from non-EU | Often Swiss KVG | Varies |

### 3. Multi-State Workers

Working in multiple countries simultaneously:

```
Coverage determined by:
1. Country of residence (if substantial activity there)
2. Country of employer's registered office
3. Coordination via A1/E101 certificate
```

---

## Special Territories

### Liechtenstein (FL)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        FÜRSTENTUM LIECHTENSTEIN                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  STATUS: EEA member, not EU member, currency CHF                            │
│                                                                             │
│  HEALTHCARE:                                                                │
│  • Own mandatory insurance system (KVG-FL)                                  │
│  • Similar to Swiss KVG but separate                                        │
│  • Mutual recognition of coverage                                           │
│                                                                             │
│  CROSS-BORDER:                                                              │
│  • Many FL residents work in CH                                             │
│  • Many CH providers treat FL patients                                      │
│  • Settlement via coordination office                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Büsingen am Hochrhein (DE)

German exclave entirely surrounded by Swiss territory:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           BÜSINGEN (DE)                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  SOVEREIGNTY: German                                                        │
│  CUSTOMS: Swiss customs area                                                │
│  CURRENCY: CHF (mainly) and EUR                                             │
│                                                                             │
│  HEALTHCARE:                                                                │
│  • Residents can choose CH or DE insurance                                  │
│  • Special agreements for healthcare access                                 │
│  • Swiss providers commonly used                                            │
│                                                                             │
│  POPULATION: ~1,500                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Campione d'Italia (IT)

Italian exclave within Swiss territory (Ticino):

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        CAMPIONE D'ITALIA (IT)                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  SOVEREIGNTY: Italian                                                       │
│  CUSTOMS: Swiss customs area (since 2020)                                   │
│  CURRENCY: CHF (since 2020)                                                 │
│                                                                             │
│  HEALTHCARE:                                                                │
│  • Italian SSN (Servizio Sanitario Nazionale)                               │
│  • Swiss providers often used due to proximity                              │
│  • Complex reimbursement procedures                                         │
│                                                                             │
│  POPULATION: ~1,900                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Samnaun (CH) - Duty-Free Zone

Swiss municipality with special customs status:

```
Not a cross-border case per se, but:
• Duty-free zone
• Special VAT rules
• Normal Swiss KVG applies
```

---

## Options Right (Optionsrecht)

Cross-border workers from certain countries have the right to choose their insurance system:

### Eligible Countries

| Country | Options Right | Deadline |
|---------|--------------|----------|
| Germany | Yes | 3 months from work start |
| France | Yes | 3 months from work start |
| Italy | Yes | 3 months from work start |
| Austria | Yes | 3 months from work start |
| Liechtenstein | Special agreement | Varies |

### Exercising the Option

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       OPTIONS RIGHT PROCESS                                 │
└─────────────────────────────────────────────────────────────────────────────┘

Day 0: Start employment in Switzerland
         │
         ▼
     ┌───────────────────────────────────────────┐
     │  DEFAULT: Subject to Swiss KVG            │
     │  Must obtain Swiss insurance within       │
     │  3 months of starting work                │
     └───────────────────────────────────────────┘
         │
         │ Within 3 months
         ▼
     ┌───────────────────────────────────────────┐
     │  OPTION: Request exemption from KVG       │
     │  • Submit to cantonal authority           │
     │  • Prove equivalent home country coverage │
     │  • Family members also covered            │
     └───────────────────────────────────────────┘
         │
         ├─────────────────────┬────────────────────┐
         ▼                     ▼                    ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│ Option A:       │   │ Option B:       │   │ Deadline passed:│
│ Swiss KVG       │   │ Home country    │   │ Swiss KVG       │
│ (default)       │   │ insurance       │   │ mandatory       │
└─────────────────┘   │ (with exemption)│   └─────────────────┘
                      └─────────────────┘
```

---

## Entity Model

### CrossBorderStatus

```java
@Entity
@Table(name = "cross_border_statuses")
public class CrossBorderStatus {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    // Classification
    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", nullable = false)
    private CrossBorderType statusType;

    // Work location
    @Column(name = "work_country", length = 2)
    private String workCountry;  // ISO 3166-1 alpha-2

    @Column(name = "work_canton", length = 2)
    private String workCanton;

    @Column(name = "employer_name")
    private String employerName;

    // Residence
    @Column(name = "residence_country", length = 2, nullable = false)
    private String residenceCountry;

    @Column(name = "residence_region")
    private String residenceRegion;

    // Options right
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_choice")
    private InsuranceChoice insuranceChoice;

    @Column(name = "exemption_granted")
    private boolean exemptionGranted;

    @Column(name = "exemption_date")
    private LocalDate exemptionDate;

    @Column(name = "exemption_reference")
    private String exemptionReference;

    // Coordination
    @Column(name = "a1_certificate_number")
    private String a1CertificateNumber;

    @Column(name = "a1_valid_from")
    private LocalDate a1ValidFrom;

    @Column(name = "a1_valid_to")
    private LocalDate a1ValidTo;

    // Foreign insurer (if exempted)
    @Column(name = "foreign_insurer_name")
    private String foreignInsurerName;

    @Column(name = "foreign_policy_number")
    private String foreignPolicyNumber;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrossBorderStatusState status;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private long version;
}
```

### Enums

```java
public enum CrossBorderType {
    GRENZGAENGER("Grenzgänger"),           // Cross-border worker
    POSTED_WORKER("Entsandter"),           // Posted worker
    MULTI_STATE("Mehrstaatlich tätig"),    // Multi-state worker
    LIECHTENSTEIN("FL-Versicherter"),       // Liechtenstein resident
    BUESINGEN("Büsingen-Einwohner"),       // Büsingen resident
    CAMPIONE("Campione-Einwohner"),        // Campione resident
    PENSIONER_ABROAD("Rentner im Ausland"); // Swiss pensioner abroad
}

public enum InsuranceChoice {
    SWISS_KVG("Schweizer KVG"),
    HOME_COUNTRY("Wohnsitzland"),
    LIECHTENSTEIN_KVG("Liechtenstein KVG");
}

public enum CrossBorderStatusState {
    PENDING("Ausstehend"),
    ACTIVE("Aktiv"),
    EXEMPTED("Befreit"),
    EXPIRED("Abgelaufen"),
    TERMINATED("Beendet");
}
```

---

## Coverage Coordination

### EU/EFTA Coordination Rules

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     EU REGULATION 883/2004                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  PRINCIPLE: One country's social security system at a time                  │
│                                                                             │
│  DETERMINATION:                                                             │
│  1. Country of work (generally)                                             │
│  2. Country of residence (for multi-state workers)                          │
│  3. Country of employer's seat (if no residence activity)                   │
│                                                                             │
│  DOCUMENTATION:                                                             │
│  • A1 Certificate: Proof of applicable legislation                          │
│  • S1 Form: Entitlement to healthcare in residence country                  │
│  • EHIC: European Health Insurance Card for temporary stays                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Healthcare Access for Cross-Border Workers

```java
public class CrossBorderHealthcareAccess {

    // Worker with Swiss KVG living in France
    public Set<Country> treatmentCountries(CrossBorderStatus status) {
        if (status.getInsuranceChoice() == InsuranceChoice.SWISS_KVG) {
            return Set.of(
                Country.CH,  // Full access
                Country.FR   // Access in residence country (with S1)
            );
        } else {
            return Set.of(
                Country.FR,  // Full access (home insurance)
                Country.CH   // Emergency only or with prior authorization
            );
        }
    }
}
```

---

## Premium Implications

### Cross-Border Worker Premiums

| Scenario | Premium Region | Cost Sharing |
|----------|---------------|--------------|
| Swiss KVG, works in CH | Swiss rates | Swiss rules |
| Swiss KVG, lives in FR | Usually lower | Swiss rules |
| Home country insurance | Home country rates | Home country rules |

### Special Premium Calculations

```java
public Money calculateGrenzgaengerPremium(CrossBorderStatus status) {
    if (status.getInsuranceChoice() == InsuranceChoice.SWISS_KVG) {
        // Special rates often apply for cross-border workers
        // Based on residence country cost levels
        return premiumService.getGrenzgaengerRate(
            status.getResidenceCountry(),
            status.getWorkCanton(),
            status.getPerson().getAgeGroup()
        );
    }
    return Money.ZERO;  // Not applicable if exempted
}
```

---

## Administrative Processes

### KVG Exemption Request (Grenzgänger)

```
Documents required:
├── Employment contract (Swiss employer)
├── Proof of residence abroad
├── Proof of foreign health insurance coverage
├── Family status documentation
└── Completed exemption application form

Submit to:
└── Cantonal health department (Gesundheitsdirektion)
    of work canton

Timeline:
├── Deadline: 3 months from start of employment
├── Processing: 2-4 weeks typically
└── Effective: Retroactive to employment start if approved
```

### End of Cross-Border Status

```java
@Service
public class CrossBorderStatusService {

    public void handleEndOfEmployment(CrossBorderStatus status) {
        status.setValidTo(LocalDate.now());
        status.setStatus(CrossBorderStatusState.TERMINATED);

        if (status.getExemptionGranted()) {
            // Person may now need Swiss KVG if staying in CH
            // or is no longer subject to Swiss insurance
            notifyComplianceTeam(status);
        }

        // Update coverage accordingly
        coverageService.reviewCoverage(status.getPersonId());
    }
}
```

---

## Related Documentation

- [Person Entity](../entities/masterdata/person.md) - Person with nationality/residence
- [Coverage Entity](../entities/contract/coverage.md) - Coverage details
- [Swiss Healthcare System](./swiss-healthcare-system.md) - Overall system
- [KVG Mandatory Insurance](./kvg-mandatory-insurance.md) - Insurance obligation

---

*Last Updated: 2026-01-28*

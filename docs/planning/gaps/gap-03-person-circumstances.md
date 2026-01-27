# GAP-03: Person Status / Circumstances Extensions

## Problem Statement

The current `PersonStatus` enum only covers: `ACTIVE`, `DECEASED`, `EMIGRATED`

This is insufficient to model **circumstances** that affect:
- Fee exemptions (deaf/blind, refugee status)
- Pricing rules (student discounts, senior rates)
- Suspension eligibility (military service)
- Third-party payment eligibility (social welfare)

## Key Insight: Status vs. Circumstance

**Status** = lifecycle state (mutually exclusive)
- ACTIVE, DECEASED, EMIGRATED

**Circumstance** = current situation (can have multiple)
- MILITARY_SERVICE, STUDENT, DISABILITY, REFUGEE, etc.

These should be modeled separately!

---

## Proposed Model

### Keep PersonStatus (Lifecycle)

```java
public enum PersonStatus {
    ACTIVE("Aktiv"),           // Living and resident
    DECEASED("Verstorben"),    // Passed away
    EMIGRATED("Ausgewandert"), // Left Switzerland
    UNKNOWN("Unbekannt");      // Status unknown

    // ... existing implementation
}
```

### New: PersonCircumstance Entity

```java
@Entity
@Table(name = "person_circumstances")
public class PersonCircumstance {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;  // FK to Person

    @Enumerated(EnumType.STRING)
    @Column(name = "circumstance_type", nullable = false)
    private CircumstanceType circumstanceType;

    @Column(name = "circumstance_detail")
    private String circumstanceDetail;  // Additional info

    // Validity (temporal)
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;  // null = indefinite

    // Verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "verified_at")
    private LocalDate verifiedAt;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "next_verification_due")
    private LocalDate nextVerificationDue;

    // Documentation
    @Column(name = "certificate_number")
    private String certificateNumber;  // External reference

    @Column(name = "document_id")
    private UUID documentId;  // FK to uploaded document

    @Column(name = "issuing_authority")
    private String issuingAuthority;  // Who issued certificate

    // Audit
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private long version;

    // Business methods
    public boolean isCurrent() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(validFrom) &&
               (validTo == null || !today.isAfter(validTo));
    }

    public boolean isVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }
}
```

### CircumstanceType Enum

```java
public enum CircumstanceType {

    // Disability-related
    DEAF("Gehörlos", CircumstanceCategory.DISABILITY),
    BLIND("Blind", CircumstanceCategory.DISABILITY),
    DEAF_BLIND("Taubblind", CircumstanceCategory.DISABILITY),
    PHYSICALLY_DISABLED("Körperbehindert", CircumstanceCategory.DISABILITY),
    MENTALLY_DISABLED("Geistig behindert", CircumstanceCategory.DISABILITY),

    // Social/Financial
    SOCIAL_WELFARE_RECIPIENT("Sozialhilfe-Empfänger", CircumstanceCategory.SOCIAL),
    EL_RECIPIENT("EL-Bezüger", CircumstanceCategory.SOCIAL),
    LOW_INCOME("Einkommensschwach", CircumstanceCategory.SOCIAL),
    UNEMPLOYED("Arbeitslos", CircumstanceCategory.SOCIAL),

    // Residence status
    REFUGEE("Flüchtling", CircumstanceCategory.RESIDENCE),
    ASYLUM_SEEKER("Asylsuchend", CircumstanceCategory.RESIDENCE),
    PROVISIONALLY_ADMITTED("Vorläufig aufgenommen", CircumstanceCategory.RESIDENCE),
    DIPLOMATIC_STATUS("Diplomatenstatus", CircumstanceCategory.RESIDENCE),
    FOREIGN_RESIDENT("Ausländer", CircumstanceCategory.RESIDENCE),

    // Service
    MILITARY_SERVICE("Militärdienst", CircumstanceCategory.SERVICE),
    CIVIL_PROTECTION("Zivilschutzdienst", CircumstanceCategory.SERVICE),
    CIVIL_SERVICE("Zivildienst", CircumstanceCategory.SERVICE),

    // Education
    STUDENT("Student", CircumstanceCategory.EDUCATION),
    APPRENTICE("Lehrling", CircumstanceCategory.EDUCATION),
    DOCTORAL_CANDIDATE("Doktorand", CircumstanceCategory.EDUCATION),

    // Employment
    EMPLOYED("Erwerbstätig", CircumstanceCategory.EMPLOYMENT),
    SELF_EMPLOYED("Selbständig", CircumstanceCategory.EMPLOYMENT),
    RETIRED("Pensioniert", CircumstanceCategory.EMPLOYMENT),
    HOMEMAKER("Hausfrau/Hausmann", CircumstanceCategory.EMPLOYMENT),

    // Life situation
    HOSPITALIZED("Hospitalisiert", CircumstanceCategory.HEALTH),
    LONG_TERM_CARE("Langzeitpflege", CircumstanceCategory.HEALTH),
    IMPRISONED("Inhaftiert", CircumstanceCategory.LEGAL),
    IN_INSTITUTION("In Institution", CircumstanceCategory.RESIDENCE);

    private final String nameDe;
    private final CircumstanceCategory category;

    CircumstanceType(String nameDe, CircumstanceCategory category) {
        this.nameDe = nameDe;
        this.category = category;
    }

    public CircumstanceCategory getCategory() {
        return category;
    }

    /**
     * Returns true if this circumstance grants fee exemption for given domain.
     */
    public boolean grantsExemption(ServiceDomain domain) {
        if (domain == ServiceDomain.BROADCAST) {
            return switch (this) {
                case EL_RECIPIENT, DEAF_BLIND, DIPLOMATIC_STATUS -> true;
                default -> false;
            };
        }
        return false;
    }

    /**
     * Returns true if this circumstance requires periodic re-verification.
     */
    public boolean requiresPeriodicVerification() {
        return switch (this) {
            case EL_RECIPIENT -> true;  // Every 3 years
            case STUDENT, APPRENTICE -> true;  // Every semester
            case UNEMPLOYED -> true;  // Every 6 months
            case MILITARY_SERVICE, CIVIL_SERVICE -> false;  // End date known
            case DEAF, BLIND, DEAF_BLIND -> false;  // Permanent
            default -> false;
        };
    }

    /**
     * Returns recommended verification interval in months.
     */
    public Integer getVerificationIntervalMonths() {
        return switch (this) {
            case EL_RECIPIENT -> 36;      // 3 years
            case STUDENT, APPRENTICE -> 6; // Every semester
            case UNEMPLOYED -> 6;
            case LOW_INCOME -> 12;
            default -> null;  // No periodic verification
        };
    }
}
```

### CircumstanceCategory Enum

```java
public enum CircumstanceCategory {
    DISABILITY("Behinderung"),
    SOCIAL("Soziales"),
    RESIDENCE("Aufenthalt"),
    SERVICE("Dienst"),
    EDUCATION("Ausbildung"),
    EMPLOYMENT("Erwerbstätigkeit"),
    HEALTH("Gesundheit"),
    LEGAL("Rechtlich");

    private final String nameDe;

    CircumstanceCategory(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

### VerificationStatus Enum

```java
public enum VerificationStatus {
    UNVERIFIED("Ungeprüft"),      // Not yet verified
    PENDING("Prüfung ausstehend"), // Verification in progress
    VERIFIED("Verifiziert"),      // Confirmed valid
    EXPIRED("Abgelaufen"),        // Verification expired
    INVALID("Ungültig"),          // Document rejected
    REVOKED("Widerrufen");        // Previously valid, now revoked

    private final String nameDe;

    VerificationStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## Extended Person Entity

Add methods to Person for accessing circumstances:

```java
public class Person {
    // ... existing fields and methods ...

    @OneToMany(mappedBy = "personId", cascade = CascadeType.ALL)
    private List<PersonCircumstance> circumstances = new ArrayList<>();

    /**
     * Returns all currently active circumstances.
     */
    public List<PersonCircumstance> currentCircumstances() {
        return circumstances.stream()
            .filter(PersonCircumstance::isCurrent)
            .collect(Collectors.toList());
    }

    /**
     * Returns current circumstances of given category.
     */
    public List<PersonCircumstance> currentCircumstances(CircumstanceCategory category) {
        return currentCircumstances().stream()
            .filter(c -> c.getCircumstanceType().getCategory() == category)
            .collect(Collectors.toList());
    }

    /**
     * Checks if person currently has specific circumstance.
     */
    public boolean hasCircumstance(CircumstanceType type) {
        return currentCircumstances().stream()
            .anyMatch(c -> c.getCircumstanceType() == type);
    }

    /**
     * Checks if person qualifies for exemption in given domain.
     */
    public boolean qualifiesForExemption(ServiceDomain domain) {
        return currentCircumstances().stream()
            .filter(PersonCircumstance::isVerified)
            .anyMatch(c -> c.getCircumstanceType().grantsExemption(domain));
    }

    /**
     * Returns circumstances requiring re-verification soon.
     */
    public List<PersonCircumstance> circumstancesNeedingVerification(int withinDays) {
        LocalDate threshold = LocalDate.now().plusDays(withinDays);
        return currentCircumstances().stream()
            .filter(c -> c.getNextVerificationDue() != null)
            .filter(c -> !c.getNextVerificationDue().isAfter(threshold))
            .collect(Collectors.toList());
    }
}
```

---

## Example Use Cases

### 1. EL Recipient (Broadcast Fee Exemption)

```java
PersonCircumstance elStatus = new PersonCircumstance();
elStatus.setPersonId(person.getId());
elStatus.setCircumstanceType(CircumstanceType.EL_RECIPIENT);
elStatus.setValidFrom(LocalDate.of(2026, 1, 1));
elStatus.setVerificationStatus(VerificationStatus.VERIFIED);
elStatus.setVerifiedAt(LocalDate.of(2025, 12, 15));
elStatus.setCertificateNumber("AK-ZH-2025-123456");
elStatus.setIssuingAuthority("Ausgleichskasse Zürich");
elStatus.setNextVerificationDue(LocalDate.of(2028, 12, 31));  // 3 years
```

### 2. Deaf-Blind Household Member

```java
PersonCircumstance deafBlind = new PersonCircumstance();
deafBlind.setPersonId(person.getId());
deafBlind.setCircumstanceType(CircumstanceType.DEAF_BLIND);
deafBlind.setValidFrom(LocalDate.of(2020, 5, 1));
deafBlind.setValidTo(null);  // Permanent
deafBlind.setVerificationStatus(VerificationStatus.VERIFIED);
deafBlind.setDocumentId(medicalCertificateId);
deafBlind.setIssuingAuthority("Dr. med. Müller, Zürich");
```

### 3. Refugee Status

```java
PersonCircumstance refugee = new PersonCircumstance();
refugee.setPersonId(person.getId());
refugee.setCircumstanceType(CircumstanceType.REFUGEE);
refugee.setCircumstanceDetail("Anerkannter Flüchtling gem. AsylG");
refugee.setValidFrom(LocalDate.of(2025, 8, 1));
refugee.setVerificationStatus(VerificationStatus.VERIFIED);
refugee.setCertificateNumber("SEM-2025-789012");
refugee.setIssuingAuthority("Staatssekretariat für Migration");
```

### 4. Military Service (Temporary)

```java
PersonCircumstance military = new PersonCircumstance();
military.setPersonId(person.getId());
military.setCircumstanceType(CircumstanceType.MILITARY_SERVICE);
military.setCircumstanceDetail("RS Inf 4, Kaserne Thun");
military.setValidFrom(LocalDate.of(2026, 7, 1));
military.setValidTo(LocalDate.of(2026, 10, 31));  // End of RS
military.setVerificationStatus(VerificationStatus.VERIFIED);
military.setCertificateNumber("MIL-2026-345678");
military.setIssuingAuthority("Schweizer Armee");
```

---

## Business Rules

### Broadcast Fee Exemption Check

```java
public class BroadcastFeeExemptionChecker {

    public ExemptionResult checkHouseholdExemption(Household household) {
        List<Person> members = household.getCurrentMembers();

        // Check 1: Any member is EL recipient
        for (Person member : members) {
            if (member.hasCircumstance(CircumstanceType.EL_RECIPIENT)) {
                PersonCircumstance el = member.currentCircumstances().stream()
                    .filter(c -> c.getCircumstanceType() == CircumstanceType.EL_RECIPIENT)
                    .filter(PersonCircumstance::isVerified)
                    .findFirst()
                    .orElse(null);

                if (el != null) {
                    return new ExemptionResult(
                        true,
                        ExemptionReason.AHV_IV_SUPPLEMENT,
                        "EL-Bezug bestätigt: " + el.getCertificateNumber()
                    );
                }
            }
        }

        // Check 2: ALL members are deaf-blind
        boolean allDeafBlind = members.stream()
            .allMatch(m -> m.hasCircumstance(CircumstanceType.DEAF_BLIND));

        if (allDeafBlind && !members.isEmpty()) {
            return new ExemptionResult(
                true,
                ExemptionReason.DEAF_BLIND,
                "Alle Haushaltsmitglieder sind taubblind"
            );
        }

        // Check 3: Any member has diplomatic status
        for (Person member : members) {
            if (member.hasCircumstance(CircumstanceType.DIPLOMATIC_STATUS)) {
                return new ExemptionResult(
                    true,
                    ExemptionReason.DIPLOMATIC_STATUS,
                    "Diplomatenstatus"
                );
            }
        }

        return new ExemptionResult(false, null, null);
    }
}
```

---

## Database Schema

```sql
CREATE TABLE person_circumstances (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL REFERENCES persons(id),
    circumstance_type VARCHAR(30) NOT NULL,
    circumstance_detail TEXT,
    valid_from DATE NOT NULL,
    valid_to DATE,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'UNVERIFIED',
    verified_at DATE,
    verified_by UUID,
    next_verification_due DATE,
    certificate_number VARCHAR(50),
    document_id UUID,
    issuing_authority VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_pc_person ON person_circumstances(person_id);
CREATE INDEX idx_pc_type ON person_circumstances(circumstance_type);
CREATE INDEX idx_pc_valid ON person_circumstances(valid_from, valid_to);
CREATE INDEX idx_pc_verification ON person_circumstances(verification_status, next_verification_due);
```

---

## Integration Notes

1. **Exemption Service**: Query circumstances when checking exemption eligibility
2. **Notification Service**: Send reminders for expiring verifications
3. **Billing Service**: Apply discounts based on circumstances
4. **Audit Trail**: Track all circumstance changes for compliance
5. **Reporting**: Generate statistics on circumstances for regulatory reporting

---

*Status: Draft*
*Priority: MEDIUM*

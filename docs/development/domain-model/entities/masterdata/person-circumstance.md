# PersonCircumstance Entity

## Overview

The **PersonCircumstance** entity tracks circumstances that affect a person's insurance fees, exemptions, or eligibility.

> **German**: Personenumstand
> **Module**: `govinda-masterdata`
> **Status**: ⏳ Planned

**Key Insight**: Circumstances are separate from PersonStatus (lifecycle). A person can have multiple concurrent circumstances.

**Examples**:
- EL recipient (EL-Bezüger) - broadcast fee exemption
- Deaf-blind (Taubblind) - broadcast fee exemption
- Refugee (Flüchtling) - special fee handling
- Military service (Militärdienst) - suspension eligibility

---

## Entity Definition

```java
@Entity
@Table(name = "person_circumstances")
public class PersonCircumstance {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "circumstance_type", nullable = false)
    private CircumstanceType circumstanceType;

    @Column(name = "circumstance_detail")
    private String circumstanceDetail;

    // Validity
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;

    @Column(name = "verified_at")
    private LocalDate verifiedAt;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "next_verification_due")
    private LocalDate nextVerificationDue;

    // Documentation
    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    // Audit
    @Column(name = "created_at")
    private Instant createdAt;

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

---

## Field Reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | UUID | ✅ | Unique identifier |
| `tenantId` | UUID | ✅ | Multi-tenant isolation |
| `personId` | UUID | ✅ | FK to Person |
| `circumstanceType` | CircumstanceType | ✅ | Type of circumstance |
| `circumstanceDetail` | String | ❌ | Additional details |
| `validFrom` | LocalDate | ✅ | Start of validity |
| `validTo` | LocalDate | ❌ | End of validity (null = indefinite) |
| `verificationStatus` | VerificationStatus | ✅ | Verification state |
| `verifiedAt` | LocalDate | ❌ | When verified |
| `nextVerificationDue` | LocalDate | ❌ | Re-verification date |
| `certificateNumber` | String | ❌ | External certificate reference |
| `issuingAuthority` | String | ❌ | Who issued the certificate |

---

## Related Enums

### CircumstanceType

```java
public enum CircumstanceType {
    // Disability
    DEAF("Gehörlos", CircumstanceCategory.DISABILITY),
    BLIND("Blind", CircumstanceCategory.DISABILITY),
    DEAF_BLIND("Taubblind", CircumstanceCategory.DISABILITY),

    // Social/Financial
    SOCIAL_WELFARE_RECIPIENT("Sozialhilfe-Empfänger", CircumstanceCategory.SOCIAL),
    EL_RECIPIENT("EL-Bezüger", CircumstanceCategory.SOCIAL),
    LOW_INCOME("Einkommensschwach", CircumstanceCategory.SOCIAL),
    UNEMPLOYED("Arbeitslos", CircumstanceCategory.SOCIAL),

    // Residence
    REFUGEE("Flüchtling", CircumstanceCategory.RESIDENCE),
    ASYLUM_SEEKER("Asylsuchend", CircumstanceCategory.RESIDENCE),
    PROVISIONALLY_ADMITTED("Vorläufig aufgenommen", CircumstanceCategory.RESIDENCE),
    DIPLOMATIC_STATUS("Diplomatenstatus", CircumstanceCategory.RESIDENCE),

    // Service
    MILITARY_SERVICE("Militärdienst", CircumstanceCategory.SERVICE),
    CIVIL_PROTECTION("Zivilschutzdienst", CircumstanceCategory.SERVICE),
    CIVIL_SERVICE("Zivildienst", CircumstanceCategory.SERVICE),

    // Education
    STUDENT("Student", CircumstanceCategory.EDUCATION),
    APPRENTICE("Lehrling", CircumstanceCategory.EDUCATION);

    public boolean grantsExemption(ServiceDomain domain) {
        if (domain == ServiceDomain.BROADCAST) {
            return switch (this) {
                case EL_RECIPIENT, DEAF_BLIND, DIPLOMATIC_STATUS -> true;
                default -> false;
            };
        }
        return false;
    }
}
```

### CircumstanceCategory

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
}
```

### VerificationStatus

```java
public enum VerificationStatus {
    UNVERIFIED("Ungeprüft"),
    PENDING("Prüfung ausstehend"),
    VERIFIED("Verifiziert"),
    EXPIRED("Abgelaufen"),
    INVALID("Ungültig"),
    REVOKED("Widerrufen");
}
```

---

## Relationships

```
Person (1) ─────────────< PersonCircumstance (N)
                              │
                              │ determines
                              ▼
                         Exemption eligibility
```

---

## Business Rules

### Broadcast Fee Exemption (RTVG)

```
IF household has member with EL_RECIPIENT (verified) THEN
    exemption = FULL
ELSE IF ALL household members are DEAF_BLIND (verified) THEN
    exemption = FULL
ELSE IF household has member with DIPLOMATIC_STATUS THEN
    exemption = FULL
END IF
```

### Re-Verification Requirements

| Circumstance | Interval |
|--------------|----------|
| EL_RECIPIENT | 36 months (3 years) |
| STUDENT | 6 months (per semester) |
| UNEMPLOYED | 6 months |
| DEAF_BLIND | None (permanent) |
| MILITARY_SERVICE | None (end date known) |

---

## Use Cases

### EL Recipient for Broadcast Exemption

```java
PersonCircumstance elStatus = new PersonCircumstance();
elStatus.setPersonId(person.getId());
elStatus.setCircumstanceType(CircumstanceType.EL_RECIPIENT);
elStatus.setValidFrom(LocalDate.of(2026, 1, 1));
elStatus.setVerificationStatus(VerificationStatus.VERIFIED);
elStatus.setCertificateNumber("AK-ZH-2025-123456");
elStatus.setIssuingAuthority("Ausgleichskasse Zürich");
elStatus.setNextVerificationDue(LocalDate.of(2028, 12, 31));
```

### Military Service (Temporary)

```java
PersonCircumstance military = new PersonCircumstance();
military.setPersonId(person.getId());
military.setCircumstanceType(CircumstanceType.MILITARY_SERVICE);
military.setValidFrom(LocalDate.of(2026, 7, 1));
military.setValidTo(LocalDate.of(2026, 10, 31));
military.setCircumstanceDetail("RS Inf 4, Kaserne Thun");
military.setVerificationStatus(VerificationStatus.VERIFIED);
```

---

## Code Location

| Component | Path |
|-----------|------|
| Entity | `govinda-masterdata/.../domain/model/PersonCircumstance.java` |
| Enums | `govinda-common/.../domain/model/CircumstanceType.java` |

---

## Related Documentation

- [Person](./person.md) - Parent entity
- [Exemption](../contract/exemption.md) - Resulting exemptions
- [Radio/TV Fee Concept](../../concepts/radio-tv-fee.md) - Exemption rules

---

*Last Updated: 2026-01-28*

# New Enumerations Specification - Extension

## Overview

This document extends `new-enums-specification.md` with additional enums identified in the gap analysis. These support:
- Business Partner / Third-Party Payer (GAP-01)
- Suspension Framework (GAP-02)
- Person Circumstances (GAP-03)
- Third-Party Payments (GAP-04)

---

## 10. PartnerType

**Purpose**: Identifies the type of business partner relationship.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/PartnerType.java`

```java
public enum PartnerType {

    // Government / Public
    CANTON("Kanton"),
    COMMUNE("Gemeinde"),
    FEDERAL_OFFICE("Bundesamt"),
    SOCIAL_SERVICES("Sozialdienst"),

    // Institutional
    EMPLOYER("Arbeitgeber"),
    INSTITUTION("Institution"),
    ASSOCIATION("Verband"),

    // Financial
    INSURANCE_COMPANY("Versicherung"),
    REINSURER("Rückversicherer"),
    BANK("Bank"),

    // Intermediaries
    BROKER("Makler"),
    AGENT("Agent"),
    RESELLER("Wiederverkäufer"),

    // Service Providers
    HEALTHCARE_PROVIDER("Leistungserbringer"),
    COLLECTION_AGENCY("Inkasso"),

    // Other
    FAMILY_PAYER("Familienzahler"),
    OTHER("Andere");

    private final String nameDe;

    PartnerType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 11. PartnerCategory

**Purpose**: Categorizes the role a partner plays.

```java
public enum PartnerCategory {
    PAYER("Zahler"),
    SUBSIDIZER("Subventionierer"),
    GUARANTOR("Garant"),
    INTERMEDIARY("Vermittler"),
    PROVIDER("Anbieter"),
    CREDITOR("Gläubiger"),
    DEBTOR("Schuldner");

    private final String nameDe;

    PartnerCategory(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 12. PartnerStatus

**Purpose**: Tracks business partner lifecycle.

```java
public enum PartnerStatus {
    PROSPECT("Interessent"),
    ACTIVE("Aktiv"),
    SUSPENDED("Sistiert"),
    TERMINATED("Beendet"),
    BLOCKED("Gesperrt");

    private final String nameDe;

    PartnerStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 13. PayerType

**Purpose**: Identifies who pays for a subscription.

```java
public enum PayerType {
    SUBSCRIBER("Versicherter"),
    PERSON("Person"),
    ORGANIZATION("Organisation"),
    BUSINESS_PARTNER("Partner"),
    CANTON("Kanton"),
    COMMUNE("Gemeinde");

    private final String nameDe;

    PayerType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 14. ArrangementType

**Purpose**: Defines payment arrangement structure.

```java
public enum ArrangementType {
    FULL_PAYMENT("Vollzahlung"),
    PARTIAL_SUBSIDY("Teilsubvention"),
    FIXED_CONTRIBUTION("Festbeitrag"),
    REMAINDER("Restbetrag"),
    GUARANTEE("Bürgschaft");

    private final String nameDe;

    ArrangementType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 15. ArrangementStatus

**Purpose**: Tracks payment arrangement lifecycle.

```java
public enum ArrangementStatus {
    PENDING("Ausstehend"),
    ACTIVE("Aktiv"),
    SUSPENDED("Sistiert"),
    EXPIRED("Abgelaufen"),
    TERMINATED("Beendet"),
    REJECTED("Abgelehnt");

    private final String nameDe;

    ArrangementStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 16. SuspensionReason

**Purpose**: Identifies why a subscription is suspended.

```java
public enum SuspensionReason {

    // Military & Civil Service
    MILITARY_SERVICE("Militärdienst", true, 365),
    CIVIL_PROTECTION("Zivilschutz", true, 90),
    CIVIL_SERVICE("Zivildienst", true, 365),

    // Relocation
    MOVING_DOMESTIC("Umzug Inland", false, 30),
    MOVING_ABROAD_TEMPORARY("Auslandaufenthalt temporär", true, 365),
    MOVING_ABROAD_PERMANENT("Auswanderung", false, null),

    // Health
    HOSPITALIZATION("Spitalaufenthalt", true, 180),
    REHABILITATION("Rehabilitation", true, 90),
    LONG_TERM_CARE("Langzeitpflege", true, null),

    // Education
    STUDY_ABROAD("Auslandstudium", true, 365),
    EXCHANGE_PROGRAM("Austauschprogramm", true, 365),

    // Legal
    IMPRISONMENT("Haft", true, null),
    ASYLUM_PROCEDURE("Asylverfahren", true, null),

    // Life Events
    MATERNITY_LEAVE("Mutterschaftsurlaub", true, 120),
    PATERNITY_LEAVE("Vaterschaftsurlaub", true, 30),
    UNPAID_LEAVE("Unbezahlter Urlaub", true, 180),
    SABBATICAL("Sabbatical", true, 365),

    // Other
    FINANCIAL_HARDSHIP("Finanzielle Härte", true, 90),
    DISPUTE("Streitfall", false, null),
    ADMINISTRATIVE("Administrativ", false, null),
    OTHER("Andere", true, 90);

    private final String nameDe;
    private final boolean requiresDocumentation;
    private final Integer defaultMaxDays;

    SuspensionReason(String nameDe, boolean requiresDocumentation, Integer defaultMaxDays) {
        this.nameDe = nameDe;
        this.requiresDocumentation = requiresDocumentation;
        this.defaultMaxDays = defaultMaxDays;
    }

    public boolean requiresDocumentation() {
        return requiresDocumentation;
    }

    public Integer getDefaultMaxDays() {
        return defaultMaxDays;
    }
}
```

---

## 17. SuspensionType

**Purpose**: Defines the nature of suspension.

```java
public enum SuspensionType {
    FULL("Vollständig"),
    PARTIAL("Teilweise"),
    COVERAGE_ONLY("Nur Deckung"),
    BILLING_ONLY("Nur Faktura");

    private final String nameDe;

    SuspensionType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 18. SuspensionStatus

**Purpose**: Tracks suspension workflow state.

```java
public enum SuspensionStatus {
    REQUESTED("Beantragt"),
    PENDING_DOCS("Dokumente ausstehend"),
    UNDER_REVIEW("In Prüfung"),
    APPROVED("Genehmigt"),
    ACTIVE("Aktiv"),
    ENDING_SOON("Endet bald"),
    ENDED("Beendet"),
    REJECTED("Abgelehnt"),
    CANCELLED("Storniert");

    private final String nameDe;

    SuspensionStatus(String nameDe) {
        this.nameDe = nameDe;
    }

    public boolean isActive() {
        return this == ACTIVE || this == ENDING_SOON;
    }

    public boolean isTerminal() {
        return this == ENDED || this == REJECTED || this == CANCELLED;
    }
}
```

---

## 19. BillingTreatment

**Purpose**: Defines billing during suspension.

```java
public enum BillingTreatment {
    NO_BILLING("Keine Fakturierung"),
    REDUCED_BILLING("Reduzierte Faktura"),
    FULL_BILLING("Volle Faktura"),
    DEFERRED_BILLING("Aufgeschoben"),
    CREDIT_ON_RETURN("Gutschrift");

    private final String nameDe;

    BillingTreatment(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 20. CircumstanceType

**Purpose**: Identifies person circumstances affecting fees.

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

---

## 21. CircumstanceCategory

**Purpose**: Groups circumstances for filtering/reporting.

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

---

## 22. VerificationStatus

**Purpose**: Tracks document/circumstance verification.

```java
public enum VerificationStatus {
    UNVERIFIED("Ungeprüft"),
    PENDING("Prüfung ausstehend"),
    VERIFIED("Verifiziert"),
    EXPIRED("Abgelaufen"),
    INVALID("Ungültig"),
    REVOKED("Widerrufen");

    private final String nameDe;

    VerificationStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## Summary: All New Enums

| # | Enum | Purpose | Module |
|---|------|---------|--------|
| 1 | ServiceDomain | Regulatory domain | common |
| 2 | SubscriberType | Type of subscriber | common |
| 3 | HouseholdType | Type of household | common |
| 4 | PricingModel | How pricing works | common |
| 5 | OrganizationType | Legal structure | common |
| 6 | OrganizationStatus | Org lifecycle | common |
| 7 | ExemptionType | Nature of exemption | common |
| 8 | ExemptionReason | Basis for exemption | common |
| 9 | ExemptionStatus | Exemption workflow | common |
| 10 | PartnerType | Type of partner | common |
| 11 | PartnerCategory | Partner role | common |
| 12 | PartnerStatus | Partner lifecycle | common |
| 13 | PayerType | Who pays | common |
| 14 | ArrangementType | Payment arrangement | common |
| 15 | ArrangementStatus | Arrangement lifecycle | common |
| 16 | SuspensionReason | Why suspended | common |
| 17 | SuspensionType | Nature of suspension | common |
| 18 | SuspensionStatus | Suspension workflow | common |
| 19 | BillingTreatment | Billing during suspension | common |
| 20 | CircumstanceType | Person circumstances | common |
| 21 | CircumstanceCategory | Circumstance groups | common |
| 22 | VerificationStatus | Document verification | common |

---

*Last Updated: 2026-01-27*

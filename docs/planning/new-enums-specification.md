# New Enumerations Specification

## Overview

This document specifies the new enumerations required for the generic subscription model extension. These enums should be added to `govinda-common` to be shared across all bounded contexts.

---

## 1. ServiceDomain

**Purpose**: Identifies the regulatory/business domain of a product or subscription.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/ServiceDomain.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the regulatory or business domain for products and subscriptions.
 * Each domain may have different business rules, pricing models, and regulatory requirements.
 */
public enum ServiceDomain {

    /**
     * Swiss health insurance domain.
     * Includes KVG (mandatory) and VVG (supplementary) products.
     * Regulated by BAG (Federal Office of Public Health).
     */
    HEALTHCARE("Gesundheit", "Santé", "Salute", "Healthcare"),

    /**
     * Swiss radio and television fee domain (RTVG).
     * Includes household and corporate fees.
     * Regulated by BAKOM, collected via contracted agencies (households) and ESTV (businesses).
     */
    BROADCAST("Rundfunk", "Radiodiffusion", "Radiodiffusione", "Broadcast"),

    /**
     * Telecommunications services domain.
     * Includes mobile, internet, and TV subscriptions.
     * Commercial products with various pricing models.
     */
    TELECOM("Telekommunikation", "Télécommunications", "Telecomunicazioni", "Telecom"),

    /**
     * Utility services domain (future).
     * May include electricity, gas, water subscriptions.
     */
    UTILITIES("Versorgung", "Services publics", "Utenze", "Utilities"),

    /**
     * Generic subscription domain.
     * For custom subscription products not fitting other categories.
     */
    CUSTOM("Andere", "Autres", "Altri", "Custom");

    private final String nameDe;
    private final String nameFr;
    private final String nameIt;
    private final String nameEn;

    ServiceDomain(String nameDe, String nameFr, String nameIt, String nameEn) {
        this.nameDe = nameDe;
        this.nameFr = nameFr;
        this.nameIt = nameIt;
        this.nameEn = nameEn;
    }

    public String getName(Language language) {
        return switch (language) {
            case DE -> nameDe;
            case FR -> nameFr;
            case IT -> nameIt;
            case EN -> nameEn;
        };
    }
}
```

**i18n Keys** (add to all message files):

```properties
# messages_de.properties
service.domain.HEALTHCARE=Gesundheit
service.domain.BROADCAST=Rundfunk
service.domain.TELECOM=Telekommunikation
service.domain.UTILITIES=Versorgung
service.domain.CUSTOM=Andere

# messages_fr.properties
service.domain.HEALTHCARE=Santé
service.domain.BROADCAST=Radiodiffusion
service.domain.TELECOM=Télécommunications
service.domain.UTILITIES=Services publics
service.domain.CUSTOM=Autres

# messages_it.properties
service.domain.HEALTHCARE=Salute
service.domain.BROADCAST=Radiodiffusione
service.domain.TELECOM=Telecomunicazioni
service.domain.UTILITIES=Utenze
service.domain.CUSTOM=Altri

# messages.properties (EN)
service.domain.HEALTHCARE=Healthcare
service.domain.BROADCAST=Broadcast
service.domain.TELECOM=Telecom
service.domain.UTILITIES=Utilities
service.domain.CUSTOM=Custom
```

---

## 2. SubscriberType

**Purpose**: Identifies the type of entity subscribing to services.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/SubscriberType.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the type of subscriber for products and subscriptions.
 * Determines which pricing model and business rules apply.
 */
public enum SubscriberType {

    /**
     * Individual person subscriber.
     * Used for: Healthcare insurance (KVG/VVG), personal telecom.
     */
    INDIVIDUAL("Einzelperson", "Personne individuelle"),

    /**
     * Private household (family unit).
     * Used for: Media fee (Serafe household fee).
     */
    PRIVATE_HOUSEHOLD("Privathaushalt", "Ménage privé"),

    /**
     * Collective household (institution).
     * Examples: Nursing homes, prisons, boarding schools.
     * Used for: Media fee (double household rate).
     */
    COLLECTIVE_HOUSEHOLD("Kollektivhaushalt", "Ménage collectif"),

    /**
     * Small business (turnover < threshold).
     * May be exempt from certain fees.
     */
    CORPORATE_SMALL("Kleinunternehmen", "Petite entreprise"),

    /**
     * Medium business (turnover >= threshold).
     * Subject to tiered corporate fees.
     */
    CORPORATE_MEDIUM("Mittleres Unternehmen", "Moyenne entreprise"),

    /**
     * Large business (turnover >> threshold).
     * Highest fee tiers apply.
     */
    CORPORATE_LARGE("Grossunternehmen", "Grande entreprise"),

    /**
     * Non-profit organization.
     * May qualify for reduced rates or exemptions.
     */
    NONPROFIT("Gemeinnützig", "Organisation à but non lucratif"),

    /**
     * Public institution (government, schools).
     * Special treatment may apply.
     */
    PUBLIC_INSTITUTION("Öffentliche Institution", "Institution publique");

    private final String nameDe;
    private final String nameFr;

    SubscriberType(String nameDe, String nameFr) {
        this.nameDe = nameDe;
        this.nameFr = nameFr;
    }

    /**
     * Returns true if this is a household-type subscriber.
     */
    public boolean isHousehold() {
        return this == PRIVATE_HOUSEHOLD || this == COLLECTIVE_HOUSEHOLD;
    }

    /**
     * Returns true if this is a corporate/business subscriber.
     */
    public boolean isCorporate() {
        return this == CORPORATE_SMALL || this == CORPORATE_MEDIUM ||
               this == CORPORATE_LARGE || this == NONPROFIT || this == PUBLIC_INSTITUTION;
    }
}
```

---

## 3. HouseholdType

**Purpose**: Distinguishes between private and collective household types.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/HouseholdType.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the type of household for fee calculation purposes.
 * Collective households pay double the standard media fee.
 */
public enum HouseholdType {

    /**
     * Standard private household (family, single person, WG).
     * Media fee: CHF 335/year.
     */
    PRIVATE("Privathaushalt", false),

    /**
     * Shared housing (WG/flatshare).
     * Treated same as private for fee purposes.
     */
    SHARED("Wohngemeinschaft", false),

    /**
     * Old-age/retirement home (Altersheim).
     * Collective household: CHF 670/year.
     */
    ELDERLY_HOME("Altersheim", true),

    /**
     * Nursing/care home (Pflegeheim).
     * Collective household: CHF 670/year.
     */
    NURSING_HOME("Pflegeheim", true),

    /**
     * Youth hostel (Jugendherberge).
     * Collective household: CHF 670/year.
     */
    HOSTEL("Jugendherberge", true),

    /**
     * Penal institution (Strafanstalt).
     * Collective household: CHF 670/year.
     */
    PRISON("Strafanstalt", true),

    /**
     * Boarding school (Internat).
     * Collective household: CHF 670/year.
     */
    BOARDING_SCHOOL("Internat", true),

    /**
     * Asylum accommodation (Asylunterkunft).
     * Collective household: CHF 670/year.
     */
    ASYLUM_CENTER("Asylunterkunft", true),

    /**
     * Religious community (Kloster).
     * Collective household: CHF 670/year.
     */
    RELIGIOUS_COMMUNITY("Kloster", true);

    private final String nameDe;
    private final boolean collective;

    HouseholdType(String nameDe, boolean collective) {
        this.nameDe = nameDe;
        this.collective = collective;
    }

    /**
     * Returns true if this is a collective household type.
     * Collective households pay double the standard media fee.
     */
    public boolean isCollective() {
        return collective;
    }

    /**
     * Returns the media fee multiplier for this household type.
     * Private: 1x, Collective: 2x.
     */
    public int getFeeMultiplier() {
        return collective ? 2 : 1;
    }
}
```

---

## 4. PricingModel

**Purpose**: Identifies how pricing is calculated for a product.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/PricingModel.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the pricing model used for a product.
 * Determines how premiums/fees are calculated.
 */
public enum PricingModel {

    /**
     * Fixed price for all subscribers.
     * Example: Serafe household fee (CHF 335/year).
     */
    FIXED("Festpreis"),

    /**
     * Price varies by subscriber's age group.
     * Example: Health insurance (child, young adult, adult).
     */
    AGE_BASED("Altersabhängig"),

    /**
     * Price varies by geographic region.
     * Example: Health insurance premium regions.
     */
    REGION_BASED("Regionabhängig"),

    /**
     * Price varies by tier (usually based on turnover).
     * Example: Serafe corporate fee (18 tiers).
     */
    TIER_BASED("Stufenbasiert"),

    /**
     * Price varies by subscriber type.
     * Example: Private vs. collective household.
     */
    SUBSCRIBER_TYPE_BASED("Kundentyp-basiert"),

    /**
     * Price based on actual usage.
     * Example: Telecom roaming charges.
     */
    USAGE_BASED("Verbrauchsabhängig"),

    /**
     * Combination of multiple pricing factors.
     * Example: Health insurance (region + age + franchise).
     */
    COMPOSITE("Kombiniert");

    private final String nameDe;

    PricingModel(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 5. OrganizationType

**Purpose**: Identifies the legal structure of an organization.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/OrganizationType.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the legal structure of an organization.
 * Based on Swiss commercial registry classifications.
 */
public enum OrganizationType {

    // Private sector - commercial
    SOLE_PROPRIETORSHIP("Einzelunternehmen", "Entreprise individuelle"),
    GENERAL_PARTNERSHIP("Kollektivgesellschaft", "Société en nom collectif"),
    LIMITED_PARTNERSHIP("Kommanditgesellschaft", "Société en commandite"),
    LIMITED_COMPANY("GmbH", "Sàrl"),
    STOCK_CORPORATION("AG", "SA"),
    COOPERATIVE("Genossenschaft", "Coopérative"),

    // Non-profit
    ASSOCIATION("Verein", "Association"),
    FOUNDATION("Stiftung", "Fondation"),

    // Public sector
    PUBLIC_INSTITUTION("Öffentlich-rechtliche Körperschaft", "Institution de droit public"),
    MUNICIPALITY("Gemeinde", "Commune"),
    CANTON("Kanton", "Canton"),

    // Other
    BRANCH_OFFICE("Zweigniederlassung", "Succursale"),
    FOREIGN_ENTITY("Ausländische Gesellschaft", "Société étrangère");

    private final String nameDe;
    private final String nameFr;

    OrganizationType(String nameDe, String nameFr) {
        this.nameDe = nameDe;
        this.nameFr = nameFr;
    }

    /**
     * Returns true if this is a for-profit commercial entity.
     */
    public boolean isCommercial() {
        return switch (this) {
            case SOLE_PROPRIETORSHIP, GENERAL_PARTNERSHIP, LIMITED_PARTNERSHIP,
                 LIMITED_COMPANY, STOCK_CORPORATION, COOPERATIVE, BRANCH_OFFICE,
                 FOREIGN_ENTITY -> true;
            default -> false;
        };
    }

    /**
     * Returns true if this is a non-profit or public entity.
     */
    public boolean isNonProfit() {
        return switch (this) {
            case ASSOCIATION, FOUNDATION, PUBLIC_INSTITUTION, MUNICIPALITY, CANTON -> true;
            default -> false;
        };
    }
}
```

---

## 6. OrganizationStatus

**Purpose**: Tracks the lifecycle status of an organization.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/OrganizationStatus.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Tracks the lifecycle status of an organization.
 */
public enum OrganizationStatus {

    /**
     * Organization is operating normally.
     */
    ACTIVE("Aktiv"),

    /**
     * Organization is temporarily inactive.
     */
    INACTIVE("Inaktiv"),

    /**
     * Organization is in liquidation process.
     */
    LIQUIDATING("In Liquidation"),

    /**
     * Organization no longer exists (dissolved).
     */
    DISSOLVED("Aufgelöst"),

    /**
     * Organization has merged into another entity.
     */
    MERGED("Fusioniert");

    private final String nameDe;

    OrganizationStatus(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 7. ExemptionType

**Purpose**: Identifies the nature of a fee exemption.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/ExemptionType.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the type/nature of a fee exemption.
 */
public enum ExemptionType {

    /**
     * Complete exemption - no fee payable.
     * Example: Deaf-blind Serafe exemption.
     */
    FULL("Vollständig"),

    /**
     * Partial exemption - reduced fee.
     * Can be percentage or fixed amount.
     */
    PARTIAL("Teilweise"),

    /**
     * Time-limited exemption.
     * Has definite end date.
     */
    TEMPORARY("Befristet"),

    /**
     * Conditional exemption requiring periodic verification.
     * Example: EL recipient (3-year verification cycle).
     */
    CONDITIONAL("Bedingt");

    private final String nameDe;

    ExemptionType(String nameDe) {
        this.nameDe = nameDe;
    }
}
```

---

## 8. ExemptionReason

**Purpose**: Identifies why a subscriber qualifies for an exemption.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/ExemptionReason.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Identifies the reason/basis for a fee exemption.
 * Organized by service domain.
 */
public enum ExemptionReason {

    // Healthcare domain
    /**
     * Receives cantonal premium subsidy (Prämienverbilligung).
     * Healthcare domain only.
     */
    PREMIUM_SUBSIDY("Prämienverbilligung", ServiceDomain.HEALTHCARE),

    // Broadcast domain (RTVG/BAKOM)
    /**
     * Receives AHV/IV supplementary benefits (Ergänzungsleistungen).
     * Full exemption for entire household.
     */
    AHV_IV_SUPPLEMENT("EL-Bezüger", ServiceDomain.BROADCAST),

    /**
     * All household members are deaf-blind.
     * Requires medical certification.
     */
    DEAF_BLIND("Taubblind", ServiceDomain.BROADCAST),

    /**
     * Holds diplomatic status (FDFA card).
     */
    DIPLOMATIC_STATUS("Diplomatenstatus", ServiceDomain.BROADCAST),

    // Telecom/general discounts
    /**
     * Means-tested low income discount.
     */
    LOW_INCOME("Einkommensschwach", null),

    /**
     * Age-based senior discount.
     */
    SENIOR_DISCOUNT("Seniorenrabatt", null),

    /**
     * Student status discount.
     */
    STUDENT_DISCOUNT("Studentenrabatt", null),

    /**
     * Disability-based discount.
     */
    DISABILITY_DISCOUNT("Behindertenrabatt", null),

    // Business exemptions
    /**
     * Business turnover below fee threshold.
     */
    BELOW_THRESHOLD("Unter Schwellenwert", null),

    /**
     * Tax-exempt non-profit status.
     */
    NONPROFIT_STATUS("Gemeinnützig", null),

    /**
     * Startup promotion discount.
     */
    STARTUP_DISCOUNT("Startup-Förderung", null),

    // Promotional/loyalty
    /**
     * Marketing promotional discount.
     */
    PROMOTIONAL("Werbeaktion", null),

    /**
     * Long-term customer loyalty discount.
     */
    LOYALTY("Treuerabatt", null),

    /**
     * Bundle/combined services discount.
     */
    BUNDLE_DISCOUNT("Kombi-Rabatt", null),

    /**
     * Referral program discount.
     */
    REFERRAL("Weiterempfehlung", null),

    /**
     * Employee discount program.
     */
    EMPLOYEE_DISCOUNT("Mitarbeiterrabatt", null),

    /**
     * Hardship case (individual assessment).
     */
    HARDSHIP("Härtefall", null);

    private final String nameDe;
    private final ServiceDomain specificDomain; // null = cross-domain

    ExemptionReason(String nameDe, ServiceDomain specificDomain) {
        this.nameDe = nameDe;
        this.specificDomain = specificDomain;
    }

    /**
     * Returns true if this reason is valid for the given domain.
     */
    public boolean isValidForDomain(ServiceDomain domain) {
        return specificDomain == null || specificDomain == domain;
    }

    /**
     * Returns true if this reason requires documentation/certification.
     */
    public boolean requiresCertificate() {
        return switch (this) {
            case AHV_IV_SUPPLEMENT, DEAF_BLIND, DIPLOMATIC_STATUS,
                 STUDENT_DISCOUNT, DISABILITY_DISCOUNT, NONPROFIT_STATUS -> true;
            default -> false;
        };
    }
}
```

---

## 9. ExemptionStatus

**Purpose**: Tracks the workflow status of an exemption request.

**Location**: `govinda-common/src/main/java/net/voytrex/govinda/common/domain/model/ExemptionStatus.java`

```java
package net.voytrex.govinda.common.domain.model;

/**
 * Tracks the workflow status of an exemption request.
 */
public enum ExemptionStatus {

    /**
     * Exemption request submitted, awaiting review.
     */
    PENDING("Ausstehend"),

    /**
     * Exemption approved and active.
     */
    APPROVED("Genehmigt"),

    /**
     * Exemption request rejected.
     */
    REJECTED("Abgelehnt"),

    /**
     * Exemption has expired (past validTo date).
     */
    EXPIRED("Abgelaufen"),

    /**
     * Exemption temporarily suspended.
     */
    SUSPENDED("Sistiert"),

    /**
     * Exemption permanently revoked.
     */
    REVOKED("Widerrufen");

    private final String nameDe;

    ExemptionStatus(String nameDe) {
        this.nameDe = nameDe;
    }

    /**
     * Returns true if this status represents an active exemption.
     */
    public boolean isActive() {
        return this == APPROVED;
    }

    /**
     * Returns true if this status is terminal (no further changes expected).
     */
    public boolean isTerminal() {
        return this == REJECTED || this == EXPIRED || this == REVOKED;
    }
}
```

---

## Migration Notes

### Default Values for Existing Data

When adding these enums, existing data should receive sensible defaults:

| Entity | Field | Default Value |
|--------|-------|---------------|
| Product | `domain` | `HEALTHCARE` |
| Household | `type` | `PRIVATE` |
| Coverage (new subscriptions) | `subscriberType` | `INDIVIDUAL` |

### Database Migration

```sql
-- Add domain to products
ALTER TABLE products ADD COLUMN domain VARCHAR(20) NOT NULL DEFAULT 'HEALTHCARE';

-- Add type to households
ALTER TABLE households ADD COLUMN type VARCHAR(30) NOT NULL DEFAULT 'PRIVATE';

-- Add resident_count for collective households
ALTER TABLE households ADD COLUMN resident_count INTEGER;

-- Add institution_id for institutional households
ALTER TABLE households ADD COLUMN institution_id UUID REFERENCES organizations(id);
```

---

## Test Coverage Requirements

Each enum should have tests for:

1. **Values exist**: All documented values are present
2. **Name methods**: Language-specific names return correctly
3. **Helper methods**: Boolean checks and calculations work
4. **Domain validation**: Where applicable, domain restrictions enforced

---

*Last Updated: 2026-01-27*

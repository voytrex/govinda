# Insurance Models (Versicherungsmodelle)

## Overview

**Insurance Models** (Versicherungsmodelle) are alternative forms of KVG basic insurance that offer premium discounts in exchange for accepting certain restrictions on healthcare access. These models are also known as "Managed Care" or "Alternative Insurance Models."

> **German**: Versicherungsmodell, Alternatives Versicherungsmodell
> **French**: Modèle d'assurance alternatif
> **Italian**: Modello assicurativo alternativo

---

## Model Comparison

```
┌─────────────────────────────────────────────────────────────────────┐
│                    INSURANCE MODELS                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│   STANDARD          HMO             HAUSARZT         TELMED         │
│   ────────         ─────           ──────────       ────────        │
│                                                                     │
│   Free choice   ──▶ HMO Center  ──▶ Family Dr.  ──▶ Hotline        │
│   of any           first            first           first           │
│   provider                                                          │
│                                                                     │
│   No discount      10-25%          10-20%          10-15%          │
│                    savings         savings         savings          │
│                                                                     │
│   Full            Restricted       Restricted      Restricted       │
│   flexibility     network          to 1 doctor     access           │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Standard Model (Standardmodell)

### Description

The traditional insurance model with **free choice of provider** (Freie Arztwahl).

| Aspect | Details |
|--------|---------|
| **Code** | `STANDARD` |
| **Access** | Visit any licensed provider |
| **Referral** | Not required for specialists |
| **Network** | None (all providers) |
| **Discount** | 0% (reference premium) |

### Best For

- People who want maximum flexibility
- Those with established specialist relationships
- Complex medical situations
- People who travel frequently within Switzerland

### Limitations

- Highest premium option
- No cost savings from model choice

---

## HMO Model (HMO-Modell)

### Description

**Health Maintenance Organization** - Care is coordinated through an HMO center or group practice.

| Aspect | Details |
|--------|---------|
| **Code** | `HMO` |
| **Access** | Must first visit HMO center |
| **Referral** | Required for specialists |
| **Network** | HMO center's network |
| **Discount** | 10-25% |

### How It Works

```
Medical Need
     │
     ▼
┌─────────────────┐
│   HMO CENTER    │
│   (First Stop)  │
├─────────────────┤
│ • Examination   │
│ • Diagnosis     │
│ • Treatment OR  │
│ • Referral      │
└────────┬────────┘
         │
         ▼ (if needed)
┌─────────────────┐
│   SPECIALIST    │
│   (Referral)    │
└─────────────────┘
```

### Best For

- People living near an HMO center
- Those comfortable with team-based care
- Cost-conscious individuals
- Generally healthy people

### Limitations

- Must live near participating HMO center
- Cannot freely choose any doctor
- May feel restricted

### Notable HMO Networks

| Network | Regions |
|---------|---------|
| Medbase | Nationwide |
| Sanacare | German-speaking Switzerland |
| mediX | Zürich, Basel, Bern |

---

## Family Doctor Model (Hausarzt-Modell)

### Description

The **Hausarzt** (family doctor/GP) acts as a **gatekeeper** for all healthcare services.

| Aspect | Details |
|--------|---------|
| **Code** | `HAUSARZT` |
| **Access** | Must first consult chosen family doctor |
| **Referral** | Required for specialists |
| **Network** | One specific GP |
| **Discount** | 10-20% |

### How It Works

```
Medical Need
     │
     ▼
┌─────────────────┐
│  FAMILY DOCTOR  │
│   (Hausarzt)    │
├─────────────────┤
│ • Examination   │
│ • Coordination  │
│ • Referral      │
└────────┬────────┘
         │
         ▼ (if needed)
┌─────────────────┐
│   SPECIALIST    │
│   (Referral)    │
└─────────────────┘
```

### Choosing a Family Doctor

- Must select a specific GP from insurer's list
- Can usually change GP once per year
- GP must agree to participate

### Best For

- People who value having a single trusted doctor
- Those with chronic conditions (coordinated care)
- Families (same GP for all members)
- People who appreciate continuity of care

### Limitations

- Bound to one specific doctor
- Must get referral for specialists
- GP may have waiting times

---

## Telemedicine Model (Telmed-Modell)

### Description

**Telemedicine first contact** - Must call a medical hotline before visiting a provider.

| Aspect | Details |
|--------|---------|
| **Code** | `TELMED` |
| **Access** | Call hotline before any visit |
| **Referral** | Hotline provides advice/referral |
| **Network** | Varies (often no restriction after call) |
| **Discount** | 10-15% |

### How It Works

```
Medical Need
     │
     ▼
┌─────────────────┐
│  TELMED HOTLINE │
│   (24/7 call)   │
├─────────────────┤
│ • Phone triage  │
│ • Medical advice│
│ • Self-care tips│
│ • OR referral   │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────┐
│          OUTCOME OPTIONS            │
├─────────────────────────────────────┤
│ • Self-care instructions            │
│ • Recommendation to see GP          │
│ • Direct specialist referral        │
│ • Emergency: go to hospital         │
└─────────────────────────────────────┘
```

### Hotline Numbers

| Insurer | Service | Number |
|---------|---------|--------|
| Sanitas | Medgate | 0844 844 222 |
| CSS | Telmed | 058 277 77 77 |
| Helsana | Medline | 0800 800 881 |

⚠️ **Note**: Numbers are insurer-specific. Check your insurance card.

### Best For

- Tech-savvy individuals
- People with minor ailments
- Those who appreciate 24/7 access
- People with busy schedules

### Limitations

- Must always call first (even for routine visits)
- Some find phone consultations frustrating
- Emergency situations are exempt

---

## Emergency Exceptions

All models allow **direct access in emergencies**:

| Situation | Allowed Without Restriction |
|-----------|----------------------------|
| Life-threatening emergency | ✅ Yes |
| Accident | ✅ Yes |
| Gynecological check-up | ✅ Yes (often) |
| Eye exam (ophthalmologist) | ✅ Yes (some insurers) |
| Pediatric emergencies | ✅ Yes |

---

## Changing Models

### When Can You Change?

| Timing | Rules |
|--------|-------|
| **January 1** | Standard change date |
| **Mid-year** | Usually not allowed |
| **With insurer change** | Can choose new model |

### Notification Deadline

To change model for next year: **November 30** written notification

---

## Combining Savings

Model discounts combine with franchise discounts:

```
Example: Adult in Zürich

Standard, CHF 300:           CHF 490/month (reference)
Telmed, CHF 300:             CHF 420/month (-14%)
Telmed, CHF 2500:            CHF 315/month (-36%)

Combined savings: ~CHF 175/month = CHF 2,100/year
```

⚠️ **Caution**: High franchise + restrictive model = maximum savings but maximum risk if you need care.

---

## Code Reference

### InsuranceModel Enum

```java
public enum InsuranceModel {
    STANDARD("STD", false, "Free choice of provider"),
    HMO("HMO", true, "HMO center first contact"),
    HAUSARZT("HAM", true, "Family doctor gatekeeper"),
    TELMED("TLM", true, "Telemedicine first contact");

    private final String code;
    private final boolean hasProviderRestriction;
    private final String description;

    public boolean requiresGatekeeper() {
        return hasProviderRestriction;
    }
}
```

### Product Entity

```java
public class Product {
    // KVG products have an insurance model
    private InsuranceModel insuranceModel;  // null for VVG

    // VVG products don't use insurance models
    public boolean isKvg() {
        return category == ProductCategory.KVG;
    }
}
```

---

## Business Rules Summary

| Rule | Description |
|------|-------------|
| ⚠️ KVG only | Insurance models apply only to basic insurance |
| ⚠️ Annual changes | Can only change model on January 1 |
| ⚠️ Emergency exempt | Direct access always allowed in emergencies |
| ⚠️ Discounts combine | Model discount + franchise discount stack |
| ⚠️ Network restrictions | HMO/Hausarzt may limit provider choice |

---

## Official Resources

| Resource | URL |
|----------|-----|
| BAG Insurance Models | [bag.admin.ch/modelle](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen/besondere-versicherungsformen.html) |
| Premium Calculator | [priminfo.admin.ch](https://www.priminfo.admin.ch) |

---

## Related Documentation

- [KVG - Mandatory Insurance](./kvg-mandatory-insurance.md)
- [Franchise System](./franchise-system.md)
- [Swiss Healthcare System](./swiss-healthcare-system.md)

---

*Last Updated: 2026-01-26*

# Regulatory Compliance

This section documents the regulatory requirements and compliance considerations for Swiss health insurance software systems. Understanding these requirements is essential for building compliant insurance management solutions.

---

## Regulatory Landscape

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    SWISS HEALTH INSURANCE REGULATION                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                         FEDERAL LEVEL                                       │
│                              │                                              │
│          ┌───────────────────┼───────────────────┐                         │
│          ▼                   ▼                   ▼                          │
│    ┌───────────┐       ┌───────────┐       ┌───────────┐                   │
│    │    BAG    │       │   FINMA   │       │   EDÖB    │                   │
│    │  Health   │       │ Financial │       │   Data    │                   │
│    │  Policy   │       │Supervision│       │Protection │                   │
│    └─────┬─────┘       └─────┬─────┘       └─────┬─────┘                   │
│          │                   │                   │                          │
│          ▼                   ▼                   ▼                          │
│    ┌───────────┐       ┌───────────┐       ┌───────────┐                   │
│    │    KVG    │       │    VVG    │       │    DSG    │                   │
│    │  Health   │       │ Insurance │       │   Data    │                   │
│    │ Insurance │       │ Contracts │       │Protection │                   │
│    │    Act    │       │    Act    │       │    Act    │                   │
│    └───────────┘       └───────────┘       └───────────┘                   │
│                                                                             │
│                         CANTONAL LEVEL                                      │
│                              │                                              │
│          ┌───────────────────┼───────────────────┐                         │
│          ▼                   ▼                   ▼                          │
│    ┌───────────┐       ┌───────────┐       ┌───────────┐                   │
│    │  Premium  │       │  Hospital │       │  Cantonal │                   │
│    │ Subsidies │       │ Planning  │       │   Health  │                   │
│    │           │       │           │       │    Laws   │                   │
│    └───────────┘       └───────────┘       └───────────┘                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Key Regulatory Bodies

| Body | Full Name | Responsibility |
|------|-----------|----------------|
| **BAG** | Bundesamt für Gesundheit | Health policy, KVG oversight, premium approval |
| **FINMA** | Finanzmarktaufsicht | Financial supervision of insurers |
| **EDÖB** | Eidg. Datenschutz- und Öffentlichkeitsbeauftragter | Data protection supervision |
| **Cantons** | 26 Swiss Cantons | Premium subsidies, hospital planning |

---

## Primary Laws and Regulations

### Federal Laws

| Law | Abbreviation | Scope | Documentation |
|-----|--------------|-------|---------------|
| Health Insurance Act | KVG | Mandatory basic insurance | [KVG Requirements](./kvg-law-requirements.md) |
| Insurance Contract Act | VVG | Supplementary insurance | [VVG Requirements](./vvg-law-requirements.md) |
| Data Protection Act | DSG | Personal data handling | [Data Protection](./data-protection.md) |
| Accident Insurance Act | UVG | Accident coverage integration | - |

### Key Ordinances

| Ordinance | Abbreviation | Purpose |
|-----------|--------------|---------|
| Krankenversicherungsverordnung | KVV | KVG implementation details |
| Verordnung über die Krankenversicherung | VKV | Technical specifications |
| Datenschutzverordnung | DSV | DSG implementation |

---

## Documentation in This Section

| Document | Purpose | Audience |
|----------|---------|----------|
| [BAG - Federal Office](./bag-federal-office.md) | BAG role, data feeds, reporting | Developers, Compliance |
| [KVG Requirements](./kvg-law-requirements.md) | Basic insurance compliance rules | Developers, Business |
| [VVG Requirements](./vvg-law-requirements.md) | Supplementary insurance rules | Developers, Business |
| [Data Protection](./data-protection.md) | DSG/GDPR requirements | Developers, DPO |

---

## Compliance Checklist Overview

### KVG Compliance

| Requirement | Description | Status |
|-------------|-------------|--------|
| Unisex premiums | No gender-based KVG pricing | ✅ Implemented |
| Premium region pricing | BAG-defined regions | ✅ Implemented |
| Franchise options | Correct levels per age | ✅ Implemented |
| Coverage continuity | No gaps allowed | ⏳ Planned |
| Acceptance obligation | Must accept all applicants | ⏳ Planned |
| Termination rules | Correct deadlines | ⏳ Planned |

### VVG Compliance

| Requirement | Description | Status |
|-------------|-------------|--------|
| Health declaration | Risk assessment support | ⏳ Planned |
| Contract terms | Notice periods, conditions | ⏳ Planned |
| Gender pricing option | Optional risk-based pricing | ✅ Implemented |
| Disclosure requirements | Customer information | ⏳ Planned |

### Data Protection

| Requirement | Description | Status |
|-------------|-------------|--------|
| Health data classification | Sensitive data handling | ⏳ Planned |
| Consent management | Customer consent tracking | ⏳ Planned |
| Data retention | Legal retention periods | ⏳ Planned |
| Access rights | Customer data access | ⏳ Planned |
| Audit logging | Change tracking | ✅ Implemented |

---

## Regulatory Update Process

When regulations change:

```
┌─────────────────────────────────────────────────────────────────┐
│              REGULATORY UPDATE WORKFLOW                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   1. MONITORING                                                 │
│      └─▶ Watch BAG/FINMA announcements                         │
│      └─▶ Subscribe to official newsletters                     │
│      └─▶ Check fedlex.admin.ch for law changes                 │
│                                                                 │
│   2. IMPACT ANALYSIS                                            │
│      └─▶ Identify affected business rules                      │
│      └─▶ Assess technical changes required                     │
│      └─▶ Determine timeline for compliance                     │
│                                                                 │
│   3. IMPLEMENTATION                                             │
│      └─▶ Update domain model if needed                         │
│      └─▶ Adjust business rules                                 │
│      └─▶ Update documentation                                  │
│                                                                 │
│   4. VALIDATION                                                 │
│      └─▶ Test compliance                                       │
│      └─▶ Review with compliance team                           │
│      └─▶ Deploy before effective date                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Official Resources

### Federal Authorities

| Authority | URL | Purpose |
|-----------|-----|---------|
| BAG | [bag.admin.ch](https://www.bag.admin.ch) | Health policy, KVG |
| FINMA | [finma.ch](https://www.finma.ch) | Financial supervision |
| EDÖB | [edoeb.admin.ch](https://www.edoeb.admin.ch) | Data protection |
| Fedlex | [fedlex.admin.ch](https://www.fedlex.admin.ch) | Official law texts |

### Industry Resources

| Resource | URL | Purpose |
|----------|-----|---------|
| santésuisse | [santesuisse.ch](https://www.santesuisse.ch) | Insurer association |
| curafutura | [curafutura.ch](https://www.curafutura.ch) | Insurer association |
| SASIS AG | [sasis.ch](https://www.sasis.ch) | Industry data services |

### Data Exchange Standards

| Standard | Purpose | Documentation |
|----------|---------|---------------|
| Sumex XML | Electronic claims exchange | [ech.ch](https://www.ech.ch) |
| eCH Standards | Government data exchange | [ech.ch](https://www.ech.ch) |
| TARMED | Medical tariff system | [tarmed-browser.ch](https://www.tarmed-browser.ch) |

---

## Annual Compliance Calendar

| Month | Event | Action Required |
|-------|-------|-----------------|
| **January** | New insurance year | Apply new tariffs, process scheduled mutations |
| **April** | Q1 reporting | Submit required statistics |
| **June** | Premium submission | Submit next year's premiums to BAG |
| **September** | Premium approval | BAG approves premiums |
| **October** | Premium announcement | Notify customers of new premiums |
| **November** | Change deadline | Process insurer change requests |
| **December** | Year-end prep | Prepare for January transitions |

---

*Last Updated: 2026-01-26*

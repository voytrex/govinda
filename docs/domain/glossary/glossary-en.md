# English Glossary

Specialized terms of Swiss health insurance in alphabetical order.

> **Note**: This glossary serves as a reference. Official definitions can be found at [bag.admin.ch](https://www.bag.admin.ch).

---

## A

### Age Group

**Definition**: Categories for calculating health insurance premiums based on the age of the insured person.

| Language | Term |
|----------|------|
| English | Age Group |
| German | Altersgruppe |
| French | Catégorie d'âge |
| Italian | Categoria d'età |

**Categories**:
- Children (0-18 years)
- Young Adults (19-25 years)
- Adults (26+ years)

**Code Reference**: `AgeGroup.CHILD`, `AgeGroup.YOUNG_ADULT`, `AgeGroup.ADULT`

**See Also**: Premium, Children

---

### AHV Number

**Definition**: The Swiss social security number that uniquely identifies each person. Format: 756.XXXX.XXXX.XX.

| Language | Term |
|----------|------|
| English | AHV Number / Swiss Social Security Number |
| German | AHV-Nummer |
| French | Numéro AVS |
| Italian | Numero AVS |

**Code Reference**: `AhvNumber` Value Object

**See Also**: Person

**Official Source**: [ahv-iv.ch](https://www.ahv-iv.ch/en/)

---

## B

### Basic Insurance

**Definition**: The mandatory health insurance according to HIA that all persons residing in Switzerland must obtain.

| Language | Term |
|----------|------|
| English | Basic Insurance / Mandatory Health Insurance |
| German | Grundversicherung / Obligatorische Krankenpflegeversicherung (OKP) |
| French | Assurance obligatoire des soins (AOS) |
| Italian | Assicurazione obbligatoria delle cure medico-sanitarie (AOMS) |

**Code Reference**: `ProductType.KVG`

**See Also**: HIA, Mandatory Benefits

---

## C

### Canton

**Definition**: One of the 26 Swiss cantons, administrative units with their own healthcare responsibilities.

| Language | Term |
|----------|------|
| English | Canton |
| German | Kanton |
| French | Canton |
| Italian | Cantone |

**All 26 Cantons**: ZH, BE, LU, UR, SZ, OW, NW, GL, ZG, FR, SO, BS, BL, SH, AR, AI, SG, GR, AG, TG, TI, VD, VS, NE, GE, JU

**Code Reference**: `Canton` Enum

**See Also**: Premium Region

---

### Co-payment

**Definition**: The 10% share of costs that the insured person pays after reaching the deductible (max. CHF 700/year for adults).

| Language | Term |
|----------|------|
| English | Co-payment / Co-insurance |
| German | Selbstbehalt |
| French | Quote-part |
| Italian | Aliquota |

**Maximum Amounts**:
- Adults: CHF 700/year
- Children: CHF 350/year

**See Also**: Deductible, Cost Sharing

---

### Cost Sharing

**Definition**: The portion of medical costs that the insured person pays themselves (deductible + co-payment).

| Language | Term |
|----------|------|
| English | Cost Sharing |
| German | Kostenbeteiligung |
| French | Participation aux coûts |
| Italian | Partecipazione ai costi |

**Components**:
- Deductible (annual)
- Co-payment (10%, max. CHF 700)

**See Also**: Deductible, Co-payment

---

### Coverage

**Definition**: The insurance protection that a person receives through an insurance contract.

| Language | Term |
|----------|------|
| English | Coverage |
| German | Deckung |
| French | Couverture |
| Italian | Copertura |

**Code Reference**: `Coverage` Entity, `CoverageStatus`

**See Also**: Insurance Protection, HIA, ICA

---

## D

### Daily Allowance

**Definition**: Daily sickness allowance insurance - replaces lost wages during illness.

| Language | Term |
|----------|------|
| English | Daily Allowance / Sick Pay |
| German | Taggeld / Krankentaggeld |
| French | Indemnité journalière |
| Italian | Indennità giornaliera |

**Code Reference**: `ProductCategory.DAILY_ALLOWANCE`

**See Also**: ICA, Supplementary Insurance

---

### Deductible

**Definition**: The annual amount that the insured person pays before the insurance begins covering costs.

| Language | Term |
|----------|------|
| English | Deductible / Annual Deductible |
| German | Franchise / Jahresfranchise |
| French | Franchise |
| Italian | Franchigia |

**Available Levels**:
- Children: CHF 0, 100, 200, 300, 400, 600
- Adults: CHF 300, 500, 1000, 1500, 2000, 2500

**Code Reference**: `Franchise.CHF_300`, `Franchise.CHF_2500`, etc.

**See Also**: Co-payment, Cost Sharing

**Official Source**: [FOPH Deductible Info](https://www.bag.admin.ch/bag/en/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

## F

### Family Doctor

**Definition**: General practitioner who provides primary medical care and refers to specialists.

| Language | Term |
|----------|------|
| English | Family Doctor / GP |
| German | Hausarzt |
| French | Médecin de famille |
| Italian | Medico di famiglia |

**See Also**: Family Doctor Model

---

### Family Doctor Model

**Definition**: An alternative insurance model where all treatments must first go through the chosen family doctor.

| Language | Term |
|----------|------|
| English | Family Doctor Model |
| German | Hausarzt-Modell |
| French | Modèle médecin de famille |
| Italian | Modello medico di famiglia |

**Characteristics**:
- Premium discount: 10-20%
- Gatekeeping by family doctor
- Referral required for specialists

**Code Reference**: `InsuranceModel.HAUSARZT`

**See Also**: HMO, Telemedicine, Insurance Model

---

### FOPH

**Definition**: Federal Office of Public Health - the Swiss federal authority responsible for healthcare, including health insurance.

| Language | Term |
|----------|------|
| English | FOPH / Federal Office of Public Health |
| German | BAG / Bundesamt für Gesundheit |
| French | OFSP / Office fédéral de la santé publique |
| Italian | UFSP / Ufficio federale della sanità pubblica |

**Responsibilities**:
- Approval of health insurance premiums
- Definition of premium regions
- Supervision of insurers

**Official Source**: [bag.admin.ch](https://www.bag.admin.ch/bag/en/home.html)

---

## H

### Healthcare Provider

**Definition**: Persons or facilities that provide medical services (doctors, hospitals, pharmacies).

| Language | Term |
|----------|------|
| English | Healthcare Provider |
| German | Leistungserbringer |
| French | Fournisseur de prestations |
| Italian | Fornitore di prestazioni |

**See Also**: Doctor, Hospital

---

### HIA

**Definition**: Health Insurance Act - the federal law governing mandatory health insurance.

| Language | Term |
|----------|------|
| English | HIA / Health Insurance Act |
| German | KVG / Krankenversicherungsgesetz |
| French | LAMal / Loi fédérale sur l'assurance-maladie |
| Italian | LAMal / Legge federale sull'assicurazione malattie |

**Code Reference**: `ProductType.KVG`

**Official Source**: [HIA Law Text](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/en)

**See Also**: Basic Insurance, ICA

---

### HMO

**Definition**: Health Maintenance Organization - an insurance model where care is coordinated through an HMO center.

| Language | Term |
|----------|------|
| English | HMO / Health Maintenance Organization |
| German | HMO / HMO-Modell |
| French | HMO |
| Italian | HMO |

**Characteristics**:
- Premium discount: 10-25%
- Treatment at HMO center
- Group practice approach

**Code Reference**: `InsuranceModel.HMO`

**See Also**: Family Doctor Model, Telemedicine

---

### Household

**Definition**: A group of persons living together who are grouped for insurance purposes.

| Language | Term |
|----------|------|
| English | Household |
| German | Haushalt |
| French | Ménage |
| Italian | Nucleo familiare |

**Roles in Household**:
- Primary Insured (PRIMARY)
- Partner (PARTNER)
- Child (CHILD)

**Code Reference**: `Household` Entity, `HouseholdRole`

**See Also**: Policyholder, Family

---

## I

### ICA

**Definition**: Insurance Contract Act - the law for private insurance contracts, including supplementary insurance.

| Language | Term |
|----------|------|
| English | ICA / Insurance Contract Act |
| German | VVG / Versicherungsvertragsgesetz |
| French | LCA / Loi fédérale sur le contrat d'assurance |
| Italian | LCA / Legge federale sul contratto d'assicurazione |

**Code Reference**: `ProductType.VVG`

**Official Source**: [ICA Law Text](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/en)

**See Also**: Supplementary Insurance, HIA

---

### Insurance Model

**Definition**: The type of HIA insurance that governs access to medical services.

| Language | Term |
|----------|------|
| English | Insurance Model |
| German | Versicherungsmodell |
| French | Modèle d'assurance |
| Italian | Modello assicurativo |

**Models**:
- Standard (free choice of provider)
- HMO
- Family Doctor
- Telemedicine

**Code Reference**: `InsuranceModel` Enum

**See Also**: HMO, Family Doctor Model, Telemedicine

---

### Insured Person

**Definition**: The person protected by an insurance contract.

| Language | Term |
|----------|------|
| English | Insured Person |
| German | Versicherte Person |
| French | Personne assurée |
| Italian | Persona assicurata |

**Code Reference**: `Person` Entity, `Coverage.insuredPersonId`

**See Also**: Policyholder

---

## M

### Mandatory Benefits

**Definition**: The legally prescribed benefits that all health insurers must cover in basic insurance.

| Language | Term |
|----------|------|
| English | Mandatory Benefits |
| German | Pflichtleistungen |
| French | Prestations obligatoires |
| Italian | Prestazioni obbligatorie |

**Examples**:
- Doctor visits
- Hospital stays (general ward)
- Medications (specialties list)
- Maternity benefits

**See Also**: Basic Insurance, HIA

---

## P

### Policy

**Definition**: The insurance contract between the policyholder and the insurer.

| Language | Term |
|----------|------|
| English | Policy / Insurance Policy |
| German | Police / Versicherungspolice |
| French | Police d'assurance |
| Italian | Polizza assicurativa |

**Code Reference**: `Policy` Entity, `PolicyStatus`

**See Also**: Policyholder, Coverage

---

### Policyholder

**Definition**: The person who concludes the insurance contract and is responsible for premium payment.

| Language | Term |
|----------|------|
| English | Policyholder |
| German | Versicherungsnehmer |
| French | Preneur d'assurance |
| Italian | Contraente |

**Code Reference**: `Policy.policyholderId`, `HouseholdRole.PRIMARY`

**See Also**: Insured Person, Household

---

### Premium

**Definition**: The monthly or annual amount that the insured person pays to the health insurance.

| Language | Term |
|----------|------|
| English | Premium |
| German | Prämie / Versicherungsprämie |
| French | Prime |
| Italian | Premio |

**Influencing Factors (HIA)**:
- Premium region
- Age group
- Deductible
- Insurance model
- Accident inclusion

**Code Reference**: `PremiumEntry`, `Money`

**See Also**: Premium Region, Deductible

---

### Premium Region

**Definition**: Geographic zones defined by FOPH that determine premium levels.

| Language | Term |
|----------|------|
| English | Premium Region |
| German | Prämienregion |
| French | Région de primes |
| Italian | Regione di premio |

**Structure**:
- 1-3 regions per canton
- Region 1: urban (higher premiums)
- Region 3: rural (lower premiums)

**Code Reference**: `PremiumRegion` Entity

**See Also**: Canton, Premium

**Official Source**: [FOPH Premium Regions](https://www.bag.admin.ch/bag/en/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

### Premium Subsidy

**Definition**: Government subsidies for low-income persons to reduce health insurance premiums.

| Language | Term |
|----------|------|
| English | Premium Subsidy |
| German | Prämienverbilligung |
| French | Réduction de primes |
| Italian | Riduzione dei premi |

**Characteristics**:
- Cantonal responsibility
- Income-dependent
- Application to canton

**See Also**: Premium, Canton

---

## S

### Supplementary Insurance

**Definition**: Voluntary insurance under ICA that goes beyond basic insurance.

| Language | Term |
|----------|------|
| English | Supplementary Insurance |
| German | Zusatzversicherung |
| French | Assurance complémentaire |
| Italian | Assicurazione complementare |

**Categories**:
- Hospital supplementary (private/semi-private)
- Dental insurance
- Alternative medicine
- Travel insurance
- Daily allowance

**Code Reference**: `ProductType.VVG`, `ProductCategory`

**See Also**: ICA, Hospital Insurance

---

## T

### Telemedicine

**Definition**: Telemedicine model - an insurance model with mandatory telephone consultation before any visit.

| Language | Term |
|----------|------|
| English | Telemedicine Model |
| German | Telmed / Telemedizin-Modell |
| French | Télémédecine |
| Italian | Telemedicina |

**Characteristics**:
- Premium discount: 10-15%
- Call hotline before doctor visit
- Available 24/7

**Code Reference**: `InsuranceModel.TELMED`

**See Also**: HMO, Family Doctor Model

---

## Official Resources

| Resource | URL |
|----------|-----|
| FOPH Main Page | [bag.admin.ch](https://www.bag.admin.ch/bag/en/home.html) |
| Premium Comparison | [priminfo.admin.ch](https://www.priminfo.admin.ch/en/) |
| HIA Law Text | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/en) |
| ICA Law Text | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/en) |
| Specialties List | [spezialitätenliste.ch](https://www.spezialitätenliste.ch) |

---

*Last Updated: 2026-01-26*

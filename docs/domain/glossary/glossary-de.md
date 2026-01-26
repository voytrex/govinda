# Glossar Deutsch

Fachbegriffe der Schweizer Krankenversicherung in alphabetischer Reihenfolge.

> **Hinweis**: Dieses Glossar dient als primäre Referenz. Offizielle Definitionen finden Sie auf [bag.admin.ch](https://www.bag.admin.ch).

---

## A

### AHV-Nummer (f.)

**Definition**: Die Sozialversicherungsnummer der Schweiz, die jede Person eindeutig identifiziert. Format: 756.XXXX.XXXX.XX.

| Sprache | Begriff |
|---------|---------|
| Deutsch | AHV-Nummer |
| Französisch | Numéro AVS |
| Italienisch | Numero AVS |
| Englisch | AHV Number / Swiss Social Security Number |

**Code-Referenz**: `AhvNumber` Value Object

**Siehe auch**: Person

**Offizielle Quelle**: [ahv-iv.ch](https://www.ahv-iv.ch)

---

### Altersgruppe (f.)

**Definition**: Kategorien zur Berechnung der Krankenkassenprämien basierend auf dem Alter der versicherten Person.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Altersgruppe |
| Französisch | Catégorie d'âge |
| Italienisch | Categoria d'età |
| Englisch | Age Group |

**Kategorien**:
- Kinder (0-18 Jahre)
- Junge Erwachsene (19-25 Jahre)
- Erwachsene (26+ Jahre)

**Code-Referenz**: `AgeGroup.CHILD`, `AgeGroup.YOUNG_ADULT`, `AgeGroup.ADULT`

**Siehe auch**: Prämie, Kinder

---

## B

### BAG (n.)

**Definition**: Bundesamt für Gesundheit - die Schweizer Bundesbehörde, die für das Gesundheitswesen zuständig ist, einschliesslich der Krankenversicherung.

| Sprache | Begriff |
|---------|---------|
| Deutsch | BAG / Bundesamt für Gesundheit |
| Französisch | OFSP / Office fédéral de la santé publique |
| Italienisch | UFSP / Ufficio federale della sanità pubblica |
| Englisch | FOPH / Federal Office of Public Health |

**Zuständigkeiten**:
- Genehmigung der Krankenkassenprämien
- Definition der Prämienregionen
- Überwachung der Versicherer

**Offizielle Quelle**: [bag.admin.ch](https://www.bag.admin.ch)

---

## D

### Deckung (f.)

**Definition**: Der Versicherungsschutz, den eine Person durch einen Versicherungsvertrag erhält.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Deckung |
| Französisch | Couverture |
| Italienisch | Copertura |
| Englisch | Coverage |

**Code-Referenz**: `Coverage` Entity, `CoverageStatus`

**Siehe auch**: Versicherungsschutz, KVG, VVG

---

## F

### Franchise (f.)

**Definition**: Der jährliche Selbstbehalt, den die versicherte Person zahlt, bevor die Versicherung Kosten übernimmt.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Franchise / Jahresfranchise |
| Französisch | Franchise |
| Italienisch | Franchigia |
| Englisch | Deductible / Annual Deductible |

**Verfügbare Stufen**:
- Kinder: CHF 0, 100, 200, 300, 400, 600
- Erwachsene: CHF 300, 500, 1000, 1500, 2000, 2500

**Code-Referenz**: `Franchise.CHF_300`, `Franchise.CHF_2500`, etc.

**Siehe auch**: Selbstbehalt, Kostenbeteiligung

**Offizielle Quelle**: [BAG Franchise-Info](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

## G

### Grundversicherung (f.)

**Definition**: Die obligatorische Krankenversicherung gemäss KVG, die alle in der Schweiz wohnhaften Personen abschliessen müssen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Grundversicherung / Obligatorische Krankenpflegeversicherung (OKP) |
| Französisch | Assurance obligatoire des soins (AOS) |
| Italienisch | Assicurazione obbligatoria delle cure medico-sanitarie (AOMS) |
| Englisch | Basic Insurance / Mandatory Health Insurance |

**Code-Referenz**: `ProductType.KVG`

**Siehe auch**: KVG, Pflichtleistungen

---

## H

### Haushalt (m.)

**Definition**: Eine Gruppe von Personen, die zusammen leben und für Versicherungszwecke gruppiert werden.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Haushalt |
| Französisch | Ménage |
| Italienisch | Nucleo familiare |
| Englisch | Household |

**Rollen im Haushalt**:
- Hauptversicherter (PRIMARY)
- Partner (PARTNER)
- Kind (CHILD)

**Code-Referenz**: `Household` Entity, `HouseholdRole`

**Siehe auch**: Versicherungsnehmer, Familie

---

### Hausarzt-Modell (n.)

**Definition**: Ein alternatives Versicherungsmodell, bei dem alle Behandlungen zuerst über den gewählten Hausarzt erfolgen müssen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Hausarzt-Modell |
| Französisch | Modèle médecin de famille |
| Italienisch | Modello medico di famiglia |
| Englisch | Family Doctor Model |

**Eigenschaften**:
- Prämienrabatt: 10-20%
- Gatekeeping durch Hausarzt
- Überweisung für Spezialisten erforderlich

**Code-Referenz**: `InsuranceModel.HAUSARZT`

**Siehe auch**: HMO, Telmed, Versicherungsmodell

---

### HMO (n.)

**Definition**: Health Maintenance Organization - ein Versicherungsmodell, bei dem die Behandlung über ein HMO-Zentrum koordiniert wird.

| Sprache | Begriff |
|---------|---------|
| Deutsch | HMO / HMO-Modell |
| Französisch | HMO |
| Italienisch | HMO |
| Englisch | HMO / Health Maintenance Organization |

**Eigenschaften**:
- Prämienrabatt: 10-25%
- Behandlung im HMO-Zentrum
- Gruppenpraxis-Ansatz

**Code-Referenz**: `InsuranceModel.HMO`

**Siehe auch**: Hausarzt-Modell, Telmed

---

## K

### Kanton (m.)

**Definition**: Einer der 26 Schweizer Kantone, die administrative Einheiten mit eigenen Gesundheitskompetenzen sind.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Kanton |
| Französisch | Canton |
| Italienisch | Cantone |
| Englisch | Canton |

**Alle 26 Kantone**: ZH, BE, LU, UR, SZ, OW, NW, GL, ZG, FR, SO, BS, BL, SH, AR, AI, SG, GR, AG, TG, TI, VD, VS, NE, GE, JU

**Code-Referenz**: `Canton` Enum

**Siehe auch**: Prämienregion

---

### Kostenbeteiligung (f.)

**Definition**: Der Anteil der medizinischen Kosten, den die versicherte Person selbst trägt (Franchise + Selbstbehalt).

| Sprache | Begriff |
|---------|---------|
| Deutsch | Kostenbeteiligung |
| Französisch | Participation aux coûts |
| Italienisch | Partecipazione ai costi |
| Englisch | Cost Sharing |

**Bestandteile**:
- Franchise (jährlich)
- Selbstbehalt (10%, max. CHF 700)

**Siehe auch**: Franchise, Selbstbehalt

---

### KVG (n.)

**Definition**: Krankenversicherungsgesetz - das Bundesgesetz, das die obligatorische Krankenversicherung regelt.

| Sprache | Begriff |
|---------|---------|
| Deutsch | KVG / Krankenversicherungsgesetz |
| Französisch | LAMal / Loi fédérale sur l'assurance-maladie |
| Italienisch | LAMal / Legge federale sull'assicurazione malattie |
| Englisch | HIA / Health Insurance Act |

**Code-Referenz**: `ProductType.KVG`

**Offizielle Quelle**: [KVG Gesetzestext](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de)

**Siehe auch**: Grundversicherung, VVG

---

## L

### Leistungserbringer (m.)

**Definition**: Personen oder Einrichtungen, die medizinische Leistungen erbringen (Ärzte, Spitäler, Apotheken).

| Sprache | Begriff |
|---------|---------|
| Deutsch | Leistungserbringer |
| Französisch | Fournisseur de prestations |
| Italienisch | Fornitore di prestazioni |
| Englisch | Healthcare Provider |

**Siehe auch**: Arzt, Spital

---

## P

### Pflichtleistungen (f. pl.)

**Definition**: Die gesetzlich vorgeschriebenen Leistungen, die alle Krankenversicherer in der Grundversicherung abdecken müssen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Pflichtleistungen |
| Französisch | Prestations obligatoires |
| Italienisch | Prestazioni obbligatorie |
| Englisch | Mandatory Benefits |

**Beispiele**:
- Arztbesuche
- Spitalaufenthalte (Allgemeine Abteilung)
- Medikamente (Spezialitätenliste)
- Mutterschaftsleistungen

**Siehe auch**: Grundversicherung, KVG

---

### Police (f.)

**Definition**: Der Versicherungsvertrag zwischen Versicherungsnehmer und Versicherer.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Police / Versicherungspolice |
| Französisch | Police d'assurance |
| Italienisch | Polizza assicurativa |
| Englisch | Policy / Insurance Policy |

**Code-Referenz**: `Policy` Entity, `PolicyStatus`

**Siehe auch**: Versicherungsnehmer, Deckung

---

### Prämie (f.)

**Definition**: Der monatliche oder jährliche Betrag, den die versicherte Person an die Krankenversicherung zahlt.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Prämie / Versicherungsprämie |
| Französisch | Prime |
| Italienisch | Premio |
| Englisch | Premium |

**Einflussfaktoren (KVG)**:
- Prämienregion
- Altersgruppe
- Franchise
- Versicherungsmodell
- Unfalleinschluss

**Code-Referenz**: `PremiumEntry`, `Money`

**Siehe auch**: Prämienregion, Franchise

---

### Prämienregion (f.)

**Definition**: Vom BAG definierte geografische Zonen, die die Prämienhöhe bestimmen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Prämienregion |
| Französisch | Région de primes |
| Italienisch | Regione di premio |
| Englisch | Premium Region |

**Struktur**:
- 1-3 Regionen pro Kanton
- Region 1: städtisch (höhere Prämien)
- Region 3: ländlich (tiefere Prämien)

**Code-Referenz**: `PremiumRegion` Entity

**Siehe auch**: Kanton, Prämie

**Offizielle Quelle**: [BAG Prämienregionen](https://www.bag.admin.ch/bag/de/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

### Prämienverbilligung (f.)

**Definition**: Staatliche Zuschüsse für Personen mit niedrigem Einkommen zur Reduktion der Krankenkassenprämien.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Prämienverbilligung |
| Französisch | Réduction de primes |
| Italienisch | Riduzione dei premi |
| Englisch | Premium Subsidy |

**Eigenschaften**:
- Kantonale Zuständigkeit
- Einkommensabhängig
- Antrag beim Kanton

**Siehe auch**: Prämie, Kanton

---

## S

### Selbstbehalt (m.)

**Definition**: Der 10%-Anteil der Kosten, den die versicherte Person nach Erreichen der Franchise zahlt (max. CHF 700/Jahr für Erwachsene).

| Sprache | Begriff |
|---------|---------|
| Deutsch | Selbstbehalt |
| Französisch | Quote-part |
| Italienisch | Aliquota |
| Englisch | Co-payment / Co-insurance |

**Maximalbeträge**:
- Erwachsene: CHF 700/Jahr
- Kinder: CHF 350/Jahr

**Siehe auch**: Franchise, Kostenbeteiligung

---

### Spezialitätenliste (f.)

**Definition**: Die offizielle Liste der Medikamente, die von der Grundversicherung vergütet werden.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Spezialitätenliste (SL) |
| Französisch | Liste des spécialités (LS) |
| Italienisch | Elenco delle specialità (ES) |
| Englisch | Specialties List |

**Offizielle Quelle**: [spezialitätenliste.ch](https://www.spezialitätenliste.ch)

---

## T

### Taggeld (n.)

**Definition**: Krankentaggeldversicherung - ersetzt Lohnausfall bei Krankheit.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Taggeld / Krankentaggeld |
| Französisch | Indemnité journalière |
| Italienisch | Indennità giornaliera |
| Englisch | Daily Allowance / Sick Pay |

**Code-Referenz**: `ProductCategory.DAILY_ALLOWANCE`

**Siehe auch**: VVG, Zusatzversicherung

---

### Telmed (n.)

**Definition**: Telemedizin-Modell - ein Versicherungsmodell mit obligatorischer telefonischer Erstkonsultation.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Telmed / Telemedizin-Modell |
| Französisch | Télémédecine |
| Italienisch | Telemedicina |
| Englisch | Telemedicine Model |

**Eigenschaften**:
- Prämienrabatt: 10-15%
- Anruf bei Hotline vor Arztbesuch
- 24/7 verfügbar

**Code-Referenz**: `InsuranceModel.TELMED`

**Siehe auch**: HMO, Hausarzt-Modell

---

## U

### UVG (n.)

**Definition**: Unfallversicherungsgesetz - regelt die obligatorische Unfallversicherung für Arbeitnehmer.

| Sprache | Begriff |
|---------|---------|
| Deutsch | UVG / Unfallversicherungsgesetz |
| Französisch | LAA / Loi fédérale sur l'assurance-accidents |
| Italienisch | LAINF / Legge federale sull'assicurazione contro gli infortuni |
| Englisch | AIA / Accident Insurance Act |

**Relevanz für KVG**:
- Arbeitnehmer (>8h/Woche): UVG-versichert, KVG ohne Unfall möglich
- Selbstständige: Unfall muss im KVG eingeschlossen sein

**Siehe auch**: KVG, Unfalleinschluss

---

## V

### Versicherungsnehmer (m.)

**Definition**: Die Person, die den Versicherungsvertrag abschliesst und für die Prämienzahlung verantwortlich ist.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Versicherungsnehmer |
| Französisch | Preneur d'assurance |
| Italienisch | Contraente |
| Englisch | Policyholder |

**Code-Referenz**: `Policy.policyholderId`, `HouseholdRole.PRIMARY`

**Siehe auch**: Versicherte Person, Haushalt

---

### Versicherte Person (f.)

**Definition**: Die Person, die durch einen Versicherungsvertrag geschützt ist.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Versicherte Person |
| Französisch | Personne assurée |
| Italienisch | Persona assicurata |
| Englisch | Insured Person |

**Code-Referenz**: `Person` Entity, `Coverage.insuredPersonId`

**Siehe auch**: Versicherungsnehmer

---

### Versicherungsmodell (n.)

**Definition**: Die Art der KVG-Versicherung, die den Zugang zu medizinischen Leistungen regelt.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Versicherungsmodell |
| Französisch | Modèle d'assurance |
| Italienisch | Modello assicurativo |
| Englisch | Insurance Model |

**Modelle**:
- Standard (freie Arztwahl)
- HMO
- Hausarzt
- Telmed

**Code-Referenz**: `InsuranceModel` Enum

**Siehe auch**: HMO, Hausarzt-Modell, Telmed

---

### VVG (n.)

**Definition**: Versicherungsvertragsgesetz - das Gesetz für private Versicherungsverträge, einschliesslich Zusatzversicherungen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | VVG / Versicherungsvertragsgesetz |
| Französisch | LCA / Loi fédérale sur le contrat d'assurance |
| Italienisch | LCA / Legge federale sul contratto d'assicurazione |
| Englisch | ICA / Insurance Contract Act |

**Code-Referenz**: `ProductType.VVG`

**Offizielle Quelle**: [VVG Gesetzestext](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de)

**Siehe auch**: Zusatzversicherung, KVG

---

## Z

### Zusatzversicherung (f.)

**Definition**: Freiwillige Versicherungen nach VVG, die über die Grundversicherung hinausgehen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Zusatzversicherung |
| Französisch | Assurance complémentaire |
| Italienisch | Assicurazione complementare |
| Englisch | Supplementary Insurance |

**Kategorien**:
- Spitalzusatz (Privat/Halbprivat)
- Zahnversicherung
- Alternativmedizin
- Auslandversicherung
- Taggeld

**Code-Referenz**: `ProductType.VVG`, `ProductCategory`

**Siehe auch**: VVG, Spitalzusatzversicherung

---

## Offizielle Ressourcen

| Ressource | URL |
|-----------|-----|
| BAG Hauptseite | [bag.admin.ch](https://www.bag.admin.ch) |
| Prämienvergleich | [priminfo.admin.ch](https://www.priminfo.admin.ch) |
| KVG Gesetzestext | [fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/de) |
| VVG Gesetzestext | [fedlex.admin.ch/eli/cc/24/719_735_717/de](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/de) |
| Spezialitätenliste | [spezialitätenliste.ch](https://www.spezialitätenliste.ch) |

---

*Letzte Aktualisierung: 2026-01-26*

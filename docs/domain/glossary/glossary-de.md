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

### Datenpool (m.)

**Definition**: Gemeinsame Datenplattform der Schweizer Krankenversicherer für statistische Auswertungen und Benchmarks.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Datenpool |
| Französisch | Pool de données |
| Italienisch | Pool di dati |
| Englisch | Data Pool |

**Zweck**:
- Anonymisierte Leistungsdaten
- Kostenanalysen
- Qualitätsmessungen

**Siehe auch**: Tarifpool, SASIS

---

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

## F

### Forum Datenaustausch (n.)

**Definition**: Schweizer Organisation und Standardformat für den elektronischen Datenaustausch im Gesundheitswesen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Forum Datenaustausch |
| Französisch | Forum d'échange de données |
| Italienisch | Forum scambio dati |
| Englisch | Data Exchange Forum |

**Funktionen**:
- XML-Standard für Rechnungen (generalInvoiceRequest)
- EDI-Nachrichtenformate
- Branchenweite Interoperabilität

**Code-Referenz**: `SubmissionMethod.FORUM_XML`

**Offizielle Quelle**: [forum-datenaustausch.ch](https://www.forum-datenaustausch.ch)

**Siehe auch**: Leistungserbringer, Leistung

---

## G

### GLN (f.)

**Definition**: Global Location Number - die 13-stellige Identifikationsnummer für Teilnehmer im Schweizer Gesundheitswesen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | GLN / Global Location Number |
| Französisch | GLN |
| Italienisch | GLN |
| Englisch | GLN / Global Location Number |

**Format**: 76XXXXXXXXXXY (13 Ziffern mit Prüfziffer)

**Verwendung**:
- Ärzte und medizinische Fachpersonen
- Spitäler und Kliniken
- Apotheken
- Krankenversicherer

**Code-Referenz**: `providerGln`, `Provider.gln`

**Siehe auch**: Refdata, MedReg

---

### Grenzgänger (m.)

**Definition**: Person, die in einem Nachbarland wohnt und in der Schweiz arbeitet.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Grenzgänger |
| Französisch | Frontalier |
| Italienisch | Frontaliere |
| Englisch | Cross-border Worker |

**Länder mit Optionsrecht**:
- Deutschland
- Frankreich
- Italien
- Österreich

**Optionsrecht**: Innerhalb von 3 Monaten Wahl zwischen Schweizer KVG oder Heimatland-Versicherung.

**Code-Referenz**: `CrossBorderType.GRENZGAENGER`

**Siehe auch**: Optionsrecht, KVG

---

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

## I

### IPV (f.)

**Definition**: Individuelle Prämienverbilligung - kantonale Subvention zur Reduktion der Krankenkassenprämien für einkommensschwache Personen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | IPV / Individuelle Prämienverbilligung |
| Französisch | Subsides individuels |
| Italienisch | Riduzione individuale dei premi |
| Englisch | Individual Premium Subsidy |

**Merkmale**:
- Kantonale Zuständigkeit
- Einkommens- und vermögensabhängig
- Antrag beim zuständigen Kanton
- Kann direkt an Versicherer gezahlt werden

**Code-Referenz**: `BusinessPartner` mit `PartnerCategory.GOVERNMENT`

**Siehe auch**: Prämienverbilligung, Kanton

---

## L

### Leistung (f.)

**Definition**: Eine genehmigte medizinische Dienstleistung mit angewandter Kostenbeteiligung.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Leistung / Leistungsabrechnung |
| Französisch | Prestation |
| Italienisch | Prestazione |
| Englisch | Claim / Benefit |

**Prozess**:
1. Rechnungseingang vom Leistungserbringer
2. Validierung und Genehmigung
3. Kostenbeteiligung anwenden
4. Zahlung an Patient oder Leistungserbringer

**Code-Referenz**: `Claim` Entity, `ClaimStatus`

**Siehe auch**: Kostenbeteiligung, Tiers Payant, Tiers Garant

---

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

## R

### Refdata (n.)

**Definition**: Referenzdatenbank für das Schweizer Gesundheitswesen mit Stammdaten zu Leistungserbringern, Produkten und Organisationen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Refdata |
| Französisch | Refdata |
| Italienisch | Refdata |
| Englisch | Refdata |

**Inhalte**:
- GLN-Verzeichnis (Leistungserbringer)
- Artikelstamm (Medikamente, Medizinprodukte)
- Organisationsdaten

**Code-Referenz**: `RefdataService`

**Offizielle Quelle**: [refdata.ch](https://www.refdata.ch)

**Siehe auch**: GLN, SASIS

---

## S

### SASIS (f.)

**Definition**: SASIS AG - die Branchenorganisation der Schweizer Krankenversicherer für gemeinsame Dienste und Datenaustausch.

| Sprache | Begriff |
|---------|---------|
| Deutsch | SASIS AG |
| Französisch | SASIS SA |
| Italienisch | SASIS SA |
| Englisch | SASIS AG |

**Dienstleistungen**:
- VeKa (Versichertenkarte)
- Versicherungsnachweis-Dienst
- Branchenstatistiken

**Code-Referenz**: `SasisService`

**Offizielle Quelle**: [sasis.ch](https://www.sasis.ch)

**Siehe auch**: VeKa, Datenpool

---

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

### Sistierung (f.)

**Definition**: Temporäre Unterbrechung oder Aussetzung des Versicherungsschutzes.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Sistierung / Ruhen |
| Französisch | Suspension |
| Italienisch | Sospensione |
| Englisch | Suspension |

**Gründe**:
- Militärdienst (Militärsistierung)
- Auslandaufenthalt (Studium, Arbeit)
- Umzug

**Merkmale**:
- Prämienreduktion oder -befreiung möglich
- Zeitlich begrenzt
- Meldepflicht

**Code-Referenz**: `Suspension` Entity, `SuspensionReason`

**Siehe auch**: Deckung, Militärdienst

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

### Tarifpool (m.)

**Definition**: Gemeinsame Datenbank der Tarifinformationen für das Schweizer Gesundheitswesen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Tarifpool |
| Französisch | Pool tarifaire |
| Italienisch | Pool tariffario |
| Englisch | Tariff Pool |

**Inhalte**:
- TARMED-Positionen und Preise
- Taxpunktwerte pro Kanton
- Gültigkeitszeiträume

**Code-Referenz**: `TarifpoolService`

**Siehe auch**: TARMED, TARDOC, Datenpool

---

### TARMED (m.)

**Definition**: Der ambulante Arzttarif der Schweiz für medizinische Leistungen.

| Sprache | Begriff |
|---------|---------|
| Deutsch | TARMED |
| Französisch | TARMED |
| Italienisch | TARMED |
| Englisch | TARMED |

**Struktur**:
- Arztleistung (AL) - medizinischer Anteil
- Technische Leistung (TL) - Infrastrukturanteil
- Taxpunktwert (kantonal)

**Status**: Wird ab 2026 durch TARDOC ersetzt

**Code-Referenz**: `TariffType.TARMED`

**Siehe auch**: TARDOC, Taxpunktwert

---

### TARDOC (m.)

**Definition**: Der neue ambulante Einzelleistungstarif, der TARMED ab 2026 ersetzt.

| Sprache | Begriff |
|---------|---------|
| Deutsch | TARDOC |
| Französisch | TARDOC |
| Italienisch | TARDOC |
| Englisch | TARDOC |

**Neuerungen**:
- Aktualisierte Positionsstruktur
- Angepasste Tarifierung
- Bessere Abbildung moderner Medizin

**Code-Referenz**: `TariffType.TARDOC`

**Siehe auch**: TARMED, Tarifpool

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

### Tiers Garant (m.)

**Definition**: Zahlungsmodell, bei dem der Patient den Leistungserbringer bezahlt und die Versicherung um Rückerstattung bittet.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Tiers Garant / System des Tiers Garant |
| Französisch | Tiers garant |
| Italienisch | Terzo garante |
| Englisch | Third-Party Guarantor |

**Ablauf**:
1. Patient erhält Behandlung
2. Patient bezahlt Rechnung an Leistungserbringer
3. Patient reicht Rechnung bei Versicherer ein
4. Versicherer erstattet (abzüglich Kostenbeteiligung)

**Code-Referenz**: `PaymentModel.TIERS_GARANT`

**Siehe auch**: Tiers Payant, Leistung

---

### Tiers Payant (m.)

**Definition**: Zahlungsmodell, bei dem die Versicherung den Leistungserbringer direkt bezahlt.

| Sprache | Begriff |
|---------|---------|
| Deutsch | Tiers Payant / System des Tiers Payant |
| Französisch | Tiers payant |
| Italienisch | Terzo pagante |
| Englisch | Third-Party Payer |

**Ablauf**:
1. Patient erhält Behandlung
2. Leistungserbringer rechnet direkt mit Versicherer ab
3. Versicherer bezahlt Leistungserbringer
4. Patient erhält Rechnung für Kostenbeteiligung

**Vorteile**:
- Patient muss nicht vorfinanzieren
- Vor allem bei teuren Behandlungen/Spitalaufenthalten

**Code-Referenz**: `PaymentModel.TIERS_PAYANT`

**Siehe auch**: Tiers Garant, Leistung

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

### VeKa (f.)

**Definition**: Versichertenkarte - die Schweizer Krankenversicherungskarte mit Chipfunktion.

| Sprache | Begriff |
|---------|---------|
| Deutsch | VeKa / Versichertenkarte |
| Französisch | Carte d'assuré |
| Italienisch | Tessera d'assicurato |
| Englisch | Insurance Card |

**Funktionen**:
- Identifikation beim Leistungserbringer
- Nachweis des Versicherungsschutzes
- Enthält AHV-Nummer

**Format**: 80756 + 13-stellige Nummer

**Code-Referenz**: `InsuranceCard` Entity, `SasisService`

**Offizielle Quelle**: [sasis.ch](https://www.sasis.ch)

**Siehe auch**: SASIS, AHV-Nummer

---

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

*Letzte Aktualisierung: 2026-01-28*

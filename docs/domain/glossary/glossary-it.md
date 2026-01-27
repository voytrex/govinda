# Glossario Italiano

Termini specialistici dell'assicurazione malattia svizzera in ordine alfabetico.

> **Nota**: Questo glossario serve come riferimento. Le definizioni ufficiali si trovano su [bag.admin.ch](https://www.bag.admin.ch).

---

## A

### Aliquota (f.)

**Definizione**: La quota del 10% dei costi che la persona assicurata paga dopo aver raggiunto la franchigia (max. CHF 700/anno per gli adulti).

| Lingua | Termine |
|--------|---------|
| Italiano | Aliquota |
| Tedesco | Selbstbehalt |
| Francese | Quote-part |
| Inglese | Co-payment / Co-insurance |

**Importi massimi**:
- Adulti: CHF 700/anno
- Bambini: CHF 350/anno

**Vedi anche**: Franchigia, Partecipazione ai costi

---

### Assicurazione complementare (f.)

**Definizione**: Assicurazioni facoltative secondo la LCA che vanno oltre l'assicurazione di base.

| Lingua | Termine |
|--------|---------|
| Italiano | Assicurazione complementare |
| Tedesco | Zusatzversicherung |
| Francese | Assurance complémentaire |
| Inglese | Supplementary Insurance |

**Categorie**:
- Assicurazione ospedaliera (privata/semiprivata)
- Assicurazione dentale
- Medicina alternativa
- Assicurazione viaggio
- Indennità giornaliera

**Riferimento codice**: `ProductType.VVG`, `ProductCategory`

**Vedi anche**: LCA, Assicurazione ospedaliera

---

### Assicurazione obbligatoria delle cure medico-sanitarie (f.) / AOMS

**Definizione**: L'assicurazione malattia obbligatoria secondo la LAMal che ogni persona residente in Svizzera deve stipulare.

| Lingua | Termine |
|--------|---------|
| Italiano | Assicurazione obbligatoria delle cure medico-sanitarie (AOMS) |
| Tedesco | Grundversicherung / Obligatorische Krankenpflegeversicherung (OKP) |
| Francese | Assurance obligatoire des soins (AOS) |
| Inglese | Basic Insurance / Mandatory Health Insurance |

**Riferimento codice**: `ProductType.KVG`

**Vedi anche**: LAMal, Prestazioni obbligatorie

---

## C

### Cantone (m.)

**Definizione**: Uno dei 26 cantoni svizzeri, unità amministrative con proprie competenze in materia sanitaria.

| Lingua | Termine |
|--------|---------|
| Italiano | Cantone |
| Tedesco | Kanton |
| Francese | Canton |
| Inglese | Canton |

**I 26 cantoni**: ZH, BE, LU, UR, SZ, OW, NW, GL, ZG, FR, SO, BS, BL, SH, AR, AI, SG, GR, AG, TG, TI, VD, VS, NE, GE, JU

**Riferimento codice**: `Canton` Enum

**Vedi anche**: Regione di premio

---

### Categoria d'età (f.)

**Definizione**: Categorie per il calcolo dei premi dell'assicurazione malattia basate sull'età della persona assicurata.

| Lingua | Termine |
|--------|---------|
| Italiano | Categoria d'età |
| Tedesco | Altersgruppe |
| Francese | Catégorie d'âge |
| Inglese | Age Group |

**Categorie**:
- Bambini (0-18 anni)
- Giovani adulti (19-25 anni)
- Adulti (26+ anni)

**Riferimento codice**: `AgeGroup.CHILD`, `AgeGroup.YOUNG_ADULT`, `AgeGroup.ADULT`

**Vedi anche**: Premio, Bambini

---

### Contraente (m.)

**Definizione**: La persona che stipula il contratto di assicurazione ed è responsabile del pagamento dei premi.

| Lingua | Termine |
|--------|---------|
| Italiano | Contraente |
| Tedesco | Versicherungsnehmer |
| Francese | Preneur d'assurance |
| Inglese | Policyholder |

**Riferimento codice**: `Policy.policyholderId`, `HouseholdRole.PRIMARY`

**Vedi anche**: Persona assicurata, Nucleo familiare

---

### Copertura (f.)

**Definizione**: La protezione assicurativa che una persona ottiene attraverso un contratto di assicurazione.

| Lingua | Termine |
|--------|---------|
| Italiano | Copertura |
| Tedesco | Deckung |
| Francese | Couverture |
| Inglese | Coverage |

**Riferimento codice**: `Coverage` Entity, `CoverageStatus`

**Vedi anche**: Protezione assicurativa, LAMal, LCA

---

## F

### Fornitore di prestazioni (m.)

**Definizione**: Persone o strutture che forniscono prestazioni mediche (medici, ospedali, farmacie).

| Lingua | Termine |
|--------|---------|
| Italiano | Fornitore di prestazioni |
| Tedesco | Leistungserbringer |
| Francese | Fournisseur de prestations |
| Inglese | Healthcare Provider |

**Vedi anche**: Medico, Ospedale

---

### Franchigia (f.)

**Definizione**: La partecipazione annuale che la persona assicurata paga prima che l'assicurazione copra i costi.

| Lingua | Termine |
|--------|---------|
| Italiano | Franchigia |
| Tedesco | Franchise / Jahresfranchise |
| Francese | Franchise |
| Inglese | Deductible / Annual Deductible |

**Livelli disponibili**:
- Bambini: CHF 0, 100, 200, 300, 400, 600
- Adulti: CHF 300, 500, 1000, 1500, 2000, 2500

**Riferimento codice**: `Franchise.CHF_300`, `Franchise.CHF_2500`, ecc.

**Vedi anche**: Aliquota, Partecipazione ai costi

**Fonte ufficiale**: [UFSP Info franchigia](https://www.bag.admin.ch/bag/it/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

## H

### HMO (m.)

**Definizione**: Health Maintenance Organization - un modello assicurativo in cui le cure sono coordinate da un centro HMO.

| Lingua | Termine |
|--------|---------|
| Italiano | HMO |
| Tedesco | HMO / HMO-Modell |
| Francese | HMO |
| Inglese | HMO / Health Maintenance Organization |

**Caratteristiche**:
- Sconto sul premio: 10-25%
- Trattamento presso il centro HMO
- Approccio di gruppo medico

**Riferimento codice**: `InsuranceModel.HMO`

**Vedi anche**: Modello medico di famiglia, Telemedicina

---

## I

### Indennità giornaliera (f.)

**Definizione**: Assicurazione per indennità giornaliere in caso di malattia - sostituisce la perdita di salario.

| Lingua | Termine |
|--------|---------|
| Italiano | Indennità giornaliera |
| Tedesco | Taggeld / Krankentaggeld |
| Francese | Indemnité journalière |
| Inglese | Daily Allowance / Sick Pay |

**Riferimento codice**: `ProductCategory.DAILY_ALLOWANCE`

**Vedi anche**: LCA, Assicurazione complementare

---

## L

### LAMal (f.)

**Definizione**: Legge federale sull'assicurazione malattie - la legge federale che regola l'assicurazione malattia obbligatoria.

| Lingua | Termine |
|--------|---------|
| Italiano | LAMal / Legge federale sull'assicurazione malattie |
| Tedesco | KVG / Krankenversicherungsgesetz |
| Francese | LAMal / Loi fédérale sur l'assurance-maladie |
| Inglese | HIA / Health Insurance Act |

**Riferimento codice**: `ProductType.KVG`

**Fonte ufficiale**: [Testo di legge LAMal](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/it)

**Vedi anche**: Assicurazione obbligatoria delle cure, LCA

---

### LCA (f.)

**Definizione**: Legge federale sul contratto d'assicurazione - la legge per i contratti di assicurazione privati, comprese le assicurazioni complementari.

| Lingua | Termine |
|--------|---------|
| Italiano | LCA / Legge federale sul contratto d'assicurazione |
| Tedesco | VVG / Versicherungsvertragsgesetz |
| Francese | LCA / Loi fédérale sur le contrat d'assurance |
| Inglese | ICA / Insurance Contract Act |

**Riferimento codice**: `ProductType.VVG`

**Fonte ufficiale**: [Testo di legge LCA](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/it)

**Vedi anche**: Assicurazione complementare, LAMal

---

## M

### Medico di famiglia (m.)

**Definizione**: Medico generico che assicura l'assistenza medica di base e indirizza agli specialisti.

| Lingua | Termine |
|--------|---------|
| Italiano | Medico di famiglia |
| Tedesco | Hausarzt |
| Francese | Médecin de famille |
| Inglese | Family Doctor / GP |

**Vedi anche**: Modello medico di famiglia

---

### Modello assicurativo (m.)

**Definizione**: Il tipo di assicurazione LAMal che regola l'accesso alle prestazioni mediche.

| Lingua | Termine |
|--------|---------|
| Italiano | Modello assicurativo |
| Tedesco | Versicherungsmodell |
| Francese | Modèle d'assurance |
| Inglese | Insurance Model |

**Modelli**:
- Standard (libera scelta del medico)
- HMO
- Medico di famiglia
- Telemedicina

**Riferimento codice**: `InsuranceModel` Enum

**Vedi anche**: HMO, Modello medico di famiglia, Telemedicina

---

### Modello medico di famiglia (m.)

**Definizione**: Un modello assicurativo alternativo in cui tutti i trattamenti devono prima passare dal medico di famiglia scelto.

| Lingua | Termine |
|--------|---------|
| Italiano | Modello medico di famiglia |
| Tedesco | Hausarzt-Modell |
| Francese | Modèle médecin de famille |
| Inglese | Family Doctor Model |

**Caratteristiche**:
- Sconto sul premio: 10-20%
- Gatekeeping tramite il medico di famiglia
- Riferimento necessario per gli specialisti

**Riferimento codice**: `InsuranceModel.HAUSARZT`

**Vedi anche**: HMO, Telemedicina, Modello assicurativo

---

## N

### Nucleo familiare (m.)

**Definizione**: Un gruppo di persone che vivono insieme e sono raggruppate a fini assicurativi.

| Lingua | Termine |
|--------|---------|
| Italiano | Nucleo familiare |
| Tedesco | Haushalt |
| Francese | Ménage |
| Inglese | Household |

**Ruoli nel nucleo familiare**:
- Assicurato principale (PRIMARY)
- Partner (PARTNER)
- Figlio (CHILD)

**Riferimento codice**: `Household` Entity, `HouseholdRole`

**Vedi anche**: Contraente, Famiglia

---

### Numero AVS (m.)

**Definizione**: Il numero di previdenza sociale svizzero che identifica ogni persona in modo univoco. Formato: 756.XXXX.XXXX.XX.

| Lingua | Termine |
|--------|---------|
| Italiano | Numero AVS |
| Tedesco | AHV-Nummer |
| Francese | Numéro AVS |
| Inglese | AHV Number / Swiss Social Security Number |

**Riferimento codice**: `AhvNumber` Value Object

**Vedi anche**: Persona

**Fonte ufficiale**: [avs-ai.ch](https://www.ahv-iv.ch/it/)

---

## P

### Partecipazione ai costi (f.)

**Definizione**: La quota dei costi medici che la persona assicurata sostiene personalmente (franchigia + aliquota).

| Lingua | Termine |
|--------|---------|
| Italiano | Partecipazione ai costi |
| Tedesco | Kostenbeteiligung |
| Francese | Participation aux coûts |
| Inglese | Cost Sharing |

**Componenti**:
- Franchigia (annuale)
- Aliquota (10%, max. CHF 700)

**Vedi anche**: Franchigia, Aliquota

---

### Persona assicurata (f.)

**Definizione**: La persona protetta da un contratto di assicurazione.

| Lingua | Termine |
|--------|---------|
| Italiano | Persona assicurata |
| Tedesco | Versicherte Person |
| Francese | Personne assurée |
| Inglese | Insured Person |

**Riferimento codice**: `Person` Entity, `Coverage.insuredPersonId`

**Vedi anche**: Contraente

---

### Polizza assicurativa (f.)

**Definizione**: Il contratto di assicurazione tra il contraente e l'assicuratore.

| Lingua | Termine |
|--------|---------|
| Italiano | Polizza assicurativa |
| Tedesco | Police / Versicherungspolice |
| Francese | Police d'assurance |
| Inglese | Policy / Insurance Policy |

**Riferimento codice**: `Policy` Entity, `PolicyStatus`

**Vedi anche**: Contraente, Copertura

---

### Premio (m.)

**Definizione**: L'importo mensile o annuale che la persona assicurata paga all'assicurazione malattia.

| Lingua | Termine |
|--------|---------|
| Italiano | Premio |
| Tedesco | Prämie / Versicherungsprämie |
| Francese | Prime |
| Inglese | Premium |

**Fattori di influenza (LAMal)**:
- Regione di premio
- Categoria d'età
- Franchigia
- Modello assicurativo
- Inclusione infortuni

**Riferimento codice**: `PremiumEntry`, `Money`

**Vedi anche**: Regione di premio, Franchigia

---

### Prestazioni obbligatorie (f. pl.)

**Definizione**: Le prestazioni previste dalla legge che tutte le assicurazioni malattia devono coprire nell'assicurazione di base.

| Lingua | Termine |
|--------|---------|
| Italiano | Prestazioni obbligatorie |
| Tedesco | Pflichtleistungen |
| Francese | Prestations obligatoires |
| Inglese | Mandatory Benefits |

**Esempi**:
- Visite mediche
- Degenze ospedaliere (reparto comune)
- Medicamenti (elenco delle specialità)
- Prestazioni di maternità

**Vedi anche**: Assicurazione obbligatoria delle cure, LAMal

---

## R

### Regione di premio (f.)

**Definizione**: Zone geografiche definite dall'UFSP che determinano il livello dei premi.

| Lingua | Termine |
|--------|---------|
| Italiano | Regione di premio |
| Tedesco | Prämienregion |
| Francese | Région de primes |
| Inglese | Premium Region |

**Struttura**:
- 1-3 regioni per cantone
- Regione 1: urbana (premi più alti)
- Regione 3: rurale (premi più bassi)

**Riferimento codice**: `PremiumRegion` Entity

**Vedi anche**: Cantone, Premio

**Fonte ufficiale**: [UFSP Regioni di premio](https://www.bag.admin.ch/bag/it/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

### Riduzione dei premi (f.)

**Definizione**: Sussidi statali per le persone a basso reddito per ridurre i premi dell'assicurazione malattia.

| Lingua | Termine |
|--------|---------|
| Italiano | Riduzione dei premi |
| Tedesco | Prämienverbilligung |
| Francese | Réduction de primes |
| Inglese | Premium Subsidy |

**Caratteristiche**:
- Competenza cantonale
- In base al reddito
- Richiesta al cantone

**Vedi anche**: Premio, Cantone

---

## T

### Telemedicina (f.)

**Definizione**: Modello di telemedicina - un modello assicurativo con consultazione telefonica obbligatoria prima di qualsiasi visita.

| Lingua | Termine |
|--------|---------|
| Italiano | Telemedicina |
| Tedesco | Telmed / Telemedizin-Modell |
| Francese | Télémédecine |
| Inglese | Telemedicine Model |

**Caratteristiche**:
- Sconto sul premio: 10-15%
- Chiamata alla hotline prima della visita medica
- Disponibile 24/7

**Riferimento codice**: `InsuranceModel.TELMED`

**Vedi anche**: HMO, Modello medico di famiglia

---

## U

### UFSP (m.)

**Definizione**: Ufficio federale della sanità pubblica - l'autorità federale svizzera responsabile della sanità, compresa l'assicurazione malattia.

| Lingua | Termine |
|--------|---------|
| Italiano | UFSP / Ufficio federale della sanità pubblica |
| Tedesco | BAG / Bundesamt für Gesundheit |
| Francese | OFSP / Office fédéral de la santé publique |
| Inglese | FOPH / Federal Office of Public Health |

**Responsabilità**:
- Approvazione dei premi dell'assicurazione malattia
- Definizione delle regioni di premio
- Supervisione degli assicuratori

**Fonte ufficiale**: [bag.admin.ch](https://www.bag.admin.ch/bag/it/home.html)

---

## Risorse ufficiali

| Risorsa | URL |
|---------|-----|
| UFSP Pagina principale | [bag.admin.ch](https://www.bag.admin.ch/bag/it/home.html) |
| Confronto premi | [priminfo.admin.ch](https://www.priminfo.admin.ch/it/) |
| Testo di legge LAMal | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/it) |
| Testo di legge LCA | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/it) |
| Elenco delle specialità | [spezialitätenliste.ch](https://www.spezialitätenliste.ch) |

---

*Ultimo aggiornamento: 2026-01-26*

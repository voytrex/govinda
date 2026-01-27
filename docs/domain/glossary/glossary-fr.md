# Glossaire Français

Termes spécialisés de l'assurance maladie suisse par ordre alphabétique.

> **Note**: Ce glossaire sert de référence. Les définitions officielles se trouvent sur [bag.admin.ch](https://www.bag.admin.ch).

---

## A

### Assurance complémentaire (f.)

**Définition**: Assurances facultatives selon la LCA qui vont au-delà de l'assurance de base.

| Langue | Terme |
|--------|-------|
| Français | Assurance complémentaire |
| Allemand | Zusatzversicherung |
| Italien | Assicurazione complementare |
| Anglais | Supplementary Insurance |

**Catégories**:
- Assurance hospitalisation (privé/semi-privé)
- Assurance dentaire
- Médecine alternative
- Assurance voyage
- Indemnité journalière

**Référence code**: `ProductType.VVG`, `ProductCategory`

**Voir aussi**: LCA, Assurance hospitalisation

---

### Assurance obligatoire des soins (f.) / AOS

**Définition**: L'assurance maladie obligatoire selon la LAMal que toute personne résidant en Suisse doit contracter.

| Langue | Terme |
|--------|-------|
| Français | Assurance obligatoire des soins (AOS) |
| Allemand | Grundversicherung / Obligatorische Krankenpflegeversicherung (OKP) |
| Italien | Assicurazione obbligatoria delle cure medico-sanitarie (AOMS) |
| Anglais | Basic Insurance / Mandatory Health Insurance |

**Référence code**: `ProductType.KVG`

**Voir aussi**: LAMal, Prestations obligatoires

---

## C

### Canton (m.)

**Définition**: L'un des 26 cantons suisses, unités administratives avec leurs propres compétences en matière de santé.

| Langue | Terme |
|--------|-------|
| Français | Canton |
| Allemand | Kanton |
| Italien | Cantone |
| Anglais | Canton |

**Les 26 cantons**: ZH, BE, LU, UR, SZ, OW, NW, GL, ZG, FR, SO, BS, BL, SH, AR, AI, SG, GR, AG, TG, TI, VD, VS, NE, GE, JU

**Référence code**: `Canton` Enum

**Voir aussi**: Région de primes

---

### Catégorie d'âge (f.)

**Définition**: Catégories pour le calcul des primes d'assurance maladie basées sur l'âge de la personne assurée.

| Langue | Terme |
|--------|-------|
| Français | Catégorie d'âge |
| Allemand | Altersgruppe |
| Italien | Categoria d'età |
| Anglais | Age Group |

**Catégories**:
- Enfants (0-18 ans)
- Jeunes adultes (19-25 ans)
- Adultes (26+ ans)

**Référence code**: `AgeGroup.CHILD`, `AgeGroup.YOUNG_ADULT`, `AgeGroup.ADULT`

**Voir aussi**: Prime, Enfants

---

### Couverture (f.)

**Définition**: La protection d'assurance qu'une personne obtient par un contrat d'assurance.

| Langue | Terme |
|--------|-------|
| Français | Couverture |
| Allemand | Deckung |
| Italien | Copertura |
| Anglais | Coverage |

**Référence code**: `Coverage` Entity, `CoverageStatus`

**Voir aussi**: Protection d'assurance, LAMal, LCA

---

## F

### Fournisseur de prestations (m.)

**Définition**: Personnes ou établissements qui fournissent des prestations médicales (médecins, hôpitaux, pharmacies).

| Langue | Terme |
|--------|-------|
| Français | Fournisseur de prestations |
| Allemand | Leistungserbringer |
| Italien | Fornitore di prestazioni |
| Anglais | Healthcare Provider |

**Voir aussi**: Médecin, Hôpital

---

### Franchise (f.)

**Définition**: La participation annuelle que la personne assurée paie avant que l'assurance ne prenne en charge les coûts.

| Langue | Terme |
|--------|-------|
| Français | Franchise |
| Allemand | Franchise / Jahresfranchise |
| Italien | Franchigia |
| Anglais | Deductible / Annual Deductible |

**Niveaux disponibles**:
- Enfants: CHF 0, 100, 200, 300, 400, 600
- Adultes: CHF 300, 500, 1000, 1500, 2000, 2500

**Référence code**: `Franchise.CHF_300`, `Franchise.CHF_2500`, etc.

**Voir aussi**: Quote-part, Participation aux coûts

**Source officielle**: [OFSP Franchise-Info](https://www.bag.admin.ch/bag/fr/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

## H

### HMO (m.)

**Définition**: Health Maintenance Organization - un modèle d'assurance où les soins sont coordonnés par un centre HMO.

| Langue | Terme |
|--------|-------|
| Français | HMO |
| Allemand | HMO / HMO-Modell |
| Italien | HMO |
| Anglais | HMO / Health Maintenance Organization |

**Caractéristiques**:
- Rabais de prime: 10-25%
- Traitement au centre HMO
- Approche de groupe médical

**Référence code**: `InsuranceModel.HMO`

**Voir aussi**: Modèle médecin de famille, Télémédecine

---

## I

### Indemnité journalière (f.)

**Définition**: Assurance d'indemnités journalières en cas de maladie - remplace la perte de salaire.

| Langue | Terme |
|--------|-------|
| Français | Indemnité journalière |
| Allemand | Taggeld / Krankentaggeld |
| Italien | Indennità giornaliera |
| Anglais | Daily Allowance / Sick Pay |

**Référence code**: `ProductCategory.DAILY_ALLOWANCE`

**Voir aussi**: LCA, Assurance complémentaire

---

## L

### LAMal (f.)

**Définition**: Loi fédérale sur l'assurance-maladie - la loi fédérale régissant l'assurance maladie obligatoire.

| Langue | Terme |
|--------|-------|
| Français | LAMal / Loi fédérale sur l'assurance-maladie |
| Allemand | KVG / Krankenversicherungsgesetz |
| Italien | LAMal / Legge federale sull'assicurazione malattie |
| Anglais | HIA / Health Insurance Act |

**Référence code**: `ProductType.KVG`

**Source officielle**: [Texte de loi LAMal](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/fr)

**Voir aussi**: Assurance obligatoire des soins, LCA

---

### LCA (f.)

**Définition**: Loi fédérale sur le contrat d'assurance - la loi pour les contrats d'assurance privés, y compris les assurances complémentaires.

| Langue | Terme |
|--------|-------|
| Français | LCA / Loi fédérale sur le contrat d'assurance |
| Allemand | VVG / Versicherungsvertragsgesetz |
| Italien | LCA / Legge federale sul contratto d'assicurazione |
| Anglais | ICA / Insurance Contract Act |

**Référence code**: `ProductType.VVG`

**Source officielle**: [Texte de loi LCA](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/fr)

**Voir aussi**: Assurance complémentaire, LAMal

---

## M

### Médecin de famille (m.)

**Définition**: Médecin généraliste qui assure le suivi médical de base et oriente vers les spécialistes.

| Langue | Terme |
|--------|-------|
| Français | Médecin de famille |
| Allemand | Hausarzt |
| Italien | Medico di famiglia |
| Anglais | Family Doctor / GP |

**Voir aussi**: Modèle médecin de famille

---

### Ménage (m.)

**Définition**: Un groupe de personnes vivant ensemble et regroupées à des fins d'assurance.

| Langue | Terme |
|--------|-------|
| Français | Ménage |
| Allemand | Haushalt |
| Italien | Nucleo familiare |
| Anglais | Household |

**Rôles dans le ménage**:
- Assuré principal (PRIMARY)
- Partenaire (PARTNER)
- Enfant (CHILD)

**Référence code**: `Household` Entity, `HouseholdRole`

**Voir aussi**: Preneur d'assurance, Famille

---

### Modèle d'assurance (m.)

**Définition**: Le type d'assurance LAMal qui régit l'accès aux prestations médicales.

| Langue | Terme |
|--------|-------|
| Français | Modèle d'assurance |
| Allemand | Versicherungsmodell |
| Italien | Modello assicurativo |
| Anglais | Insurance Model |

**Modèles**:
- Standard (libre choix du médecin)
- HMO
- Médecin de famille
- Télémédecine

**Référence code**: `InsuranceModel` Enum

**Voir aussi**: HMO, Modèle médecin de famille, Télémédecine

---

### Modèle médecin de famille (m.)

**Définition**: Un modèle d'assurance alternatif où tous les traitements doivent d'abord passer par le médecin de famille choisi.

| Langue | Terme |
|--------|-------|
| Français | Modèle médecin de famille |
| Allemand | Hausarzt-Modell |
| Italien | Modello medico di famiglia |
| Anglais | Family Doctor Model |

**Caractéristiques**:
- Rabais de prime: 10-20%
- Gatekeeping par le médecin de famille
- Référence nécessaire pour les spécialistes

**Référence code**: `InsuranceModel.HAUSARZT`

**Voir aussi**: HMO, Télémédecine, Modèle d'assurance

---

## N

### Numéro AVS (m.)

**Définition**: Le numéro de sécurité sociale suisse qui identifie chaque personne de manière unique. Format: 756.XXXX.XXXX.XX.

| Langue | Terme |
|--------|-------|
| Français | Numéro AVS |
| Allemand | AHV-Nummer |
| Italien | Numero AVS |
| Anglais | AHV Number / Swiss Social Security Number |

**Référence code**: `AhvNumber` Value Object

**Voir aussi**: Personne

**Source officielle**: [avs-ai.ch](https://www.ahv-iv.ch/fr/)

---

## O

### OFSP (m.)

**Définition**: Office fédéral de la santé publique - l'autorité fédérale suisse responsable de la santé, y compris l'assurance maladie.

| Langue | Terme |
|--------|-------|
| Français | OFSP / Office fédéral de la santé publique |
| Allemand | BAG / Bundesamt für Gesundheit |
| Italien | UFSP / Ufficio federale della sanità pubblica |
| Anglais | FOPH / Federal Office of Public Health |

**Responsabilités**:
- Approbation des primes d'assurance maladie
- Définition des régions de primes
- Surveillance des assureurs

**Source officielle**: [bag.admin.ch](https://www.bag.admin.ch/bag/fr/home.html)

---

## P

### Participation aux coûts (f.)

**Définition**: La part des frais médicaux que la personne assurée assume elle-même (franchise + quote-part).

| Langue | Terme |
|--------|-------|
| Français | Participation aux coûts |
| Allemand | Kostenbeteiligung |
| Italien | Partecipazione ai costi |
| Anglais | Cost Sharing |

**Composants**:
- Franchise (annuelle)
- Quote-part (10%, max. CHF 700)

**Voir aussi**: Franchise, Quote-part

---

### Personne assurée (f.)

**Définition**: La personne protégée par un contrat d'assurance.

| Langue | Terme |
|--------|-------|
| Français | Personne assurée |
| Allemand | Versicherte Person |
| Italien | Persona assicurata |
| Anglais | Insured Person |

**Référence code**: `Person` Entity, `Coverage.insuredPersonId`

**Voir aussi**: Preneur d'assurance

---

### Police d'assurance (f.)

**Définition**: Le contrat d'assurance entre le preneur d'assurance et l'assureur.

| Langue | Terme |
|--------|-------|
| Français | Police d'assurance |
| Allemand | Police / Versicherungspolice |
| Italien | Polizza assicurativa |
| Anglais | Policy / Insurance Policy |

**Référence code**: `Policy` Entity, `PolicyStatus`

**Voir aussi**: Preneur d'assurance, Couverture

---

### Preneur d'assurance (m.)

**Définition**: La personne qui conclut le contrat d'assurance et est responsable du paiement des primes.

| Langue | Terme |
|--------|-------|
| Français | Preneur d'assurance |
| Allemand | Versicherungsnehmer |
| Italien | Contraente |
| Anglais | Policyholder |

**Référence code**: `Policy.policyholderId`, `HouseholdRole.PRIMARY`

**Voir aussi**: Personne assurée, Ménage

---

### Prestations obligatoires (f. pl.)

**Définition**: Les prestations légalement prescrites que tous les assureurs maladie doivent couvrir dans l'assurance de base.

| Langue | Terme |
|--------|-------|
| Français | Prestations obligatoires |
| Allemand | Pflichtleistungen |
| Italien | Prestazioni obbligatorie |
| Anglais | Mandatory Benefits |

**Exemples**:
- Consultations médicales
- Séjours hospitaliers (division commune)
- Médicaments (liste des spécialités)
- Prestations de maternité

**Voir aussi**: Assurance obligatoire des soins, LAMal

---

### Prime (f.)

**Définition**: Le montant mensuel ou annuel que la personne assurée paie à l'assurance maladie.

| Langue | Terme |
|--------|-------|
| Français | Prime |
| Allemand | Prämie / Versicherungsprämie |
| Italien | Premio |
| Anglais | Premium |

**Facteurs d'influence (LAMal)**:
- Région de primes
- Catégorie d'âge
- Franchise
- Modèle d'assurance
- Inclusion des accidents

**Référence code**: `PremiumEntry`, `Money`

**Voir aussi**: Région de primes, Franchise

---

## Q

### Quote-part (f.)

**Définition**: La part de 10% des coûts que la personne assurée paie après avoir atteint la franchise (max. CHF 700/an pour les adultes).

| Langue | Terme |
|--------|-------|
| Français | Quote-part |
| Allemand | Selbstbehalt |
| Italien | Aliquota |
| Anglais | Co-payment / Co-insurance |

**Montants maximaux**:
- Adultes: CHF 700/an
- Enfants: CHF 350/an

**Voir aussi**: Franchise, Participation aux coûts

---

## R

### Réduction de primes (f.)

**Définition**: Subventions étatiques pour les personnes à faible revenu afin de réduire les primes d'assurance maladie.

| Langue | Terme |
|--------|-------|
| Français | Réduction de primes |
| Allemand | Prämienverbilligung |
| Italien | Riduzione dei premi |
| Anglais | Premium Subsidy |

**Caractéristiques**:
- Compétence cantonale
- Selon le revenu
- Demande auprès du canton

**Voir aussi**: Prime, Canton

---

### Région de primes (f.)

**Définition**: Zones géographiques définies par l'OFSP qui déterminent le niveau des primes.

| Langue | Terme |
|--------|-------|
| Français | Région de primes |
| Allemand | Prämienregion |
| Italien | Regione di premio |
| Anglais | Premium Region |

**Structure**:
- 1-3 régions par canton
- Région 1: urbaine (primes plus élevées)
- Région 3: rurale (primes plus basses)

**Référence code**: `PremiumRegion` Entity

**Voir aussi**: Canton, Prime

**Source officielle**: [OFSP Régions de primes](https://www.bag.admin.ch/bag/fr/home/versicherungen/krankenversicherung/krankenversicherung-versicherte-mit-wohnsitz-in-der-schweiz/praemien-franchisen.html)

---

## T

### Télémédecine (f.)

**Définition**: Modèle de télémédecine - un modèle d'assurance avec consultation téléphonique obligatoire avant toute visite.

| Langue | Terme |
|--------|-------|
| Français | Télémédecine |
| Allemand | Telmed / Telemedizin-Modell |
| Italien | Telemedicina |
| Anglais | Telemedicine Model |

**Caractéristiques**:
- Rabais de prime: 10-15%
- Appel à la hotline avant visite médicale
- Disponible 24/7

**Référence code**: `InsuranceModel.TELMED`

**Voir aussi**: HMO, Modèle médecin de famille

---

## Ressources officielles

| Ressource | URL |
|-----------|-----|
| OFSP Page principale | [bag.admin.ch](https://www.bag.admin.ch/bag/fr/home.html) |
| Comparaison des primes | [priminfo.admin.ch](https://www.priminfo.admin.ch/fr/) |
| Texte de loi LAMal | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/1995/1328_1328_1328/fr) |
| Texte de loi LCA | [fedlex.admin.ch](https://www.fedlex.admin.ch/eli/cc/24/719_735_717/fr) |
| Liste des spécialités | [spezialitätenliste.ch](https://www.spezialitätenliste.ch) |

---

*Dernière mise à jour: 2026-01-26*

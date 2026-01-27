# Govinda ERP

**Open Source Enterprise Resource Planning for Swiss Health Insurance**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)

## Overview

Govinda is a modern, modular ERP system designed specifically for Swiss health insurance companies (Krankenversicherungen). It supports both mandatory basic insurance (KVG) and supplementary insurance (VVG) according to Swiss federal regulations.

### Key Features

- **Multi-tenant Architecture**: Support multiple insurance brands/entities
- **Swiss Compliance**: Built for KVG/VVG regulations and BAG requirements
- **Temporal Data Model**: Full history tracking with bitemporal support
- **Multi-language**: German, French, Italian, and English
- **Modern API**: RESTful API with OpenAPI/Swagger documentation
- **Domain-Driven Design**: Clean architecture with bounded contexts

## Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.2+ |
| Database | PostgreSQL 18+ |
| Build | Maven |
| API Docs | SpringDoc OpenAPI |
| Testing | JUnit 5, Mockito, Testcontainers |

## Project Structure

```
govinda/
├── backend/
│   ├── govinda-app/          # Main application & configuration
│   ├── govinda-common/       # Shared kernel (security, i18n, audit)
│   ├── govinda-masterdata/   # Person, household, address management
│   │   # Future modules (to be implemented):
│   │   # ├── govinda-product/      # Products and tariffs
│   │   # ├── govinda-contract/     # Policies and coverages
│   │   # ├── govinda-premium/      # Premium calculation engine
│   │   # └── govinda-billing/      # Invoicing and payments
├── frontend/                 # React frontend (Phase 2)
├── infrastructure/           # Docker, Kubernetes configs
├── data/reference/           # Swiss reference data (regions, PLZ)
└── docs/                     # Documentation & ADRs
```

## Modules

### MVP (Phase 1)
- **Master Data**: Insured persons, households, addresses with full history
- **Products**: KVG basic insurance (Standard, HMO, Hausarzt, Telmed), VVG supplementary (Dental, Alternativmedizin, Travel)
- **Contracts**: Policy and coverage management with mutations
- **Premium Calculation**: Age, region, franchise, and model-based calculation
- **Billing**: Invoice generation and payment processing

### Phase 2
- Cost sharing calculation (Franchise, Selbstbehalt)
- Benefit statements
- Provider management
- Financial accounting (Fibu/GL)
- BAG statistics & reporting
- Document generation

### Phase 3
- Dunning / debt collection (Inkasso)
- Claims processing (Sumex XML import)
- External integrations (SASIS, Common Interface)

## Getting Started

### Prerequisites

- JDK 21+
- Docker & Docker Compose
- PostgreSQL 18+ (or use Docker)

### Quick Start

```bash
# Clone the repository
git clone https://github.com/voytrex/govinda.git
cd govinda

# Run the application (starts PostgreSQL + app)
./scripts/run-local.sh

# Manual steps
docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres

# Build the project (from root)
mvn clean install -DskipTests

# Run the application - Recommended: run directly from app module
cd backend/govinda-app
mvn spring-boot:run

# Alternative: run from backend directory (builds dependencies first)
cd backend
mvn -rf govinda-app spring-boot:run

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

### Running Tests

```bash
# Run all tests
mvn test
```

## API Overview

The API follows domain-driven design principles with clear bounded contexts:

```
/api/v1
├── /masterdata           # Persons, households, addresses
│   ├── /persons
│   ├── /households
│   └── /regions
├── /products             # Product catalog and tariffs
├── /contracts            # Policies and coverages
│   ├── /policies
│   └── /coverages
├── /premiums             # Premium calculation
└── /billing              # Invoices and payments
```

See [API Documentation](docs/api/) for detailed specifications.

## Swiss Health Insurance Context

### Supported Products

**KVG (Grundversicherung)**
- Basic mandatory health insurance
- Models: Standard, HMO, Hausarzt, Telmed
- Franchise options: CHF 300 - 2500 (adults), CHF 0 (children)

**VVG (Zusatzversicherung)**
- Alternativmedizin (complementary medicine)
- Zahnversicherung (dental)
- Auslandversicherung (travel)

### Regulatory Compliance

- KVG (Krankenversicherungsgesetz)
- VVG (Versicherungsvertragsgesetz)
- BAG (Bundesamt für Gesundheit) reporting requirements
- All 26 cantons and BAG premium regions supported

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) and the [Developer Guide](docs/development/developer-guide.md) for setup and workflow details.

### Development Guidelines

- Follow standard Java coding conventions
- Write tests first (TDD)
- Document public APIs with Javadoc
- See [Definition of Done](docs/development/definition-of-done.md)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Links

- **Website**: https://www.voytrex.net/
- **GitHub**: https://github.com/voytrex
- **Documentation**: [docs/](docs/)

---

Made with care for Swiss healthcare by [Voytrex](https://www.voytrex.net/)

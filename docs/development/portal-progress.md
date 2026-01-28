# Customer Portal Implementation Progress

This document tracks implementation progress and next steps for the customer portal backend.
Update this file at the end of each phase.

## Current Phase

Phase 2: Cases + Identity Mapping (portal backend MVP)

## Completed

- Phase 1: Portal module skeleton and profile endpoint
  - `govinda-portal` module added and wired into build.
  - `GET /api/portal/v1/profile` implemented.
- Phase 2: Portal cases and identity mapping
  - `POST /api/portal/v1/cases` + `GET /api/portal/v1/cases` implemented.
  - `PortalCase` persistence (Flyway + JPA).
  - Customer identity mapping via `customer_identity`.
  - `X-Portal-Subject` header resolves `personId`.

## Next Steps

- Phase 3: Profile update
  - Add `PATCH /api/portal/v1/profile` for limited fields.
  - Validate fields and reuse `PersonService` update logic.
- Phase 4: Document list/download (stubs until document module exists)
  - Define document metadata model and storage adapter.
  - Implement `/api/portal/v1/documents`.
- Phase 5: Invoices + payment intent (depends on billing module)
  - Implement `govinda-billing` invoices.
  - Add portal invoice endpoints and payment intent.
- Phase 6: Notifications and preferences
  - Add notification event model and preferences endpoints.

## Last Updated

2026-01-28

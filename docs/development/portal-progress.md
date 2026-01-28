# Customer Portal Implementation Progress

This document tracks implementation progress and next steps for the customer portal backend.
Update this file at the end of each phase.

## Current Phase

Phase 5: Invoices + payment intent (depends on billing module)

## Completed

- Phase 1: Portal module skeleton and profile endpoint
  - `govinda-portal` module added and wired into build.
  - `GET /api/portal/v1/profile` implemented.
- Phase 2: Portal cases and identity mapping
  - `POST /api/portal/v1/cases` + `GET /api/portal/v1/cases` implemented.
  - `PortalCase` persistence (Flyway + JPA).
  - Customer identity mapping via `customer_identity`.
  - `X-Portal-Subject` header resolves `personId`.
- Phase 3: Profile update
  - `PATCH /api/portal/v1/profile` implemented for limited fields.
- Phase 4: Documents (metadata)
  - `GET /api/portal/v1/documents` + `GET /api/portal/v1/documents/{id}` implemented.
  - Document metadata persistence (Flyway + JPA).

## Next Steps

- Phase 5: Invoices + payment intent (depends on billing module)
  - Implement `govinda-billing` invoices.
  - Add portal invoice endpoints and payment intent.
- Phase 6: Notifications and preferences
  - Add notification event model and preferences endpoints.

## Last Updated

2026-01-28

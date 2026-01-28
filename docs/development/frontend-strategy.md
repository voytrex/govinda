# Frontend Strategy

This document defines the overall frontend strategy for Govinda ERP. The platform is a modular monolith backend
with multiple client types to match different user needs.

## Goals

- Provide a rich, efficient desktop experience for complex ERP workflows.
- Provide a lightweight web client for simple masterdata tasks and administrative workflows.
- Keep a single backend source of truth for business rules, security, and i18n.
- Prepare for a future mobile client without re-architecting the backend.

## Non-goals (for now)

- Full parity between desktop and web clients.
- Offline-first web client.
- Mobile client implementation (design only).

## Client Types

### 1) Rich/Fat Client (Primary)

Best for complex workflows, high-density UI, and power users.

- Technology: Java (JavaFX) with MVVM/MVP.
- Access: Backend REST APIs (preferred), or local integration for dev-only tooling.
- Strengths: rich UI controls, high performance, keyboard-heavy workflows.

### 2) Web Client (Secondary)

Best for simple CRUD and admin workflows.

- Technology: SPA (React/Angular/Vue) with a thin UI layer.
- Access: Backend REST APIs only.
- Strengths: low friction access, easier rollouts, simple task focus.

### 3) Mobile Client (Future)

Best for lightweight read-heavy workflows and notifications.

- Technology: React Native or Flutter.
- Access: Backend REST APIs only.

### 4) Customer Portal (Web + Mobile)

Separate end-user portal for policyholders and subscribers.

- Channels: Web portal + mobile app (iOS/Android).
- Access: Backend REST APIs only, with dedicated auth flows.
- Scope: self-service documents, invoices, notifications, and service requests.

## Backend Alignment

- **Single API surface** shared across all clients.
- **Domain logic stays in backend**; clients are thin.
- **Validation happens twice**: client UX validation + backend authoritative validation.
- **i18n** is backend-driven for error messages and data labels.
- **Security** enforced in the backend, not in the client.

## Decision Summary

- Desktop client is the primary UX for complex ERP tasks.
- Web client is scoped to masterdata and simple management.
- API is the shared contract; avoid desktop-only backdoors.
- Customer portal is a separate end-user channel with its own UX and security model.

## Related Documents

- `docs/development/frontend-web-client.md`
- `docs/development/frontend-desktop-client.md`
- `docs/development/frontend-deployment.md`
- `docs/development/customer-portal.md`

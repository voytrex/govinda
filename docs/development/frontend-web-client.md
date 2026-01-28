# Web Client (Thin UI) Guidelines

This document defines scope, constraints, and patterns for the web client.

## Scope

Initial scope targets simple masterdata management and admin tasks:

- Person management (create, edit, deactivate, view)
- Address management
- Simple lookup tables and reference data
- Search, filter, and basic export

Complex workflows remain in the desktop client.

## Architecture

- SPA that consumes backend REST APIs.
- Backend owns validation, business rules, and permissions.
- Client handles UX-only logic: form state, layout, field-level hints.

## Data Access Patterns

- Use a single API client module for all REST calls.
- Centralized error handling to map backend error codes to user messages.
- Use optimistic updates only for non-critical flows.

## Error Handling

- Display localized backend error messages when present.
- For validation errors, show field-level messages derived from backend error codes.
- Unknown errors show a generic localized message with a support reference.

## Security

- Use OAuth2/OIDC with access tokens.
- Store tokens in memory or secure storage; avoid localStorage where possible.
- Enforce authorization in backend; UI is only a convenience filter.

## i18n

- UI strings in the web client must be localized.
- Backend error messages are localized by API locale negotiation.
- Respect the user's language preference from profile settings.

## UX Principles

- Keep forms minimal and fast.
- Keyboard-first navigation for data entry.
- Provide bulk editing only when safe and explicit.

## Testing

- Unit tests for forms and validation mapping.
- Integration tests against a mocked API.
- A small set of end-to-end flows for person/address CRUD.

## Out of Scope

- Complex workflow orchestration.
- Heavy data visualization.
- Offline mode.

## Dependencies on Backend

- Stable REST endpoints for masterdata.
- Pagination and filtering parameters standardized.
- Audit metadata returned for admin views (createdBy, updatedAt).

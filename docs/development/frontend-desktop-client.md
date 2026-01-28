# Desktop (Fat Client) Guidelines

This document defines the desktop client approach for Govinda ERP.

## Role in the Product

The desktop client is the primary UI for complex and high-volume workflows:

- Policy/contract lifecycle and mutations
- Premium and billing workflows
- Case handling with complex validation
- Power-user screens with dense data grids

## Architecture

- JavaFX client with MVVM or MVP for clear separation.
- API-first design: all business logic through backend REST APIs.
- Optional local cache for performance, never as a source of truth.

## UX Principles

- Keyboard-centric data entry.
- High-density layouts with advanced filtering.
- Configurable views and saved filters for power users.

## Networking

- Use a resilient API client with retries and circuit breakers.
- Support offline read-only mode if required, but avoid write-offline until needed.

## Error Handling

- Display backend error messages with localized text.
- Provide a diagnostics view for support (correlation ID, timestamp, module).

## Security

- OAuth2/OIDC with refresh tokens.
- Device registration for enterprise deployments if required.

## i18n

- Desktop UI strings localized.
- Backend error messages localized by locale negotiation.

## Testing

- Unit tests for view models and client services.
- API contract tests to ensure desktop compatibility.
- Manual regression set for critical workflows.

## Compatibility

- Target Java 21+ runtime for all installations.
- Provide OS packages for macOS, Windows, and Linux.

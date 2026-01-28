# Frontend Deployment and Updates

This document covers deployment strategies for the desktop and web clients, with a focus on releases and auto-update
mechanisms for the fat client.

## Web Client Deployment

### Hosting

- Serve the SPA from a CDN or behind a reverse proxy in front of the backend.
- Version the static assets with content hashes.
- Keep the API and web client deployments loosely coupled to reduce release risk.

### Release Strategy

- Standard CI/CD pipeline builds and deploys static assets.
- Use blue/green or canary deployment where possible.
- Rollbacks are a CDN/cache invalidation plus artifact rollback.

### Cache Management

- Use cache-busting file names for JS/CSS bundles.
- Use a short TTL for HTML entry point so new releases are discovered quickly.

## Desktop Client Deployment

### Packaging

- Use `jpackage` (Java 21) to generate native installers:
  - macOS: `.dmg` or `.pkg`
  - Windows: `.msi` or `.exe`
  - Linux: `.deb` / `.rpm`
- Embed a minimal JRE to avoid system JDK dependency mismatches.

### Distribution Models

Choose one of the following depending on your customer environment:

1. **Managed distribution (recommended for SaaS)**
   - Download from a secure portal.
   - Auto-update enabled by default.
   - Centralized version control and telemetry.

2. **Enterprise offline distribution**
   - Installers distributed via internal IT.
   - Auto-updates may be disabled.
   - Signed artifacts and version catalog for IT audits.

### Auto-Update Strategies

#### Option A: Built-in updater (recommended)

- Client checks a version manifest hosted by the backend.
- If a newer version is available:
  - Download update in background.
  - Prompt user or auto-restart based on policy.
- The updater verifies integrity and signature before applying.

**Pros:** Full control, works in restricted environments.  
**Cons:** Requires building update infrastructure and signatures.

#### Option B: OS-native update channels

- macOS: Sparkle or enterprise MDM-based updates.
- Windows: MSIX with Windows Update for Business, or SCCM.
- Linux: OS package repos (APT/YUM) with standard update flows.

**Pros:** Familiar to IT departments.  
**Cons:** More platform-specific setup and maintenance.

### Update Policy

- Provide **configurable update channels**: stable, preview, emergency hotfix.
- Allow **forced updates** for security patches.
- Allow **deferred updates** with a deadline for regulated environments.

### Signing and Integrity

- Sign all installers and update packages.
- Verify signatures before installation.
- Keep a checksum manifest (SHA-256) for auditing.

### Rollback

- Keep the last known good version locally.
- If the update fails health checks, auto-rollback.

### Versioning

- Semantic versioning (MAJOR.MINOR.PATCH).
- Increment MAJOR only for incompatible changes.
- Client must check API compatibility against backend version.

## Release Coordination

- **API compatibility** must be maintained across client versions.
- For breaking API changes:
  - Introduce versioned endpoints or additive changes first.
  - Deprecate with a clear timeline.
  - Update clients before removal.

## Observability

- Add client telemetry for:
  - App version distribution
  - Update success/failure rates
  - API error rates
- Provide a privacy-friendly opt-in policy.

## Security Notes

- All downloads must use TLS.
- Validate update payloads with signatures.
- Avoid running update installers with elevated privileges unless required.

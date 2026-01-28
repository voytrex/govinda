# Customer Portal Strategy and Specification

The customer portal is a separate end-user channel for policyholders, subscribers, and insured persons. It provides
self-service access to documents, payments, notifications, and service requests, with an optional mobile app for
on-the-go use (including scanning invoices and barcodes).

## Goals

- Enable self-service for documents and invoices.
- Provide secure, easy payment of open invoices.
- Reduce call center load with clear request flows.
- Support mobile-first usage with a dedicated app.

## Scope (Phase 1)

### Core Features

- Document access (contracts, policies, invoices, letters).
- Invoice list with status and payment actions.
- Notifications for new documents and due invoices.
- Service requests (e.g., change address, family changes).

### Mobile Enhancements

- Scan invoice barcodes (Swiss QR-bill / ESR as applicable).
- Upload reimbursement receipts ("Rückforderungsbeleg").
- Push notifications (payment reminders, request updates).

## Architecture Extension

### Channels

- Web Portal (SPA)
- Mobile App (iOS/Android)

### Backend Components

- **Portal API**: dedicated endpoints for end-user flows.
- **Document Service**: secure access to stored PDFs and metadata.
- **Payment Service**: open invoice listing + payment initiation.
- **Notification Service**: email/push/SMS dispatch.
- **Case Service**: request/case creation and status tracking.
- **Upload Service**: secure file ingestion and virus scanning.

### Security Model

- OAuth2/OIDC with customer identity provider.
- Strong MFA for sensitive actions (payments, address changes).
- Data isolation by tenant and customer identity.
- Audit logging for all portal actions.

## Functional Specification

### Documents

- List documents with filters (type, date, status).
- Download PDF.
- Optional per-document notifications.

### Invoices & Payments

- Show open invoices with due dates and amounts.
- Pay invoice via supported PSP (e.g., Twint, card, e-banking).
- Payment status updates via webhook reconciliation.

### Notifications

- Channels: email + push; SMS optional.
- User preferences for notification types.
- Event types: new document, invoice due, case update.

### Service Requests / Cases

- Create case with category (moving, contact data, benefit question).
- Attach documents or photos.
- Track status with timestamps and messages.

### Uploads & Scanning

- Upload images or PDFs.
- Mobile barcode scan to prefill invoice data.
- Automatic OCR for reimbursement receipts when available.

## API Endpoints (Draft)

All endpoints are scoped to the authenticated end user and tenant.

### Documents

- `GET /api/portal/documents`
  - Query: `type`, `from`, `to`, `status`, `page`, `size`, `sort`
- `GET /api/portal/documents/{documentId}`
- `GET /api/portal/documents/{documentId}/download`

### Invoices & Payments

- `GET /api/portal/invoices`
  - Query: `status` (open, paid, overdue), `from`, `to`, `page`, `size`
- `GET /api/portal/invoices/{invoiceId}`
- `POST /api/portal/invoices/{invoiceId}/payment-intent`
- `POST /api/portal/payments/webhooks/psp`
  - Used by PSP to confirm payments

### Notifications

- `GET /api/portal/notifications`
- `PATCH /api/portal/notifications/preferences`

### Service Requests / Cases

- `GET /api/portal/cases`
- `POST /api/portal/cases`
- `GET /api/portal/cases/{caseId}`
- `POST /api/portal/cases/{caseId}/messages`
- `POST /api/portal/cases/{caseId}/attachments`

### Uploads & Scanning

- `POST /api/portal/uploads`
  - Returns pre-signed upload URL and upload token
- `POST /api/portal/uploads/{uploadId}/complete`
- `POST /api/portal/scan/barcode`
  - Accepts raw scan payload and returns parsed fields

### Profile & Preferences

- `GET /api/portal/profile`
- `PATCH /api/portal/profile`

## Data Contracts (Draft)

These are indicative contracts to align web and mobile clients. Final DTOs belong in the API layer.

### DocumentSummary

```json
{
  "id": "doc_123",
  "type": "CONTRACT",
  "title": "Policy Contract 2026",
  "status": "ACTIVE",
  "createdAt": "2026-01-27T09:12:00Z",
  "availableActions": ["DOWNLOAD"]
}
```

### InvoiceSummary

```json
{
  "id": "inv_456",
  "status": "OPEN",
  "currency": "CHF",
  "amount": "124.50",
  "dueDate": "2026-02-15",
  "reference": "RF18539007547034",
  "qrBillPayload": "SPC ...",
  "availableActions": ["PAY", "DOWNLOAD"]
}
```

### PaymentIntentRequest / Response

```json
{
  "method": "TWINT",
  "returnUrl": "govinda://payment/return",
  "cancelUrl": "govinda://payment/cancel"
}
```

```json
{
  "paymentId": "pay_789",
  "status": "PENDING",
  "provider": "TWINT",
  "redirectUrl": "https://psp.example/checkout/..."
}
```

### CaseCreateRequest

```json
{
  "type": "ADDRESS_CHANGE",
  "subject": "Moving to new address",
  "description": "New address from 2026-03-01",
  "attachments": ["upl_123"]
}
```

### CaseSummary

```json
{
  "id": "case_101",
  "type": "ADDRESS_CHANGE",
  "status": "IN_PROGRESS",
  "createdAt": "2026-01-27T10:10:00Z",
  "lastUpdatedAt": "2026-01-27T12:05:00Z"
}
```

### NotificationPreference

```json
{
  "email": true,
  "push": true,
  "sms": false,
  "topics": {
    "DOCUMENT": true,
    "INVOICE_DUE": true,
    "CASE_UPDATE": true
  }
}
```

### UploadInitResponse

```json
{
  "uploadId": "upl_123",
  "uploadUrl": "https://storage.example/upload/...",
  "expiresAt": "2026-01-27T10:15:00Z"
}
```

## UX Principles

- Simple, guided flows with minimal steps.
- Mobile-first layout; responsive web.
- Clear support handoff when an issue cannot be solved in-app.

## Data & Integration Considerations

- Document storage must support retention and audit needs.
- Payments require reconciliation and idempotent handling.
- Case creation integrates with existing ERP workflows.
- Notification events should be derived from domain events.

## Implementation Plan (Draft)

### Phase 0: Discovery

- Define portal personas and top customer tasks.
- Decide identity provider and payment provider.
- Define portal-specific API surface.

### Phase 1: MVP

- Document list and download.
- Open invoices list.
- Basic case creation (moving, address change).
- Email notifications.

### Phase 2: Payments & Mobile App

- In-app payments for open invoices.
- Push notifications.
- Mobile barcode scan and upload.

### Phase 3: Enhancements

- OCR for receipts.
- Expanded case types and messaging.
- In-app claims status tracking.

## Non-Functional Requirements

- Availability: 99.9% for portal APIs.
- Performance: document list < 2 seconds for typical users.
- Security: MFA, rate limiting, and anomaly detection.
- Compliance: GDPR/Swiss data protection requirements.

## Open Decisions

- Identity provider selection.
- Payment service provider selection.
- OCR vendor vs in-house.
- Document storage location (internal vs external).

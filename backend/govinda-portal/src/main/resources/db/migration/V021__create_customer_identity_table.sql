CREATE TABLE customer_identity (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    person_id UUID NOT NULL,
    subject VARCHAR(200) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX ux_customer_identity_tenant_subject ON customer_identity (tenant_id, subject);
CREATE INDEX idx_customer_identity_tenant_person ON customer_identity (tenant_id, person_id);

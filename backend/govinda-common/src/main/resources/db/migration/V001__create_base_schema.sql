/*
 * Govinda ERP - Base Schema Migration
 * Version: V001
 * Description: Creates base tables (tenant, audit_log, reference data)
 */

-- ═══════════════════════════════════════════════════════════════
-- EXTENSIONS
-- ═══════════════════════════════════════════════════════════════
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ═══════════════════════════════════════════════════════════════
-- TENANT (Multi-tenancy)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE tenant (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code            VARCHAR(20) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    settings        JSONB,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tenant_code ON tenant(code);

COMMENT ON TABLE tenant IS 'Multi-tenant support - each insurance company is a tenant';

-- ═══════════════════════════════════════════════════════════════
-- AUDIT LOG (Generic audit trail)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE audit_log (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),

    -- What changed
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       UUID NOT NULL,

    -- Change details
    action          VARCHAR(20) NOT NULL,  -- CREATE, UPDATE, DELETE
    changed_fields  JSONB,

    -- Who and when
    changed_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    changed_by      UUID,
    user_name       VARCHAR(200),

    -- Context
    ip_address      INET,
    user_agent      VARCHAR(500),
    correlation_id  UUID,

    -- Full snapshots (optional)
    before_state    JSONB,
    after_state     JSONB
);

CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id, changed_at DESC);
CREATE INDEX idx_audit_time ON audit_log(tenant_id, changed_at DESC);
CREATE INDEX idx_audit_user ON audit_log(tenant_id, changed_by, changed_at DESC);

COMMENT ON TABLE audit_log IS 'Generic audit trail for all entity changes';

-- ═══════════════════════════════════════════════════════════════
-- CANTON (Swiss Cantons)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE canton (
    code            VARCHAR(2) PRIMARY KEY,
    name_de         VARCHAR(100) NOT NULL,
    name_fr         VARCHAR(100) NOT NULL,
    name_it         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100) NOT NULL
);

COMMENT ON TABLE canton IS 'Swiss cantons reference data';

-- ═══════════════════════════════════════════════════════════════
-- PREMIUM REGION (BAG premium regions)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE premium_region (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    canton_code     VARCHAR(2) NOT NULL REFERENCES canton(code),
    region_number   INTEGER NOT NULL,  -- 1, 2, or 3
    name_de         VARCHAR(200) NOT NULL,
    name_fr         VARCHAR(200),

    CONSTRAINT uk_region UNIQUE (canton_code, region_number),
    CONSTRAINT chk_region_number CHECK (region_number BETWEEN 1 AND 3)
);

CREATE INDEX idx_premium_region_canton ON premium_region(canton_code);

COMMENT ON TABLE premium_region IS 'BAG premium regions (Prämienregionen) - 1, 2, or 3 per canton';

-- ═══════════════════════════════════════════════════════════════
-- POSTAL CODE (Swiss PLZ with region mapping)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE postal_code (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    plz             VARCHAR(10) NOT NULL,
    city            VARCHAR(200) NOT NULL,
    canton_code     VARCHAR(2) NOT NULL REFERENCES canton(code),
    region_id       UUID NOT NULL REFERENCES premium_region(id),

    CONSTRAINT uk_plz_city UNIQUE (plz, city)
);

CREATE INDEX idx_postal_code_plz ON postal_code(plz);
CREATE INDEX idx_postal_code_canton ON postal_code(canton_code);
CREATE INDEX idx_postal_code_region ON postal_code(region_id);

COMMENT ON TABLE postal_code IS 'Swiss postal codes with BAG region mapping';

-- ═══════════════════════════════════════════════════════════════
-- Insert Default Tenant
-- ═══════════════════════════════════════════════════════════════
INSERT INTO tenant (id, code, name, status) VALUES
    ('00000000-0000-0000-0000-000000000001', 'DEFAULT', 'Default Tenant', 'ACTIVE');

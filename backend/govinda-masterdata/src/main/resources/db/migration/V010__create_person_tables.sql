/*
 * Govinda ERP - Person Tables Migration
 * Version: V010
 * Description: Creates person, address, household tables with history support
 */

-- ═══════════════════════════════════════════════════════════════
-- PERSON
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE person (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),

    -- Identity
    ahv_nr              VARCHAR(16) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    first_name          VARCHAR(100) NOT NULL,
    date_of_birth       DATE NOT NULL,
    gender              VARCHAR(10) NOT NULL,

    -- Personal details
    marital_status      VARCHAR(20),
    nationality         VARCHAR(3) DEFAULT 'CHE',
    preferred_language  VARCHAR(2) DEFAULT 'DE',

    -- Status
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Metadata
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_person_ahv UNIQUE (tenant_id, ahv_nr),
    CONSTRAINT chk_person_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_person_status CHECK (status IN ('ACTIVE', 'DECEASED', 'EMIGRATED'))
);

CREATE INDEX idx_person_tenant ON person(tenant_id);
CREATE INDEX idx_person_name ON person(tenant_id, LOWER(last_name), LOWER(first_name));
CREATE INDEX idx_person_dob ON person(tenant_id, date_of_birth);

COMMENT ON TABLE person IS 'Insured persons (Versicherte)';

-- ═══════════════════════════════════════════════════════════════
-- PERSON HISTORY (Bitemporal)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE person_history (
    history_id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id           UUID NOT NULL REFERENCES person(id) ON DELETE CASCADE,

    -- Versioned fields
    last_name           VARCHAR(100) NOT NULL,
    first_name          VARCHAR(100) NOT NULL,
    marital_status      VARCHAR(20),

    -- Valid time (business time)
    valid_from          DATE NOT NULL,
    valid_to            DATE,

    -- Transaction time (system time)
    recorded_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    superseded_at       TIMESTAMP WITH TIME ZONE,

    -- Mutation metadata
    mutation_type       VARCHAR(20) NOT NULL,
    mutation_reason     VARCHAR(500),
    changed_by          UUID NOT NULL,

    CONSTRAINT chk_person_history_valid CHECK (valid_to IS NULL OR valid_to >= valid_from),
    CONSTRAINT chk_person_history_mutation CHECK (mutation_type IN ('CREATE', 'UPDATE', 'CORRECTION', 'CANCELLATION'))
);

CREATE INDEX idx_person_history_person ON person_history(person_id);
CREATE INDEX idx_person_history_lookup ON person_history(person_id, valid_from, valid_to, superseded_at);

COMMENT ON TABLE person_history IS 'Bitemporal history of person changes';

-- ═══════════════════════════════════════════════════════════════
-- ADDRESS
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE address (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id           UUID NOT NULL REFERENCES person(id) ON DELETE CASCADE,

    -- Address type
    address_type        VARCHAR(20) NOT NULL,

    -- Address fields
    street              VARCHAR(200) NOT NULL,
    house_number        VARCHAR(20),
    additional_line     VARCHAR(200),
    postal_code         VARCHAR(10) NOT NULL,
    city                VARCHAR(100) NOT NULL,
    canton              VARCHAR(2) NOT NULL REFERENCES canton(code),
    country             VARCHAR(3) NOT NULL DEFAULT 'CHE',

    -- Premium region (resolved from PLZ)
    premium_region_id   UUID REFERENCES premium_region(id),

    -- Validity period
    valid_from          DATE NOT NULL,
    valid_to            DATE,

    -- Transaction time
    recorded_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    superseded_at       TIMESTAMP WITH TIME ZONE,

    -- Metadata
    created_by          UUID,

    CONSTRAINT chk_address_type CHECK (address_type IN ('MAIN', 'CORRESPONDENCE', 'BILLING')),
    CONSTRAINT chk_address_valid CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

CREATE INDEX idx_address_person ON address(person_id);
CREATE INDEX idx_address_current ON address(person_id, address_type) WHERE valid_to IS NULL;
CREATE INDEX idx_address_plz ON address(postal_code);

COMMENT ON TABLE address IS 'Person addresses with temporal validity';

-- ═══════════════════════════════════════════════════════════════
-- HOUSEHOLD
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE household (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),

    name                VARCHAR(200) NOT NULL,

    -- Metadata
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version             BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_household_tenant ON household(tenant_id);

COMMENT ON TABLE household IS 'Household grouping related persons';

-- ═══════════════════════════════════════════════════════════════
-- HOUSEHOLD MEMBER
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE household_member (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    household_id        UUID NOT NULL REFERENCES household(id) ON DELETE CASCADE,
    person_id           UUID NOT NULL REFERENCES person(id),

    role                VARCHAR(20) NOT NULL,

    valid_from          DATE NOT NULL,
    valid_to            DATE,

    CONSTRAINT chk_member_role CHECK (role IN ('PRIMARY', 'PARTNER', 'CHILD')),
    CONSTRAINT chk_member_valid CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

CREATE INDEX idx_household_member_household ON household_member(household_id);
CREATE INDEX idx_household_member_person ON household_member(person_id);
CREATE INDEX idx_household_member_current ON household_member(household_id) WHERE valid_to IS NULL;

COMMENT ON TABLE household_member IS 'Household membership with role and validity';

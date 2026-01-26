/*
 * Govinda ERP - User Authentication & Authorization Schema
 * Version: V005
 * Description: Creates user, role, and permission tables for authentication and authorization
 */

-- ═══════════════════════════════════════════════════════════════
-- ROLE (User roles/permissions)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE role (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_role_code ON role(code);

COMMENT ON TABLE role IS 'User roles with specific permissions';

-- ═══════════════════════════════════════════════════════════════
-- PERMISSION (Fine-grained permissions)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE permission (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code            VARCHAR(100) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    resource        VARCHAR(100) NOT NULL,  -- e.g., 'person', 'contract', 'premium'
    action          VARCHAR(50) NOT NULL,   -- e.g., 'read', 'write', 'delete'
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_permission_code ON permission(code);
CREATE INDEX idx_permission_resource ON permission(resource);

COMMENT ON TABLE permission IS 'Fine-grained permissions for resources and actions';

-- ═══════════════════════════════════════════════════════════════
-- ROLE_PERMISSION (Many-to-many: roles have permissions)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE role_permission (
    role_id         UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permission_role ON role_permission(role_id);
CREATE INDEX idx_role_permission_permission ON role_permission(permission_id);

COMMENT ON TABLE role_permission IS 'Maps roles to their permissions';

-- ═══════════════════════════════════════════════════════════════
-- USER (Application users)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE "user" (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,  -- BCrypt hash
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, LOCKED
    last_login_at   TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_username ON "user"(username);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_status ON "user"(status);

COMMENT ON TABLE "user" IS 'Application users - can access multiple tenants';

-- ═══════════════════════════════════════════════════════════════
-- USER_TENANT (Many-to-many: users can access multiple tenants)
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE user_tenant (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    tenant_id       UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    role_id         UUID NOT NULL REFERENCES role(id),
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,  -- Default tenant for user
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    
    CONSTRAINT uk_user_tenant UNIQUE (user_id, tenant_id)
);

CREATE INDEX idx_user_tenant_user ON user_tenant(user_id);
CREATE INDEX idx_user_tenant_tenant ON user_tenant(tenant_id);
CREATE INDEX idx_user_tenant_role ON user_tenant(role_id);

COMMENT ON TABLE user_tenant IS 'Maps users to tenants with specific roles - supports multi-tenant users';

-- ═══════════════════════════════════════════════════════════════
-- Insert Default Roles
-- ═══════════════════════════════════════════════════════════════
INSERT INTO role (id, code, name, description) VALUES
    ('00000000-0000-0000-0000-000000000010', 'ADMIN', 'Administrator', 'Full system access'),
    ('00000000-0000-0000-0000-000000000011', 'USER', 'User', 'Standard user with read/write access'),
    ('00000000-0000-0000-0000-000000000012', 'READONLY', 'Read Only', 'Read-only access');

-- ═══════════════════════════════════════════════════════════════
-- Insert Default Permissions
-- ═══════════════════════════════════════════════════════════════
INSERT INTO permission (id, code, name, description, resource, action) VALUES
    -- Person permissions
    ('00000000-0000-0000-0000-000000000100', 'person:read', 'Read Persons', 'View person data', 'person', 'read'),
    ('00000000-0000-0000-0000-000000000101', 'person:write', 'Write Persons', 'Create and update persons', 'person', 'write'),
    ('00000000-0000-0000-0000-000000000102', 'person:delete', 'Delete Persons', 'Delete persons', 'person', 'delete'),
    
    -- Contract permissions
    ('00000000-0000-0000-0000-000000000110', 'contract:read', 'Read Contracts', 'View contract data', 'contract', 'read'),
    ('00000000-0000-0000-0000-000000000111', 'contract:write', 'Write Contracts', 'Create and update contracts', 'contract', 'write'),
    
    -- Premium permissions
    ('00000000-0000-0000-0000-000000000120', 'premium:read', 'Read Premiums', 'View premium calculations', 'premium', 'read'),
    ('00000000-0000-0000-0000-000000000121', 'premium:write', 'Write Premiums', 'Calculate and update premiums', 'premium', 'write');

-- ═══════════════════════════════════════════════════════════════
-- Assign Permissions to Roles
-- ═══════════════════════════════════════════════════════════════
-- ADMIN: All permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT 
    '00000000-0000-0000-0000-000000000010'::UUID,
    id
FROM permission;

-- USER: Read and write permissions (no delete)
INSERT INTO role_permission (role_id, permission_id)
SELECT 
    '00000000-0000-0000-0000-000000000011'::UUID,
    id
FROM permission
WHERE action IN ('read', 'write');

-- READONLY: Only read permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT 
    '00000000-0000-0000-0000-000000000012'::UUID,
    id
FROM permission
WHERE action = 'read';

-- ═══════════════════════════════════════════════════════════════
-- Insert Default Admin User (password: admin123)
-- ═══════════════════════════════════════════════════════════════
-- BCrypt hash for "admin123" (10 rounds)
INSERT INTO "user" (id, username, email, password_hash, first_name, last_name, status) VALUES
    ('00000000-0000-0000-0000-000000000020', 'admin', 'admin@govinda.local', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
     'Admin', 'User', 'ACTIVE');

-- Assign admin user to default tenant with ADMIN role
INSERT INTO user_tenant (user_id, tenant_id, role_id, is_default) VALUES
    ('00000000-0000-0000-0000-000000000020'::UUID, 
     '00000000-0000-0000-0000-000000000001'::UUID,
     '00000000-0000-0000-0000-000000000010'::UUID,
     TRUE);

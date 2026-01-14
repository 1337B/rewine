-- =============================================================================
-- V1__initial_schema.sql - Initial database schema for authentication
-- =============================================================================

-- -----------------------------------------------------------------------------
-- EXTENSIONS
-- -----------------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -----------------------------------------------------------------------------
-- ROLES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE roles (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(50)     NOT NULL UNIQUE,
    description     VARCHAR(255),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for role name lookups
CREATE INDEX idx_roles_name ON roles(name);

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_USER', 'Standard user role'),
    ('ROLE_ADMIN', 'Administrator role'),
    ('ROLE_MODERATOR', 'Content moderator role'),
    ('ROLE_PARTNER', 'Business partner role');

-- -----------------------------------------------------------------------------
-- USERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(50)     NOT NULL UNIQUE,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    name            VARCHAR(100),
    avatar_url      VARCHAR(500),
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
    email_verified  BOOLEAN         NOT NULL DEFAULT FALSE,
    locked          BOOLEAN         NOT NULL DEFAULT FALSE,
    lock_reason     VARCHAR(255),
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_users_created_at ON users(created_at);

-- -----------------------------------------------------------------------------
-- USER_ROLES TABLE (Many-to-Many relationship)
-- -----------------------------------------------------------------------------
CREATE TABLE user_roles (
    user_id         UUID            NOT NULL,
    role_id         BIGINT          NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

-- Indexes for user_roles
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- -----------------------------------------------------------------------------
-- REFRESH_TOKENS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE refresh_tokens (
    id                      BIGSERIAL       PRIMARY KEY,
    user_id                 UUID            NOT NULL,
    token_hash              VARCHAR(255)    NOT NULL UNIQUE,
    device_info             VARCHAR(500),
    ip_address              VARCHAR(45),
    expires_at              TIMESTAMPTZ     NOT NULL,
    revoked_at              TIMESTAMPTZ,
    revoked_reason          VARCHAR(100),
    replaced_by_token_hash  VARCHAR(255),
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_refresh_tokens_replaced_by
        FOREIGN KEY (replaced_by_token_hash)
        REFERENCES refresh_tokens(token_hash)
        ON DELETE SET NULL
);

-- Indexes for refresh_tokens
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_revoked_at ON refresh_tokens(revoked_at);

-- Partial index for active (non-revoked) tokens
CREATE INDEX idx_refresh_tokens_active
    ON refresh_tokens(user_id, expires_at)
    WHERE revoked_at IS NULL;

-- -----------------------------------------------------------------------------
-- FUNCTION: Update updated_at timestamp
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- -----------------------------------------------------------------------------
-- TRIGGERS: Auto-update updated_at
-- -----------------------------------------------------------------------------
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_roles_updated_at
    BEFORE UPDATE ON roles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


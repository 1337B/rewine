-- =============================================================================
-- H2 Initial Data for Development/Testing
-- This file is loaded after Hibernate creates the schema
-- =============================================================================

-- Insert default roles if they don't exist
INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'ROLE_USER', 'Standard user role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'ROLE_ADMIN', 'Administrator role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'ROLE_MODERATOR', 'Content moderator role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_MODERATOR');

INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'ROLE_PARTNER', 'Business partner role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_PARTNER');


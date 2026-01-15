-- =============================================================================
-- R__seed_local_data.sql - Repeatable seed data for local development
-- =============================================================================
-- This migration is IDEMPOTENT and can be re-run safely.
-- It only runs in local profile via Flyway repeatable migrations.
--
-- Seeded Users (all with password: Rewine123!):
--   - admin@rewine.local (ROLE_ADMIN)
--   - partner@rewine.local (ROLE_PARTNER)
--   - moderator@rewine.local (ROLE_MODERATOR)
--   - user@rewine.local (ROLE_USER)
-- =============================================================================

-- =============================================================================
-- USERS SEED DATA
-- =============================================================================
-- BCrypt hash for password "Rewine123!" (cost factor 10)
-- Generated using: BCryptPasswordEncoder().encode("Rewine123!")

-- Admin user
INSERT INTO users (id, username, email, password_hash, name, enabled, email_verified, locked, created_at, updated_at)
SELECT
    'a0000000-0000-0000-0000-000000000001',
    'admin',
    'admin@rewine.local',
    '$2a$10$chhC1A5pg6xOwTP89Ksu.OcRGW0YytB7SncC28UmvnXjl8UrdHSa2',
    'System Administrator',
    TRUE,
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- Partner user
INSERT INTO users (id, username, email, password_hash, name, enabled, email_verified, locked, created_at, updated_at)
SELECT
    'a0000000-0000-0000-0000-000000000002',
    'partner',
    'partner@rewine.local',
    '$2a$10$chhC1A5pg6xOwTP89Ksu.OcRGW0YytB7SncC28UmvnXjl8UrdHSa2',
    'Partner Business',
    TRUE,
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Moderator user
INSERT INTO users (id, username, email, password_hash, name, enabled, email_verified, locked, created_at, updated_at)
SELECT
    'a0000000-0000-0000-0000-000000000003',
    'moderator',
    'moderator@rewine.local',
    '$2a$10$chhC1A5pg6xOwTP89Ksu.OcRGW0YytB7SncC28UmvnXjl8UrdHSa2',
    'Content Moderator',
    TRUE,
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'moderator@rewine.local');

-- Regular user
INSERT INTO users (id, username, email, password_hash, name, enabled, email_verified, locked, created_at, updated_at)
SELECT
    'a0000000-0000-0000-0000-000000000004',
    'user',
    'user@rewine.local',
    '$2a$10$chhC1A5pg6xOwTP89Ksu.OcRGW0YytB7SncC28UmvnXjl8UrdHSa2',
    'Regular User',
    TRUE,
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@rewine.local');

-- =============================================================================
-- ASSIGN ROLES TO SEEDED USERS
-- =============================================================================

-- Admin gets ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'admin@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
  );

-- Admin also gets ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'admin@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
  );

-- Partner gets ROLE_PARTNER
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_PARTNER')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'partner@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_PARTNER')
  );

-- Partner also gets ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'partner@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
  );

-- Moderator gets ROLE_MODERATOR
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'moderator@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_MODERATOR')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'moderator@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'moderator@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_MODERATOR')
  );

-- Moderator also gets ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'moderator@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'moderator@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'moderator@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
  );

-- Regular user gets ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE email = 'user@rewine.local'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'user@rewine.local')
  AND NOT EXISTS (
      SELECT 1 FROM user_roles
      WHERE user_id = (SELECT id FROM users WHERE email = 'user@rewine.local')
        AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
  );

-- =============================================================================
-- ADDITIONAL WINERIES (to reach 5 total - already have 5 from V2)
-- =============================================================================
-- Wineries already seeded in V2:
-- 1. Bodega Catena Zapata
-- 2. Bodega Norton
-- 3. Trapiche
-- 4. Zuccardi
-- 5. Luigi Bosca

-- =============================================================================
-- ADDITIONAL WINES (to reach 20 total)
-- =============================================================================
-- Already have 8 wines from V2, adding 12 more

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000001',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Catena Cabernet Sauvignon',
    2020,
    'RED',
    'Full-bodied',
    '["Cabernet Sauvignon"]',
    'Cabernet Sauvignon de alta gama con notas de cassis y cedro.',
    'Premium Cabernet Sauvignon with cassis and cedar notes.',
    14.5,
    35.00,
    42.00,
    4.4,
    95,
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000001');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000002',
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Norton Barrel Select Malbec',
    2019,
    'RED',
    'Full-bodied',
    '["Malbec"]',
    'Malbec seleccionado con 12 meses en barricas de roble francés.',
    'Selected Malbec with 12 months in French oak barrels.',
    14.8,
    40.00,
    48.00,
    4.6,
    78,
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000002');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000003',
    'c3d4e5f6-a7b8-9012-cdef-123456789012',
    'Trapiche Gran Medalla Malbec',
    2018,
    'RED',
    'Full-bodied',
    '["Malbec"]',
    'El mejor Malbec de Trapiche, gran estructura y complejidad.',
    'Trapiche flagship Malbec, great structure and complexity.',
    15.0,
    65.00,
    75.00,
    4.8,
    112,
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000003');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000004',
    'd4e5f6a7-b8c9-0123-def0-234567890123',
    'Zuccardi Q Malbec',
    2021,
    'RED',
    'Full-bodied',
    '["Malbec"]',
    'Malbec icónico de la línea Q, pura expresión del terroir.',
    'Iconic Q line Malbec, pure terroir expression.',
    14.5,
    28.00,
    35.00,
    4.5,
    156,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000004');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000005',
    'e5f6a7b8-c9d0-1234-ef01-345678901234',
    'Luigi Bosca Finca La Linda Malbec',
    2022,
    'RED',
    'Medium-bodied',
    '["Malbec"]',
    'Malbec joven y frutal, perfecto para el día a día.',
    'Young and fruity Malbec, perfect for everyday.',
    13.5,
    12.00,
    15.00,
    4.0,
    234,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000005');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000006',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Catena Chardonnay',
    2022,
    'WHITE',
    'Full-bodied',
    '["Chardonnay"]',
    'Chardonnay de altura con crianza en roble.',
    'High-altitude Chardonnay with oak aging.',
    13.5,
    22.00,
    28.00,
    4.3,
    89,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000006');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000007',
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Norton Colección Torrontés',
    2023,
    'WHITE',
    'Light-bodied',
    '["Torrontés"]',
    'Torrontés aromático con notas florales intensas.',
    'Aromatic Torrontés with intense floral notes.',
    12.5,
    15.00,
    18.00,
    4.1,
    67,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000007');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000008',
    'c3d4e5f6-a7b8-9012-cdef-123456789012',
    'Trapiche Fond de Cave Sauvignon Blanc',
    2022,
    'WHITE',
    'Light-bodied',
    '["Sauvignon Blanc"]',
    'Sauvignon Blanc fresco y mineral.',
    'Fresh and mineral Sauvignon Blanc.',
    13.0,
    18.00,
    22.00,
    4.2,
    98,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000008');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000009',
    'd4e5f6a7-b8c9-0123-def0-234567890123',
    'Zuccardi Brazos de los Andes Rosado',
    2023,
    'ROSE',
    'Light-bodied',
    '["Malbec", "Cabernet Franc"]',
    'Rosado fresco con notas de frutos rojos.',
    'Fresh rosé with red fruit notes.',
    12.5,
    14.00,
    17.00,
    4.0,
    45,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000009');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000010',
    'e5f6a7b8-c9d0-1234-ef01-345678901234',
    'Luigi Bosca De Sangre Blend',
    2019,
    'RED',
    'Full-bodied',
    '["Malbec", "Cabernet Sauvignon", "Tannat"]',
    'Blend premium con gran potencial de guarda.',
    'Premium blend with great aging potential.',
    14.5,
    50.00,
    60.00,
    4.7,
    67,
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000010');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000011',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Catena Alta Historic Rows Malbec',
    2018,
    'RED',
    'Full-bodied',
    '["Malbec"]',
    'De las hileras históricas de viñedos centenarios.',
    'From historic rows of century-old vineyards.',
    14.8,
    85.00,
    95.00,
    4.9,
    45,
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000011');

INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at)
SELECT
    'aaaa0001-0001-0001-0001-000000000012',
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Norton Perdriel',
    2018,
    'RED',
    'Full-bodied',
    '["Malbec"]',
    'Single vineyard Malbec de la finca Perdriel.',
    'Single vineyard Malbec from Perdriel estate.',
    14.5,
    55.00,
    65.00,
    4.6,
    56,
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wines WHERE id = 'aaaa0001-0001-0001-0001-000000000012');

-- =============================================================================
-- WINE ROUTES (3 routes with route_wineries mapping)
-- =============================================================================

-- Route 1: Ruta del Malbec - Luján de Cuyo (using admin as creator)
INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by, created_at, updated_at)
SELECT
    'bbbb0001-0001-0001-0001-000000000001',
    'Camino del Vino - Luján de Cuyo',
    'Recorrido por las bodegas más tradicionales de Luján de Cuyo, cuna del Malbec argentino. Incluye degustaciones premium y almuerzo en viñedos.',
    'Argentina',
    'Mendoza',
    'Luján de Cuyo',
    360,
    1,
    'easy',
    'active',
    '["RED", "ROSE"]',
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000001')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- Route 2: Valle de Uco Experience
INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by, created_at, updated_at)
SELECT
    'bbbb0001-0001-0001-0001-000000000002',
    'Valle de Uco Experience',
    'Explora los viñedos de altura del Valle de Uco, donde se producen algunos de los vinos más premiados del mundo. Tour de 2 días con alojamiento.',
    'Argentina',
    'Mendoza',
    'Valle de Uco',
    720,
    2,
    'moderate',
    'active',
    '["RED", "WHITE", "SPARKLING"]',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000002')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Route 3: Gran Tour Mendocino
INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by, created_at, updated_at)
SELECT
    'bbbb0001-0001-0001-0001-000000000003',
    'Gran Tour Mendocino',
    'El tour más completo de Mendoza: Luján de Cuyo, Maipú y Valle de Uco. 3 días de inmersión total en la cultura del vino argentino.',
    'Argentina',
    'Mendoza',
    NULL,
    1080,
    3,
    'challenging',
    'active',
    '["RED", "WHITE", "ROSE", "SPARKLING"]',
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- =============================================================================
-- ROUTE_WINERIES MAPPINGS
-- =============================================================================

-- Route 1: Camino del Vino - Luján de Cuyo -> Catena Zapata, Norton, Luigi Bosca
INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000001')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000001' AND winery_id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000001', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 2, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000001')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'b2c3d4e5-f6a7-8901-bcde-f12345678901')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000001' AND winery_id = 'b2c3d4e5-f6a7-8901-bcde-f12345678901');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000001', 'e5f6a7b8-c9d0-1234-ef01-345678901234', 3, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000001')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'e5f6a7b8-c9d0-1234-ef01-345678901234')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000001' AND winery_id = 'e5f6a7b8-c9d0-1234-ef01-345678901234');

-- Route 2: Valle de Uco Experience -> Zuccardi, Trapiche
INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000002', 'd4e5f6a7-b8c9-0123-def0-234567890123', 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000002')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'd4e5f6a7-b8c9-0123-def0-234567890123')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000002' AND winery_id = 'd4e5f6a7-b8c9-0123-def0-234567890123');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000002', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 2, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000002')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'c3d4e5f6-a7b8-9012-cdef-123456789012')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000002' AND winery_id = 'c3d4e5f6-a7b8-9012-cdef-123456789012');

-- Route 3: Gran Tour Mendocino -> All 5 wineries
INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000003', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000003' AND winery_id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000003', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 2, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'b2c3d4e5-f6a7-8901-bcde-f12345678901')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000003' AND winery_id = 'b2c3d4e5-f6a7-8901-bcde-f12345678901');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000003', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 3, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'c3d4e5f6-a7b8-9012-cdef-123456789012')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000003' AND winery_id = 'c3d4e5f6-a7b8-9012-cdef-123456789012');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000003', 'd4e5f6a7-b8c9-0123-def0-234567890123', 4, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'd4e5f6a7-b8c9-0123-def0-234567890123')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000003' AND winery_id = 'd4e5f6a7-b8c9-0123-def0-234567890123');

INSERT INTO route_wineries (route_id, winery_id, stop_order, created_at)
SELECT 'bbbb0001-0001-0001-0001-000000000003', 'e5f6a7b8-c9d0-1234-ef01-345678901234', 5, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM wine_routes WHERE id = 'bbbb0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM wineries WHERE id = 'e5f6a7b8-c9d0-1234-ef01-345678901234')
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = 'bbbb0001-0001-0001-0001-000000000003' AND winery_id = 'e5f6a7b8-c9d0-1234-ef01-345678901234');

-- =============================================================================
-- EVENTS (10 events around Mendoza, Argentina - lat: -32.8908, lng: -68.8272)
-- =============================================================================

-- Event 1: Fiesta de la Vendimia
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000001',
    'Fiesta de la Vendimia 2026',
    'La celebración más grande del vino argentino. Música, degustaciones, y la coronación de la Reina de la Vendimia.',
    'festival',
    '2026-03-01 18:00:00'::TIMESTAMPTZ,
    '2026-03-08 23:59:00'::TIMESTAMPTZ,
    'Teatro Griego Frank Romero Day',
    'San Martín s/n',
    'Mendoza',
    'Mendoza',
    -32.8833,
    -68.8500,
    0.00,
    30000,
    0,
    'active',
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000001')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- Event 2: Cata Premium Catena Zapata
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000002',
    'Cata Premium Catena Zapata',
    'Degustación exclusiva de los vinos más premiados de Bodega Catena Zapata con el sommelier de la bodega.',
    'tasting',
    '2026-02-15 17:00:00'::TIMESTAMPTZ,
    '2026-02-15 20:00:00'::TIMESTAMPTZ,
    'Bodega Catena Zapata',
    'Cobos s/n',
    'Agrelo',
    'Mendoza',
    -33.0167,
    -68.8833,
    150.00,
    30,
    12,
    'active',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000002')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Event 3: Maridaje y Cocina Regional
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000003',
    'Maridaje y Cocina Regional',
    'Aprende a maridar vinos con cocina regional mendocina de la mano de un chef reconocido.',
    'workshop',
    '2026-02-20 12:00:00'::TIMESTAMPTZ,
    '2026-02-20 16:00:00'::TIMESTAMPTZ,
    'Restaurant Francis Mallmann 1884',
    'Belgrano 1188',
    'Godoy Cruz',
    'Mendoza',
    -32.9167,
    -68.8333,
    200.00,
    20,
    8,
    'active',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000003')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Event 4: Sunset Wine Tasting
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000004',
    'Sunset Wine Tasting en Viñedos',
    'Degustación al atardecer con vista a los Andes. Incluye tabla de quesos y embutidos locales.',
    'tasting',
    '2026-02-22 18:00:00'::TIMESTAMPTZ,
    '2026-02-22 21:00:00'::TIMESTAMPTZ,
    'Bodega Zuccardi Valle de Uco',
    'RP 89 km 7.5',
    'Tupungato',
    'Mendoza',
    -33.5667,
    -69.1333,
    80.00,
    50,
    35,
    'active',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000004')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Event 5: Tour en Bicicleta por Viñedos
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000005',
    'Tour en Bicicleta por Viñedos',
    'Recorre los viñedos de Maipú en bicicleta. Incluye degustaciones en 3 bodegas.',
    'tour',
    '2026-02-25 09:00:00'::TIMESTAMPTZ,
    '2026-02-25 14:00:00'::TIMESTAMPTZ,
    'Mr Hugo Bikes & Wine',
    'Urquiza 2232',
    'Maipú',
    'Mendoza',
    -32.9833,
    -68.7833,
    45.00,
    15,
    10,
    'active',
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000005')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- Event 6: Noche de Jazz y Vinos
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000006',
    'Noche de Jazz y Vinos',
    'Una velada mágica con jazz en vivo y los mejores vinos de la región.',
    'concert',
    '2026-03-05 20:00:00'::TIMESTAMPTZ,
    '2026-03-06 01:00:00'::TIMESTAMPTZ,
    'Bodega Ruca Malen',
    'RN 7 km 1059',
    'Luján de Cuyo',
    'Mendoza',
    -33.0333,
    -68.9000,
    120.00,
    100,
    45,
    'active',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000006')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Event 7: Clase de Viticultura
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000007',
    'Introducción a la Viticultura',
    'Aprende los fundamentos de la viticultura de la mano de enólogos profesionales.',
    'workshop',
    '2026-03-10 10:00:00'::TIMESTAMPTZ,
    '2026-03-10 13:00:00'::TIMESTAMPTZ,
    'Bodega Norton',
    'RN 7 km 23.5',
    'Luján de Cuyo',
    'Mendoza',
    -33.0000,
    -68.8667,
    75.00,
    25,
    15,
    'active',
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000007')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- Event 8: Feria del Vino Artesanal
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000008',
    'Feria del Vino Artesanal',
    'Descubre pequeños productores y vinos de autor en esta feria única.',
    'festival',
    '2026-03-15 11:00:00'::TIMESTAMPTZ,
    '2026-03-15 20:00:00'::TIMESTAMPTZ,
    'Plaza Independencia',
    'Espejo esq. Chile',
    'Mendoza',
    'Mendoza',
    -32.8894,
    -68.8458,
    15.00,
    500,
    0,
    'active',
    (SELECT id FROM users WHERE email = 'admin@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000008')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'admin@rewine.local');

-- Event 9: Picnic en los Viñedos
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000009',
    'Picnic Gourmet en los Viñedos',
    'Disfruta de un picnic gourmet entre viñedos con vinos premium.',
    'experience',
    '2026-03-20 12:00:00'::TIMESTAMPTZ,
    '2026-03-20 17:00:00'::TIMESTAMPTZ,
    'Bodega Salentein',
    'RP 89 s/n',
    'Tunuyán',
    'Mendoza',
    -33.6667,
    -69.2000,
    180.00,
    40,
    22,
    'active',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000009')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- Event 10: Cena Maridaje bajo las Estrellas
INSERT INTO events (id, title, description, type, start_date, end_date, location_name, location_address, location_city, location_region, latitude, longitude, price, max_attendees, current_attendees, status, organizer_id, created_at, updated_at)
SELECT
    'cccc0001-0001-0001-0001-000000000010',
    'Cena Maridaje bajo las Estrellas',
    'Una experiencia gastronómica inolvidable: cena de 7 pasos maridada con vinos de autor bajo el cielo estrellado de Mendoza.',
    'dinner',
    '2026-03-25 20:30:00'::TIMESTAMPTZ,
    '2026-03-26 00:30:00'::TIMESTAMPTZ,
    'Casa de Uco Vineyards & Wine Resort',
    'RP 94 km 14.5',
    'San Carlos',
    'Mendoza',
    -33.7500,
    -69.0500,
    350.00,
    60,
    42,
    'active',
    (SELECT id FROM users WHERE email = 'partner@rewine.local'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 'cccc0001-0001-0001-0001-000000000010')
  AND EXISTS (SELECT 1 FROM users WHERE email = 'partner@rewine.local');

-- =============================================================================
-- VERIFICATION QUERIES (for debugging - these don't modify data)
-- =============================================================================
-- SELECT 'Users seeded:' AS info, COUNT(*) AS count FROM users WHERE email LIKE '%@rewine.local';
-- SELECT 'User roles assigned:' AS info, COUNT(*) AS count FROM user_roles;
-- SELECT 'Total wines:' AS info, COUNT(*) AS count FROM wines;
-- SELECT 'Total wineries:' AS info, COUNT(*) AS count FROM wineries;
-- SELECT 'Total wine routes:' AS info, COUNT(*) AS count FROM wine_routes;
-- SELECT 'Route-winery mappings:' AS info, COUNT(*) AS count FROM route_wineries;
-- SELECT 'Total events:' AS info, COUNT(*) AS count FROM events;


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

-- =============================================================================
-- Sample Wineries
-- =============================================================================
INSERT INTO wineries (id, name, country, region, subregion, description, established, created_at, updated_at) VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Bodega Catena Zapata', 'Argentina', 'Mendoza', 'Luján de Cuyo', 'One of the most prestigious wineries in Argentina, known for high-altitude Malbec.', 1902, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Bodega Norton', 'Argentina', 'Mendoza', 'Luján de Cuyo', 'Historic winery producing premium wines since 1895.', 1895, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Trapiche', 'Argentina', 'Mendoza', 'Maipú', 'Leading Argentine winery with wide portfolio.', 1883, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d4e5f6a7-b8c9-0123-def0-234567890123', 'Zuccardi', 'Argentina', 'Mendoza', 'Valle de Uco', 'Family winery focused on terroir-driven wines.', 1963, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('e5f6a7b8-c9d0-1234-ef01-345678901234', 'Luigi Bosca', 'Argentina', 'Mendoza', 'Luján de Cuyo', 'Premium winery with over 100 years of history.', 1901, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =============================================================================
-- Sample Wines
-- =============================================================================
INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured, is_active, created_at, updated_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Catena Malbec', 2021, 'RED', 'Full-bodied', '["Malbec"]', 'Un Malbec intenso de altura con notas de ciruela y violetas.', 'An intense high-altitude Malbec with notes of plum and violets.', 14.5, 25.00, 30.00, 4.5, 125, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Catena Alta Malbec', 2019, 'RED', 'Full-bodied', '["Malbec"]', 'Malbec de alta gama con complejidad excepcional.', 'High-end Malbec with exceptional complexity.', 14.8, 55.00, 65.00, 4.8, 89, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Norton Reserva Malbec', 2020, 'RED', 'Medium-bodied', '["Malbec"]', 'Malbec equilibrado con taninos suaves.', 'Balanced Malbec with soft tannins.', 14.0, 18.00, 22.00, 4.2, 203, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('44444444-4444-4444-4444-444444444444', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Trapiche Medalla Chardonnay', 2022, 'WHITE', 'Full-bodied', '["Chardonnay"]', 'Chardonnay con crianza en roble, notas de vainilla.', 'Oak-aged Chardonnay with vanilla notes.', 13.5, 20.00, 25.00, 4.3, 156, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('55555555-5555-5555-5555-555555555555', 'd4e5f6a7-b8c9-0123-def0-234567890123', 'Zuccardi Valle de Uco', 2020, 'RED', 'Full-bodied', '["Malbec", "Cabernet Sauvignon"]', 'Blend de altura con gran estructura.', 'High-altitude blend with great structure.', 14.2, 35.00, 40.00, 4.6, 112, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('66666666-6666-6666-6666-666666666666', 'e5f6a7b8-c9d0-1234-ef01-345678901234', 'Luigi Bosca Pinot Noir', 2021, 'RED', 'Light-bodied', '["Pinot Noir"]', 'Pinot Noir elegante y aromático.', 'Elegant and aromatic Pinot Noir.', 13.0, 28.00, 35.00, 4.1, 78, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('77777777-7777-7777-7777-777777777777', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Catena Rosé', 2023, 'ROSE', 'Light-bodied', '["Malbec"]', 'Rosado fresco y vibrante.', 'Fresh and vibrant rosé.', 12.5, 15.00, 18.00, 4.0, 45, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('88888888-8888-8888-8888-888888888888', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Trapiche Extra Brut', 2022, 'SPARKLING', 'Dry', '["Chardonnay", "Pinot Noir"]', 'Espumante con finas burbujas.', 'Sparkling wine with fine bubbles.', 12.0, 12.00, 15.00, 4.0, 67, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


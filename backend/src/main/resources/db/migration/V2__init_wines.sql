-- =============================================================================
-- V2__init_wines.sql - Wines and Wineries tables
-- =============================================================================

-- -----------------------------------------------------------------------------
-- WINERIES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE wineries (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255)    NOT NULL,
    country         VARCHAR(100)    NOT NULL,
    region          VARCHAR(100),
    subregion       VARCHAR(100),
    description     TEXT,
    website_url     VARCHAR(500),
    logo_url        VARCHAR(500),
    established     INTEGER,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for wineries
CREATE INDEX idx_wineries_name ON wineries(name);
CREATE INDEX idx_wineries_country ON wineries(country);
CREATE INDEX idx_wineries_region ON wineries(region);
CREATE INDEX idx_wineries_name_lower ON wineries(LOWER(name));

-- -----------------------------------------------------------------------------
-- WINES TABLE (Enhanced)
-- -----------------------------------------------------------------------------
-- Drop existing wines table if it exists from H2 dev mode
DROP TABLE IF EXISTS wines CASCADE;

CREATE TABLE wines (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    winery_id           UUID,
    name                VARCHAR(255)    NOT NULL,
    vintage             INTEGER,
    wine_type           VARCHAR(50)     NOT NULL,
    style               VARCHAR(100),
    grapes              TEXT,
    allergens           TEXT,
    description_es      TEXT,
    description_en      TEXT,
    alcohol_content     DECIMAL(4, 2),
    serving_temp_min    INTEGER,
    serving_temp_max    INTEGER,
    price_min           DECIMAL(10, 2),
    price_max           DECIMAL(10, 2),
    image_url           VARCHAR(500),
    rating_average      DECIMAL(3, 2),
    rating_count        INTEGER         DEFAULT 0,
    is_featured         BOOLEAN         DEFAULT FALSE,
    is_active           BOOLEAN         DEFAULT TRUE,
    created_by          UUID,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_wines_winery
        FOREIGN KEY (winery_id)
        REFERENCES wineries(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_wines_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_wine_type CHECK (wine_type IN ('RED', 'WHITE', 'ROSE', 'SPARKLING', 'DESSERT', 'FORTIFIED', 'ORANGE'))
);

-- Indexes for wines
CREATE INDEX idx_wines_name ON wines(name);
CREATE INDEX idx_wines_name_lower ON wines(LOWER(name));
CREATE INDEX idx_wines_winery_id ON wines(winery_id);
CREATE INDEX idx_wines_wine_type ON wines(wine_type);
CREATE INDEX idx_wines_vintage ON wines(vintage);
CREATE INDEX idx_wines_style ON wines(style);
CREATE INDEX idx_wines_rating_average ON wines(rating_average);
CREATE INDEX idx_wines_price_min ON wines(price_min);
CREATE INDEX idx_wines_is_active ON wines(is_active);
CREATE INDEX idx_wines_is_featured ON wines(is_featured);
CREATE INDEX idx_wines_created_at ON wines(created_at);

-- Full-text search index (PostgreSQL specific)
-- CREATE INDEX idx_wines_search ON wines USING gin(to_tsvector('spanish', name || ' ' || COALESCE(description_es, '')));

-- -----------------------------------------------------------------------------
-- Sample data for testing
-- -----------------------------------------------------------------------------

-- Insert sample wineries
INSERT INTO wineries (id, name, country, region, subregion, description, established) VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Bodega Catena Zapata', 'Argentina', 'Mendoza', 'Luján de Cuyo', 'One of the most prestigious wineries in Argentina, known for high-altitude Malbec.', 1902),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Bodega Norton', 'Argentina', 'Mendoza', 'Luján de Cuyo', 'Historic winery producing premium wines since 1895.', 1895),
    ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Trapiche', 'Argentina', 'Mendoza', 'Maipú', 'Leading Argentine winery with wide portfolio.', 1883),
    ('d4e5f6a7-b8c9-0123-def0-234567890123', 'Zuccardi', 'Argentina', 'Mendoza', 'Valle de Uco', 'Family winery focused on terroir-driven wines.', 1963),
    ('e5f6a7b8-c9d0-1234-ef01-345678901234', 'Luigi Bosca', 'Argentina', 'Mendoza', 'Luján de Cuyo', 'Premium winery with over 100 years of history.', 1901);

-- Insert sample wines
INSERT INTO wines (id, winery_id, name, vintage, wine_type, style, grapes, description_es, description_en, alcohol_content, price_min, price_max, rating_average, rating_count, is_featured) VALUES
    ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Catena Malbec', 2021, 'RED', 'Full-bodied', '["Malbec"]', 'Un Malbec intenso de altura con notas de ciruela y violetas.', 'An intense high-altitude Malbec with notes of plum and violets.', 14.5, 25.00, 30.00, 4.5, 125, TRUE),
    ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Catena Alta Malbec', 2019, 'RED', 'Full-bodied', '["Malbec"]', 'Malbec de alta gama con complejidad excepcional.', 'High-end Malbec with exceptional complexity.', 14.8, 55.00, 65.00, 4.8, 89, TRUE),
    ('33333333-3333-3333-3333-333333333333', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Norton Reserva Malbec', 2020, 'RED', 'Medium-bodied', '["Malbec"]', 'Malbec equilibrado con taninos suaves.', 'Balanced Malbec with soft tannins.', 14.0, 18.00, 22.00, 4.2, 203, FALSE),
    ('44444444-4444-4444-4444-444444444444', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Trapiche Medalla Chardonnay', 2022, 'WHITE', 'Full-bodied', '["Chardonnay"]', 'Chardonnay con crianza en roble, notas de vainilla.', 'Oak-aged Chardonnay with vanilla notes.', 13.5, 20.00, 25.00, 4.3, 156, FALSE),
    ('55555555-5555-5555-5555-555555555555', 'd4e5f6a7-b8c9-0123-def0-234567890123', 'Zuccardi Valle de Uco', 2020, 'RED', 'Full-bodied', '["Malbec", "Cabernet Sauvignon"]', 'Blend de altura con gran estructura.', 'High-altitude blend with great structure.', 14.2, 35.00, 40.00, 4.6, 112, TRUE),
    ('66666666-6666-6666-6666-666666666666', 'e5f6a7b8-c9d0-1234-ef01-345678901234', 'Luigi Bosca Pinot Noir', 2021, 'RED', 'Light-bodied', '["Pinot Noir"]', 'Pinot Noir elegante y aromático.', 'Elegant and aromatic Pinot Noir.', 13.0, 28.00, 35.00, 4.1, 78, FALSE),
    ('77777777-7777-7777-7777-777777777777', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Catena Rosé', 2023, 'ROSE', 'Light-bodied', '["Malbec"]', 'Rosado fresco y vibrante.', 'Fresh and vibrant rosé.', 12.5, 15.00, 18.00, 4.0, 45, FALSE),
    ('88888888-8888-8888-8888-888888888888', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Trapiche Extra Brut', 2022, 'SPARKLING', 'Dry', '["Chardonnay", "Pinot Noir"]', 'Espumante con finas burbujas.', 'Sparkling wine with fine bubbles.', 12.0, 12.00, 15.00, 4.0, 67, FALSE);


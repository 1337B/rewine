-- =============================================================================
-- V8__wine_routes_enhancements.sql - Wine Routes enhancements for browsing
-- =============================================================================

-- -----------------------------------------------------------------------------
-- ADD NEW COLUMNS TO wine_routes TABLE
-- -----------------------------------------------------------------------------
ALTER TABLE wine_routes ADD COLUMN IF NOT EXISTS country VARCHAR(100);
ALTER TABLE wine_routes ADD COLUMN IF NOT EXISTS subregion VARCHAR(100);
ALTER TABLE wine_routes ADD COLUMN IF NOT EXISTS estimated_days INTEGER;
ALTER TABLE wine_routes ADD COLUMN IF NOT EXISTS recommended_wine_types_json TEXT;

-- Update existing rows with default country (Argentina) if null
UPDATE wine_routes SET country = 'Argentina' WHERE country IS NULL;

-- Create indexes for hierarchical browsing
CREATE INDEX IF NOT EXISTS idx_wine_routes_country ON wine_routes(country);
CREATE INDEX IF NOT EXISTS idx_wine_routes_country_region ON wine_routes(country, region);
CREATE INDEX IF NOT EXISTS idx_wine_routes_country_region_subregion ON wine_routes(country, region, subregion);

-- -----------------------------------------------------------------------------
-- ROUTE_WINERIES JOIN TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS route_wineries (
    route_id    UUID NOT NULL,
    winery_id   UUID NOT NULL,
    stop_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (route_id, winery_id),

    CONSTRAINT fk_route_wineries_route
        FOREIGN KEY (route_id)
        REFERENCES wine_routes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_route_wineries_winery
        FOREIGN KEY (winery_id)
        REFERENCES wineries(id)
        ON DELETE CASCADE
);

-- Index for winery lookup
CREATE INDEX IF NOT EXISTS idx_route_wineries_winery_id ON route_wineries(winery_id);

-- -----------------------------------------------------------------------------
-- SAMPLE WINE ROUTES DATA
-- -----------------------------------------------------------------------------
INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by)
SELECT
    uuid_generate_v4(),
    'Ruta del Malbec - Luján de Cuyo',
    'Recorre las bodegas más emblemáticas de Luján de Cuyo, cuna del Malbec argentino. Visita viñedos centenarios y degusta los mejores vinos de altura.',
    'Argentina',
    'Mendoza',
    'Luján de Cuyo',
    480,
    2,
    'easy',
    'active',
    '["RED", "ROSE"]',
    (SELECT id FROM users LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE name = 'Ruta del Malbec - Luján de Cuyo')
AND EXISTS (SELECT 1 FROM users);

INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by)
SELECT
    uuid_generate_v4(),
    'Valle de Uco Premium Tour',
    'Descubre los viñedos de altura del Valle de Uco, donde se producen algunos de los vinos más premiados de Argentina.',
    'Argentina',
    'Mendoza',
    'Valle de Uco',
    600,
    3,
    'moderate',
    'active',
    '["RED", "WHITE", "SPARKLING"]',
    (SELECT id FROM users LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE name = 'Valle de Uco Premium Tour')
AND EXISTS (SELECT 1 FROM users);

INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by)
SELECT
    uuid_generate_v4(),
    'Ruta del Torrontés - Cafayate',
    'Explora los viñedos de Cafayate, hogar del Torrontés, el vino blanco insignia de Argentina.',
    'Argentina',
    'Salta',
    'Cafayate',
    360,
    2,
    'easy',
    'active',
    '["WHITE"]',
    (SELECT id FROM users LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE name = 'Ruta del Torrontés - Cafayate')
AND EXISTS (SELECT 1 FROM users);

INSERT INTO wine_routes (id, name, description, country, region, subregion, estimated_duration, estimated_days, difficulty, status, recommended_wine_types_json, created_by)
SELECT
    uuid_generate_v4(),
    'Patagonia Wine Experience',
    'Ruta vitivinícola por las bodegas de la Patagonia, con paisajes únicos y vinos de clima frío.',
    'Argentina',
    'Neuquén',
    'San Patricio del Chañar',
    540,
    3,
    'moderate',
    'active',
    '["RED", "WHITE", "SPARKLING"]',
    (SELECT id FROM users LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM wine_routes WHERE name = 'Patagonia Wine Experience')
AND EXISTS (SELECT 1 FROM users);

-- Link routes with wineries
INSERT INTO route_wineries (route_id, winery_id, stop_order)
SELECT wr.id, w.id, 1
FROM wine_routes wr, wineries w
WHERE wr.name = 'Ruta del Malbec - Luján de Cuyo'
  AND w.name = 'Bodega Catena Zapata'
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = wr.id AND winery_id = w.id);

INSERT INTO route_wineries (route_id, winery_id, stop_order)
SELECT wr.id, w.id, 2
FROM wine_routes wr, wineries w
WHERE wr.name = 'Ruta del Malbec - Luján de Cuyo'
  AND w.name = 'Bodega Norton'
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = wr.id AND winery_id = w.id);

INSERT INTO route_wineries (route_id, winery_id, stop_order)
SELECT wr.id, w.id, 3
FROM wine_routes wr, wineries w
WHERE wr.name = 'Ruta del Malbec - Luján de Cuyo'
  AND w.name = 'Luigi Bosca'
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = wr.id AND winery_id = w.id);

INSERT INTO route_wineries (route_id, winery_id, stop_order)
SELECT wr.id, w.id, 1
FROM wine_routes wr, wineries w
WHERE wr.name = 'Valle de Uco Premium Tour'
  AND w.name = 'Zuccardi'
  AND NOT EXISTS (SELECT 1 FROM route_wineries WHERE route_id = wr.id AND winery_id = w.id);


-- =============================================================================
-- V4__init_events_routes.sql - Events and Wine Routes tables
-- =============================================================================

-- -----------------------------------------------------------------------------
-- EVENTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE events (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    title               VARCHAR(255)    NOT NULL,
    description         TEXT,
    type                VARCHAR(50)     NOT NULL,
    start_date          TIMESTAMPTZ     NOT NULL,
    end_date            TIMESTAMPTZ     NOT NULL,
    location_name       VARCHAR(255),
    location_address    VARCHAR(255),
    location_city       VARCHAR(255),
    location_region     VARCHAR(255),
    latitude            DECIMAL,
    longitude           DECIMAL,
    price               DECIMAL,
    max_attendees       INTEGER,
    current_attendees   INTEGER         DEFAULT 0,
    status              VARCHAR(50)     NOT NULL DEFAULT 'draft',
    image_url           VARCHAR(255),
    organizer_id        UUID            NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_events_organizer
        FOREIGN KEY (organizer_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Indexes for events
CREATE INDEX idx_events_organizer_id ON events(organizer_id);
CREATE INDEX idx_events_start_date ON events(start_date);
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_type ON events(type);

-- -----------------------------------------------------------------------------
-- WINE_ROUTES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE wine_routes (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    region              VARCHAR(255),
    estimated_duration  INTEGER,
    total_distance      DOUBLE PRECISION,
    difficulty          VARCHAR(50),
    image_url           VARCHAR(255),
    status              VARCHAR(50)     NOT NULL DEFAULT 'draft',
    created_by          UUID            NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_wine_routes_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Indexes for wine_routes
CREATE INDEX idx_wine_routes_created_by ON wine_routes(created_by);
CREATE INDEX idx_wine_routes_region ON wine_routes(region);
CREATE INDEX idx_wine_routes_status ON wine_routes(status);

-- -----------------------------------------------------------------------------
-- WINE_ROUTE_STOPS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE wine_route_stops (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    wine_route_id       UUID            NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    type                VARCHAR(50),
    address             VARCHAR(255),
    latitude            DECIMAL,
    longitude           DECIMAL,
    stop_order          INTEGER         NOT NULL,
    estimated_duration  INTEGER,

    CONSTRAINT fk_wine_route_stops_route
        FOREIGN KEY (wine_route_id)
        REFERENCES wine_routes(id)
        ON DELETE CASCADE
);

-- Indexes for wine_route_stops
CREATE INDEX idx_wine_route_stops_route_id ON wine_route_stops(wine_route_id);
CREATE INDEX idx_wine_route_stops_order ON wine_route_stops(wine_route_id, stop_order);


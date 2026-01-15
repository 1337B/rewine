-- =============================================================================
-- V6__wine_comparisons.sql - AI-generated wine comparisons table
-- =============================================================================

-- -----------------------------------------------------------------------------
-- WINE_COMPARISONS TABLE
-- -----------------------------------------------------------------------------
-- Stores AI-generated comparisons between two wines, cached per language
-- A comparison is generated once and reused for subsequent requests
-- Normalization: wine_a_id < wine_b_id to avoid duplicate comparisons

CREATE TABLE wine_comparisons (
    id                  UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    wine_a_id           UUID            NOT NULL,
    wine_b_id           UUID            NOT NULL,
    language            VARCHAR(10)     NOT NULL,
    comparison_json     JSONB           NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Each wine pair can have one comparison per language
    -- wine_a_id must always be < wine_b_id (normalized order)
    CONSTRAINT uq_wine_comparisons_wine_pair_language
        UNIQUE (wine_a_id, wine_b_id, language),

    -- Ensure wine_a_id < wine_b_id for normalized ordering
    CONSTRAINT chk_wine_comparisons_normalized_order
        CHECK (wine_a_id < wine_b_id),

    -- Foreign keys to wines table
    CONSTRAINT fk_wine_comparisons_wine_a
        FOREIGN KEY (wine_a_id)
        REFERENCES wines(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_wine_comparisons_wine_b
        FOREIGN KEY (wine_b_id)
        REFERENCES wines(id)
        ON DELETE CASCADE
);

-- Indexes for wine comparisons
CREATE INDEX idx_wine_comparisons_wine_a_id ON wine_comparisons(wine_a_id);
CREATE INDEX idx_wine_comparisons_wine_b_id ON wine_comparisons(wine_b_id);
CREATE INDEX idx_wine_comparisons_language ON wine_comparisons(language);
CREATE INDEX idx_wine_comparisons_created_at ON wine_comparisons(created_at);

-- Combined index for efficient lookups
CREATE INDEX idx_wine_comparisons_pair_language ON wine_comparisons(wine_a_id, wine_b_id, language);

-- -----------------------------------------------------------------------------
-- COMMENTS
-- -----------------------------------------------------------------------------
COMMENT ON TABLE wine_comparisons IS 'Cached AI-generated wine comparisons by language';
COMMENT ON COLUMN wine_comparisons.wine_a_id IS 'Reference to the first wine (normalized: always < wine_b_id)';
COMMENT ON COLUMN wine_comparisons.wine_b_id IS 'Reference to the second wine (normalized: always > wine_a_id)';
COMMENT ON COLUMN wine_comparisons.language IS 'Language code (e.g., es-AR, en-US)';
COMMENT ON COLUMN wine_comparisons.comparison_json IS 'JSON containing the AI-generated comparison content';


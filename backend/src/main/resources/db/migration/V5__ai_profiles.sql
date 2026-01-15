-- =============================================================================
-- V5__ai_profiles.sql - AI-generated wine profiles table
-- =============================================================================

-- -----------------------------------------------------------------------------
-- WINE_AI_PROFILES TABLE
-- -----------------------------------------------------------------------------
-- Stores AI-generated profiles for wines, cached per language
-- A profile is generated once and reused for subsequent requests

CREATE TABLE wine_ai_profiles (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    wine_id         UUID            NOT NULL,
    language        VARCHAR(10)     NOT NULL,
    profile_json    JSONB           NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Each wine can have one profile per language
    CONSTRAINT uq_wine_ai_profiles_wine_language
        UNIQUE (wine_id, language),

    -- Foreign key to wines table
    CONSTRAINT fk_wine_ai_profiles_wine
        FOREIGN KEY (wine_id)
        REFERENCES wines(id)
        ON DELETE CASCADE
);

-- Indexes for wine AI profiles
CREATE INDEX idx_wine_ai_profiles_wine_id ON wine_ai_profiles(wine_id);
CREATE INDEX idx_wine_ai_profiles_language ON wine_ai_profiles(language);
CREATE INDEX idx_wine_ai_profiles_created_at ON wine_ai_profiles(created_at);

-- -----------------------------------------------------------------------------
-- COMMENTS
-- -----------------------------------------------------------------------------
COMMENT ON TABLE wine_ai_profiles IS 'Cached AI-generated wine profiles by language';
COMMENT ON COLUMN wine_ai_profiles.wine_id IS 'Reference to the wine';
COMMENT ON COLUMN wine_ai_profiles.language IS 'Language code (e.g., es-AR, en-US)';
COMMENT ON COLUMN wine_ai_profiles.profile_json IS 'JSON containing the AI-generated profile content';


-- =============================================================================
-- V3__init_reviews.sql - Reviews, likes and comments tables
-- =============================================================================

-- -----------------------------------------------------------------------------
-- REVIEWS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE reviews (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    wine_id         UUID            NOT NULL,
    user_id         UUID            NOT NULL,
    rating          DECIMAL(2, 1)   NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title           VARCHAR(255),
    comment         TEXT,
    is_verified     BOOLEAN         DEFAULT FALSE,
    helpful_count   INTEGER         DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reviews_wine
        FOREIGN KEY (wine_id)
        REFERENCES wines(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    -- One review per user per wine
    CONSTRAINT uq_reviews_user_wine UNIQUE (user_id, wine_id)
);

-- Indexes for reviews
CREATE INDEX idx_reviews_wine_id ON reviews(wine_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_wine_created ON reviews(wine_id, created_at DESC);

-- -----------------------------------------------------------------------------
-- REVIEW_LIKES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE review_likes (
    review_id       UUID            NOT NULL,
    user_id         UUID            NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (review_id, user_id),

    CONSTRAINT fk_review_likes_review
        FOREIGN KEY (review_id)
        REFERENCES reviews(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_likes_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Indexes for review_likes
CREATE INDEX idx_review_likes_review_id ON review_likes(review_id);
CREATE INDEX idx_review_likes_user_id ON review_likes(user_id);

-- -----------------------------------------------------------------------------
-- REVIEW_COMMENTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE review_comments (
    id              UUID            PRIMARY KEY DEFAULT uuid_generate_v4(),
    review_id       UUID            NOT NULL,
    user_id         UUID            NOT NULL,
    comment         TEXT            NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_comments_review
        FOREIGN KEY (review_id)
        REFERENCES reviews(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_comments_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Indexes for review_comments
CREATE INDEX idx_review_comments_review_id ON review_comments(review_id);
CREATE INDEX idx_review_comments_user_id ON review_comments(user_id);
CREATE INDEX idx_review_comments_created_at ON review_comments(created_at);


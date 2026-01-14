package com.rewine.backend.model.enums;

/**
 * Enum for review filter types.
 */
public enum ReviewFilter {

    /**
     * Most recent reviews first.
     */
    RECENT,

    /**
     * Featured reviews (most likes + comments).
     */
    FEATURED,

    /**
     * Current user's review.
     */
    MINE,

    /**
     * Highest rated reviews.
     */
    TOP_RATED
}


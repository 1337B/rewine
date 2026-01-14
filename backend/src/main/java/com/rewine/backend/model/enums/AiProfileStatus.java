package com.rewine.backend.model.enums;

/**
 * Status of AI-generated wine profile.
 */
public enum AiProfileStatus {

    /**
     * AI profile has not been requested for this wine.
     */
    NOT_REQUESTED,

    /**
     * AI profile generation is in progress.
     */
    GENERATING,

    /**
     * AI profile has been generated and is available.
     */
    GENERATED,

    /**
     * AI profile generation failed.
     */
    FAILED
}


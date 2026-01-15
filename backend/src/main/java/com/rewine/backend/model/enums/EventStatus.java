package com.rewine.backend.model.enums;

/**
 * Event status enumeration.
 */
public enum EventStatus {
    /**
     * Event is in draft mode, not visible to public.
     */
    DRAFT,

    /**
     * Event is published and visible to public.
     */
    PUBLISHED,

    /**
     * Event has been cancelled.
     */
    CANCELLED,

    /**
     * Event has been completed.
     */
    COMPLETED,

    /**
     * Event is sold out.
     */
    SOLD_OUT
}


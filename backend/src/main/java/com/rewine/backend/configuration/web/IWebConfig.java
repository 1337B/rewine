package com.rewine.backend.configuration.web;

/**
 * Interface for web configuration.
 */
public interface IWebConfig {

    /**
     * Returns the allowed CORS origins.
     */
    String[] getAllowedOrigins();

    /**
     * Returns the allowed HTTP methods.
     */
    String[] getAllowedMethods();
}


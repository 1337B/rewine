package com.rewine.backend.configuration.security;

/**
 * Interface for security configuration.
 */
public interface ISecurityConfig {

    /**
     * Returns the list of public endpoints that don't require authentication.
     */
    String[] getPublicEndpoints();
}


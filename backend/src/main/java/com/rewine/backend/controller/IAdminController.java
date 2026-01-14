package com.rewine.backend.controller;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Interface for admin-only endpoints.
 */
public interface IAdminController {

    /**
     * Test endpoint for admin role verification.
     *
     * @return success message
     */
    ResponseEntity<Map<String, String>> testAdminAccess();

    /**
     * Test endpoint for moderator role verification.
     *
     * @return success message
     */
    ResponseEntity<Map<String, String>> testModeratorAccess();
}


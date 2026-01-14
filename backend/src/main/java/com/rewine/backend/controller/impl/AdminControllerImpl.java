package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IAdminController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin endpoints implementation with method-level security.
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Administrative endpoints (requires ROLE_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminControllerImpl implements IAdminController {

    @Override
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Test admin access", description = "Verifies that the user has ROLE_ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin access verified"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ROLE_ADMIN")
    })
    public ResponseEntity<Map<String, String>> testAdminAccess() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "You have admin access!"
        ));
    }

    @Override
    @GetMapping("/moderator-test")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Test moderator access", description = "Verifies that the user has ROLE_ADMIN or ROLE_MODERATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Moderator access verified"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ROLE_ADMIN or ROLE_MODERATOR")
    })
    public ResponseEntity<Map<String, String>> testModeratorAccess() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "You have moderator access!"
        ));
    }
}


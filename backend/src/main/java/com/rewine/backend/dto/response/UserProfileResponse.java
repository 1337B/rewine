package com.rewine.backend.dto.response;

import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for user profile information.
 */
public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        String name,
        String avatarUrl,
        Set<String> roles,
        boolean emailVerified
) {
}


package com.rewine.backend.dto.common;

import com.rewine.backend.model.enums.AiProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response containing the AI profile cache status for a wine.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCacheStatusResponse {

    /**
     * The wine ID.
     */
    private UUID wineId;

    /**
     * The current status of the AI profile.
     */
    private AiProfileStatus status;

    /**
     * When the profile was generated (null if not generated).
     */
    private Instant generatedAt;

    /**
     * List of languages for which profiles are available.
     */
    private List<String> availableLanguages;

    /**
     * Whether a profile exists for the requested language.
     */
    private Boolean hasRequestedLanguage;

    /**
     * The requested language code.
     */
    private String requestedLanguage;
}


package com.rewine.backend.utils.validation;

import java.util.UUID;

/**
 * Normalizes wine pairs to ensure consistent ordering.
 * This prevents duplicate comparisons where (A, B) and (B, A) are stored separately.
 */
public interface IWinePairNormalizer {

    /**
     * Result of normalizing a wine pair.
     * Contains the normalized (first, second) wine IDs where first < second.
     */
    record NormalizedWinePair(UUID wineAId, UUID wineBId, boolean wasSwapped) {
        /**
         * Creates a normalized pair ensuring wineAId < wineBId.
         */
        public static NormalizedWinePair of(UUID id1, UUID id2) {
            if (id1.compareTo(id2) <= 0) {
                return new NormalizedWinePair(id1, id2, false);
            } else {
                return new NormalizedWinePair(id2, id1, true);
            }
        }
    }

    /**
     * Normalizes a wine pair so that the first ID is always less than the second.
     * This ensures consistent storage and lookup of wine comparisons.
     *
     * @param wineId1 the first wine ID
     * @param wineId2 the second wine ID
     * @return a normalized pair with wineAId < wineBId
     */
    NormalizedWinePair normalize(UUID wineId1, UUID wineId2);

    /**
     * Validates that both wine IDs are provided and different.
     *
     * @param wineId1 the first wine ID
     * @param wineId2 the second wine ID
     * @throws IllegalArgumentException if validation fails
     */
    void validate(UUID wineId1, UUID wineId2);
}


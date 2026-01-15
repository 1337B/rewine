package com.rewine.backend.utils.validation.impl;

import com.rewine.backend.utils.validation.IWinePairNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of wine pair normalizer.
 * Ensures consistent ordering of wine pairs for comparison storage.
 */
@Component
public class WinePairNormalizerImpl implements IWinePairNormalizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinePairNormalizerImpl.class);

    @Override
    public NormalizedWinePair normalize(UUID wineId1, UUID wineId2) {
        validate(wineId1, wineId2);

        NormalizedWinePair normalized = NormalizedWinePair.of(wineId1, wineId2);

        if (normalized.wasSwapped()) {
            LOGGER.debug("Wine pair normalized: swapped {} <-> {} to maintain order",
                    wineId1, wineId2);
        } else {
            LOGGER.debug("Wine pair already in normalized order: {} < {}",
                    wineId1, wineId2);
        }

        return normalized;
    }

    @Override
    public void validate(UUID wineId1, UUID wineId2) {
        if (Objects.isNull(wineId1)) {
            throw new IllegalArgumentException("First wine ID cannot be null");
        }
        if (Objects.isNull(wineId2)) {
            throw new IllegalArgumentException("Second wine ID cannot be null");
        }
        if (wineId1.equals(wineId2)) {
            throw new IllegalArgumentException("Cannot compare a wine with itself");
        }
    }
}


package com.rewine.backend.utils.converter;

/**
 * Interface for type converters.
 */
public interface IConverter<S, T> {

    /**
     * Convert source to target type.
     */
    T convert(S source);

    /**
     * Convert target back to source type (reverse).
     */
    S reverse(T target);
}


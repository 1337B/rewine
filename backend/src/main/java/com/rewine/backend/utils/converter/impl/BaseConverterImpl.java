package com.rewine.backend.utils.converter.impl;

import com.rewine.backend.utils.converter.IConverter;

/**
 * Base converter implementation with common functionality.
 */
public abstract class BaseConverterImpl<S, T> implements IConverter<S, T> {

    // Subclasses should implement convert() and reverse()
}


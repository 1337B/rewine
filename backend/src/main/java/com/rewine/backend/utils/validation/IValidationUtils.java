package com.rewine.backend.utils.validation;

/**
 * Interface for validation utilities.
 */
public interface IValidationUtils {

    /**
     * Validate email format.
     */
    boolean isValidEmail(String email);

    /**
     * Validate password strength.
     */
    boolean isValidPassword(String password);

    /**
     * Validate UUID format.
     */
    boolean isValidUuid(String uuid);

    /**
     * Check if string is not blank.
     */
    boolean isNotBlank(String value);
}


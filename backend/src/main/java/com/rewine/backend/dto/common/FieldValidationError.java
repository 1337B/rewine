package com.rewine.backend.dto.common;

/**
 * DTO for field validation errors.
 * Used to provide detailed information about validation failures.
 */
public record FieldValidationError(
        /**
         * The field that failed validation.
         */
        String field,

        /**
         * The validation error message.
         */
        String message
) {

    /**
     * Creates a new FieldValidationError.
     *
     * @param field   The field name
     * @param message The error message
     * @return A new FieldValidationError instance
     */
    public static FieldValidationError of(String field, String message) {
        return new FieldValidationError(field, message);
    }
}


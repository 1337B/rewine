package com.rewine.backend.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RewineException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Creates a ResourceNotFoundException.
     *
     * @param resourceName the name of the resource (e.g., "Wine", "User")
     * @param fieldName    the field used to search (e.g., "id", "email")
     * @param fieldValue   the value that was not found
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                ErrorCode.RESOURCE_NOT_FOUND,
                String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue)
        );
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Creates a ResourceNotFoundException with a custom message.
     *
     * @param message custom message
     */
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}


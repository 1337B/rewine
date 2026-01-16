package com.rewine.backend.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RewineException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Factory method for Wine not found.
     */
    public static ResourceNotFoundException forWine(UUID wineId) {
        return new ResourceNotFoundException("Wine", "id", wineId);
    }

    /**
     * Factory method for User not found.
     */
    public static ResourceNotFoundException forUser(UUID userId) {
        return new ResourceNotFoundException("User", "id", userId);
    }

    /**
     * Factory method for Event not found.
     */
    public static ResourceNotFoundException forEvent(UUID eventId) {
        return new ResourceNotFoundException("Event", "id", eventId);
    }

    /**
     * Factory method for WineRoute not found.
     */
    public static ResourceNotFoundException forWineRoute(UUID routeId) {
        return new ResourceNotFoundException("WineRoute", "id", routeId);
    }

    /**
     * Factory method for Winery not found.
     */
    public static ResourceNotFoundException forWinery(UUID wineryId) {
        return new ResourceNotFoundException("Winery", "id", wineryId);
    }

    /**
     * Factory method for Role not found.
     */
    public static ResourceNotFoundException forRole(String roleName) {
        return new ResourceNotFoundException("Role", "name", roleName);
    }

    /**
     * Factory method for WineAiProfile not found.
     */
    public static ResourceNotFoundException forWineAiProfile(UUID wineId, String language) {
        return new ResourceNotFoundException("WineAiProfile", "wineId:language", wineId + ":" + language);
    }

    /**
     * Factory method for WineComparison not found.
     */
    public static ResourceNotFoundException forWineComparison(UUID wineAId, UUID wineBId, String language) {
        return new ResourceNotFoundException("WineComparison", "wines:language",
                wineAId + ":" + wineBId + ":" + language);
    }

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


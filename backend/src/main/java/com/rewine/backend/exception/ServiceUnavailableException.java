package com.rewine.backend.exception;

import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * Exception thrown when an external service (AI, Maps, etc.) is not available.
 * This can occur when the service is disabled or not properly configured.
 */
public class ServiceUnavailableException extends RewineException {

    private final String serviceName;

    /**
     * Creates a new ServiceUnavailableException for a specific service.
     *
     * @param serviceName the name of the unavailable service (e.g., "AI", "Maps")
     * @param message     additional message about the unavailability
     */
    public ServiceUnavailableException(String serviceName, String message) {
        super(
                getErrorCodeForService(serviceName),
                HttpStatus.NOT_IMPLEMENTED,
                buildMessage(serviceName, message)
        );
        this.serviceName = serviceName;
    }

    /**
     * Creates a new ServiceUnavailableException for a specific service.
     *
     * @param serviceName the name of the unavailable service
     */
    public ServiceUnavailableException(String serviceName) {
        this(serviceName, null);
    }

    /**
     * Gets the name of the unavailable service.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Creates a ServiceUnavailableException for the AI service.
     *
     * @return the exception
     */
    public static ServiceUnavailableException forAi() {
        return new ServiceUnavailableException("AI");
    }

    /**
     * Creates a ServiceUnavailableException for the AI service with a custom message.
     *
     * @param message the custom message
     * @return the exception
     */
    public static ServiceUnavailableException forAi(String message) {
        return new ServiceUnavailableException("AI", message);
    }

    /**
     * Creates a ServiceUnavailableException for the Maps service.
     *
     * @return the exception
     */
    public static ServiceUnavailableException forMaps() {
        return new ServiceUnavailableException("Maps");
    }

    /**
     * Creates a ServiceUnavailableException for the Maps service with a custom message.
     *
     * @param message the custom message
     * @return the exception
     */
    public static ServiceUnavailableException forMaps(String message) {
        return new ServiceUnavailableException("Maps", message);
    }

    private static ErrorCode getErrorCodeForService(String serviceName) {
        return switch (serviceName.toUpperCase()) {
            case "AI" -> ErrorCode.AI_SERVICE_UNAVAILABLE;
            case "MAPS" -> ErrorCode.MAPS_SERVICE_UNAVAILABLE;
            default -> ErrorCode.SERVICE_UNAVAILABLE;
        };
    }

    private static String buildMessage(String serviceName, String additionalMessage) {
        String baseMessage = serviceName + " service is not configured or disabled. ";
        String guidance = switch (serviceName.toUpperCase()) {
            case "AI" -> "To enable AI features, set OPENAI_API_KEY environment variable "
                    + "and ensure rewine.ai.enabled=true.";
            case "MAPS" -> "To enable Maps features, set GOOGLE_MAPS_API_KEY environment variable "
                    + "and ensure rewine.maps.enabled=true.";
            default -> "Please check the service configuration.";
        };

        if (Objects.nonNull(additionalMessage) && !additionalMessage.isBlank()) {
            return baseMessage + additionalMessage + " " + guidance;
        }
        return baseMessage + guidance;
    }
}


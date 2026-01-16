package com.rewine.backend.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps error codes to HTTP status codes.
 */
public class ErrorMapping {

    private static final Map<ErrorCode, HttpStatus> ERROR_STATUS_MAP = new HashMap<>();

    static {
        // General errors
        ERROR_STATUS_MAP.put(ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        ERROR_STATUS_MAP.put(ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        ERROR_STATUS_MAP.put(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_STATUS_MAP.put(ErrorCode.RESOURCE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        ERROR_STATUS_MAP.put(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_STATUS_MAP.put(ErrorCode.CONFLICT, HttpStatus.CONFLICT);

        // Authentication errors
        ERROR_STATUS_MAP.put(ErrorCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put(ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put(ErrorCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put(ErrorCode.TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
        ERROR_STATUS_MAP.put(ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        ERROR_STATUS_MAP.put(ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);

        // User errors
        ERROR_STATUS_MAP.put(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_STATUS_MAP.put(ErrorCode.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
        ERROR_STATUS_MAP.put(ErrorCode.EMAIL_ALREADY_TAKEN, HttpStatus.CONFLICT);
        ERROR_STATUS_MAP.put(ErrorCode.INVALID_PASSWORD, HttpStatus.BAD_REQUEST);

        // Wine errors
        ERROR_STATUS_MAP.put(ErrorCode.WINE_NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_STATUS_MAP.put(ErrorCode.WINE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        ERROR_STATUS_MAP.put(ErrorCode.INVALID_WINE_DATA, HttpStatus.BAD_REQUEST);

        // Review errors
        ERROR_STATUS_MAP.put(ErrorCode.REVIEW_NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_STATUS_MAP.put(ErrorCode.DUPLICATE_REVIEW, HttpStatus.CONFLICT);

        // Event errors
        ERROR_STATUS_MAP.put(ErrorCode.EVENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_STATUS_MAP.put(ErrorCode.EVENT_FULL, HttpStatus.CONFLICT);
        ERROR_STATUS_MAP.put(ErrorCode.ALREADY_REGISTERED, HttpStatus.CONFLICT);

        // Wine route errors
        ERROR_STATUS_MAP.put(ErrorCode.WINE_ROUTE_NOT_FOUND, HttpStatus.NOT_FOUND);

        // External service errors
        ERROR_STATUS_MAP.put(ErrorCode.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
        ERROR_STATUS_MAP.put(ErrorCode.AI_SERVICE_UNAVAILABLE, HttpStatus.NOT_IMPLEMENTED);
        ERROR_STATUS_MAP.put(ErrorCode.MAPS_SERVICE_UNAVAILABLE, HttpStatus.NOT_IMPLEMENTED);
        ERROR_STATUS_MAP.put(ErrorCode.EXTERNAL_SERVICE_ERROR, HttpStatus.BAD_GATEWAY);
    }

    /**
     * Get HTTP status for an error code.
     */
    public static HttpStatus getHttpStatus(ErrorCode errorCode) {
        return ERROR_STATUS_MAP.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Get HTTP status for an exception.
     */
    public static HttpStatus getHttpStatus(RewineException exception) {
        return getHttpStatus(exception.getErrorCode());
    }

    private ErrorMapping() {
        // Utility class
    }
}


package com.rewine.backend.exception;

/**
 * Error codes for the application.
 */
public enum ErrorCode {

    // General errors (1xxx)
    INTERNAL_ERROR("E1000", "Internal server error"),
    VALIDATION_ERROR("E1001", "Validation failed"),
    RESOURCE_NOT_FOUND("E1002", "Resource not found"),
    RESOURCE_ALREADY_EXISTS("E1003", "Resource already exists"),
    NOT_FOUND("E1004", "Not found"),
    CONFLICT("E1005", "Conflict"),

    // Authentication errors (2xxx)
    AUTHENTICATION_FAILED("E2000", "Authentication failed"),
    INVALID_CREDENTIALS("E2001", "Invalid credentials"),
    TOKEN_EXPIRED("E2002", "Token has expired"),
    TOKEN_INVALID("E2003", "Invalid token"),
    UNAUTHORIZED("E2004", "Unauthorized access"),
    FORBIDDEN("E2005", "Access forbidden"),
    ACCESS_DENIED("E2006", "Access denied"),
    AUTHENTICATION_REQUIRED("E2007", "Authentication required"),

    // User errors (3xxx)
    USER_NOT_FOUND("E3000", "User not found"),
    USER_ALREADY_EXISTS("E3001", "User already exists"),
    EMAIL_ALREADY_TAKEN("E3002", "Email already taken"),
    INVALID_PASSWORD("E3003", "Invalid password"),

    // Wine errors (4xxx)
    WINE_NOT_FOUND("E4000", "Wine not found"),
    WINE_ALREADY_EXISTS("E4001", "Wine already exists"),
    INVALID_WINE_DATA("E4002", "Invalid wine data"),

    // Review errors (5xxx)
    REVIEW_NOT_FOUND("E5000", "Review not found"),
    DUPLICATE_REVIEW("E5001", "User already reviewed this wine"),

    // Event errors (6xxx)
    EVENT_NOT_FOUND("E6000", "Event not found"),
    EVENT_FULL("E6001", "Event is full"),
    ALREADY_REGISTERED("E6002", "Already registered for event"),

    // Wine route errors (7xxx)
    WINE_ROUTE_NOT_FOUND("E7000", "Wine route not found");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}


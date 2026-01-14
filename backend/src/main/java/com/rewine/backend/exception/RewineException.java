package com.rewine.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all Rewine application exceptions.
 */
public class RewineException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;
    private final Object details;

    public RewineException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.status = ErrorMapping.getHttpStatus(errorCode);
        this.details = null;
    }

    public RewineException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = ErrorMapping.getHttpStatus(errorCode);
        this.details = null;
    }

    public RewineException(ErrorCode errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.status = ErrorMapping.getHttpStatus(errorCode);
        this.details = details;
    }

    public RewineException(ErrorCode errorCode, HttpStatus status, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = null;
    }

    public RewineException(ErrorCode errorCode, HttpStatus status, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }

    public RewineException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = ErrorMapping.getHttpStatus(errorCode);
        this.details = null;
    }

    public RewineException(ErrorCode errorCode, String message, Throwable cause, Object details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = ErrorMapping.getHttpStatus(errorCode);
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Object getDetails() {
        return details;
    }

    /**
     * Creates a new RewineException with NOT_FOUND error.
     */
    public static RewineException notFound(String message) {
        return new RewineException(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    /**
     * Creates a new RewineException with VALIDATION_ERROR.
     */
    public static RewineException validationError(String message, Object details) {
        return new RewineException(ErrorCode.VALIDATION_ERROR, message, details);
    }

    /**
     * Creates a new RewineException with UNAUTHORIZED error.
     */
    public static RewineException unauthorized(String message) {
        return new RewineException(ErrorCode.UNAUTHORIZED, message);
    }

    /**
     * Creates a new RewineException with FORBIDDEN error.
     */
    public static RewineException forbidden(String message) {
        return new RewineException(ErrorCode.FORBIDDEN, message);
    }

    /**
     * Creates a new RewineException with CONFLICT error.
     */
    public static RewineException conflict(String message) {
        return new RewineException(ErrorCode.RESOURCE_ALREADY_EXISTS, message);
    }

    /**
     * Creates a new RewineException with INTERNAL_ERROR.
     */
    public static RewineException internalError(String message, Throwable cause) {
        return new RewineException(ErrorCode.INTERNAL_ERROR, message, cause);
    }
}


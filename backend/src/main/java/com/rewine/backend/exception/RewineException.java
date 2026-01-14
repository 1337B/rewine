package com.rewine.backend.exception;

/**
 * Base exception for all Rewine application exceptions.
 */
public class RewineException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public RewineException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public RewineException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public RewineException(ErrorCode errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public RewineException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}


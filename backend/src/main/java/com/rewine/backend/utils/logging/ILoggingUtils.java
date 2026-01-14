package com.rewine.backend.utils.logging;

/**
 * Interface for logging utilities.
 */
public interface ILoggingUtils {

    /**
     * Log method entry.
     */
    void logEntry(String className, String methodName, Object... args);

    /**
     * Log method exit.
     */
    void logExit(String className, String methodName, Object result);

    /**
     * Log exception.
     */
    void logException(String className, String methodName, Throwable exception);
}


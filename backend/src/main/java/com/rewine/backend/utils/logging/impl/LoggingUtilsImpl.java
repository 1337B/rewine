package com.rewine.backend.utils.logging.impl;

import com.rewine.backend.utils.logging.ILoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Logging utilities implementation.
 */
@Component
public class LoggingUtilsImpl implements ILoggingUtils {

    @Override
    public void logEntry(String className, String methodName, Object... args) {
        Logger logger = LoggerFactory.getLogger(className);
        if (logger.isDebugEnabled()) {
            logger.debug("Entering {}.{}() with arguments: {}",
                    className, methodName, Arrays.toString(args));
        }
    }

    @Override
    public void logExit(String className, String methodName, Object result) {
        Logger logger = LoggerFactory.getLogger(className);
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting {}.{}() with result: {}",
                    className, methodName, result);
        }
    }

    @Override
    public void logException(String className, String methodName, Throwable exception) {
        Logger logger = LoggerFactory.getLogger(className);
        logger.error("Exception in {}.{}(): {}",
                className, methodName, exception.getMessage(), exception);
    }
}


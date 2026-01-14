package com.rewine.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 *
 * @deprecated Use {@link com.rewine.backend.controller.advice.impl.GlobalExceptionHandlerImpl} instead.
 *             This class is kept for backwards compatibility and will be removed in a future version.
 */
@Deprecated(since = "0.0.2", forRemoval = true)
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public ResponseEntity<ErrorResponse> handleRewineException(RewineException ex) {
        LOGGER.error("Application error: {} - {}", ex.getCode(), ex.getMessage(), ex);

        HttpStatus status = ErrorMapping.getHttpStatus(ex);
        ErrorResponse response = new ErrorResponse(
                ex.getCode(),
                ex.getMessage(),
                status.value(),
                Instant.now()
        );

        return ResponseEntity.status(status).body(response);
    }

    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        LOGGER.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                Instant.now(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_ERROR.getCode(),
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Error response DTO.
     *
     * @deprecated Use {@link com.rewine.backend.dto.common.ApiErrorResponse} instead.
     */
    @Deprecated(since = "0.0.2", forRemoval = true)
    public record ErrorResponse(
            String code,
            String message,
            int status,
            Instant timestamp,
            Map<String, String> details
    ) {
        public ErrorResponse(String code, String message, int status, Instant timestamp) {
            this(code, message, status, timestamp, null);
        }
    }
}


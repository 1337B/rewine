package com.rewine.backend.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standard API error response DTO.
 * Used for consistent error responses across all endpoints.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        /**
         * Timestamp when the error occurred.
         */
        Instant timestamp,

        /**
         * Request path that caused the error.
         */
        String path,

        /**
         * Unique request ID for tracing.
         */
        String requestId,

        /**
         * HTTP status code.
         */
        int status,

        /**
         * Application-specific error code.
         */
        String code,

        /**
         * Human-readable error message.
         */
        String message,

        /**
         * Additional error details (e.g., validation errors).
         */
        List<FieldValidationError> details
) {

    /**
     * Constructor without details.
     */
    public ApiErrorResponse(Instant timestamp, String path, String requestId, int status, String code, String message) {
        this(timestamp, path, requestId, status, code, message, null);
    }

    /**
     * Builder for creating ApiErrorResponse instances.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for ApiErrorResponse.
     */
    public static class Builder {
        private Instant timestamp = Instant.now();
        private String path;
        private String requestId;
        private int status;
        private String code;
        private String message;
        private List<FieldValidationError> details;

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(List<FieldValidationError> details) {
            this.details = details;
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(timestamp, path, requestId, status, code, message, details);
        }
    }
}


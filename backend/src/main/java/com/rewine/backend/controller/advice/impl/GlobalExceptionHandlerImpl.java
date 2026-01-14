package com.rewine.backend.controller.advice.impl;

import com.rewine.backend.controller.advice.IGlobalExceptionHandler;
import com.rewine.backend.dto.common.ApiErrorResponse;
import com.rewine.backend.dto.common.FieldValidationError;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.utils.logging.IRequestLoggingFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Global exception handler implementation.
 * Provides consistent error responses across all REST endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandlerImpl implements IGlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlerImpl.class);

    @Override
    @ExceptionHandler(RewineException.class)
    public ResponseEntity<ApiErrorResponse> handleRewineException(RewineException ex) {
        LOGGER.error("Application error: {} - {}", ex.getCode(), ex.getMessage(), ex);

        HttpStatus status = ex.getStatus();
        ApiErrorResponse response = buildErrorResponse(
                status,
                ex.getCode(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        LOGGER.warn("Validation error: {}", ex.getMessage());

        List<FieldValidationError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError fieldError
                            ? fieldError.getField()
                            : error.getObjectName();
                    String errorMessage = error.getDefaultMessage();
                    return FieldValidationError.of(fieldName, errorMessage);
                })
                .toList();

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles constraint violation exceptions from @Validated on path/query params.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.warn("Constraint violation: {}", ex.getMessage());

        List<FieldValidationError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    String fieldName = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return FieldValidationError.of(fieldName, violation.getMessage());
                })
                .toList();

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles illegal argument exceptions.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.warn("Illegal argument: {}", ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.getCode(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles missing request parameters.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException ex) {
        LOGGER.warn("Missing parameter: {}", ex.getMessage());

        List<FieldValidationError> fieldErrors = List.of(
                FieldValidationError.of(ex.getParameterName(), "Parameter is required")
        );

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Missing required parameter: " + ex.getParameterName(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles type mismatch exceptions.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        LOGGER.warn("Type mismatch: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.getCode(),
                message,
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles malformed JSON requests.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.warn("Message not readable: {}", ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Malformed request body",
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    @Override
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        LOGGER.warn("Authentication error: {}", ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED.getCode(),
                "Authentication required",
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @Override
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.warn("Access denied: {}", ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                ErrorCode.FORBIDDEN.getCode(),
                "Access denied",
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @Override
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        LOGGER.warn("No handler found: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                "Resource not found: " + ex.getRequestURL(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @Override
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR.getCode(),
                "An unexpected error occurred",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Builds an ApiErrorResponse with common fields populated.
     */
    private ApiErrorResponse buildErrorResponse(
            HttpStatus status,
            String code,
            String message,
            List<FieldValidationError> details) {

        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .path(getRequestPath())
                .requestId(getRequestId())
                .status(status.value())
                .code(code)
                .message(message)
                .details(details)
                .build();
    }

    /**
     * Gets the current request path.
     */
    private String getRequestPath() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.nonNull(attributes)) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            LOGGER.debug("Could not get request path", e);
        }
        return null;
    }

    /**
     * Gets the request ID from MDC.
     */
    private String getRequestId() {
        return MDC.get(IRequestLoggingFilter.MDC_REQUEST_ID);
    }
}


package com.rewine.backend.controller.advice;

import com.rewine.backend.dto.common.ApiErrorResponse;
import com.rewine.backend.exception.RewineException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Interface for global exception handling.
 */
public interface IGlobalExceptionHandler {

    /**
     * Handles RewineException.
     *
     * @param ex The exception
     * @return ResponseEntity with ApiErrorResponse
     */
    ResponseEntity<ApiErrorResponse> handleRewineException(RewineException ex);

    /**
     * Handles validation exceptions from @Valid annotations.
     *
     * @param ex The exception
     * @return ResponseEntity with ApiErrorResponse containing field errors
     */
    ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex);

    /**
     * Handles authentication exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with ApiErrorResponse
     */
    ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex);

    /**
     * Handles access denied exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with ApiErrorResponse
     */
    ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex);

    /**
     * Handles 404 not found exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with ApiErrorResponse
     */
    ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex);

    /**
     * Handles all other exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with ApiErrorResponse
     */
    ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex);
}


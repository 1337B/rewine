package com.rewine.backend.controller;

import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.RefreshRequest;
import com.rewine.backend.dto.request.RegisterRequest;
import com.rewine.backend.dto.response.AuthResponse;
import com.rewine.backend.dto.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Interface for authentication endpoints.
 */
public interface IAuthController {

    /**
     * Registers a new user.
     *
     * @param request the registration request
     * @param httpRequest the HTTP request for extracting client info
     * @return authentication response with tokens
     */
    ResponseEntity<AuthResponse> register(RegisterRequest request, HttpServletRequest httpRequest);

    /**
     * Authenticates a user.
     *
     * @param request the login request
     * @param httpRequest the HTTP request for extracting client info
     * @return authentication response with tokens
     */
    ResponseEntity<AuthResponse> login(LoginRequest request, HttpServletRequest httpRequest);

    /**
     * Refreshes the access token.
     *
     * @param request the refresh request
     * @param httpRequest the HTTP request for extracting client info
     * @return authentication response with new tokens
     */
    ResponseEntity<AuthResponse> refresh(RefreshRequest request, HttpServletRequest httpRequest);

    /**
     * Logs out the current user.
     *
     * @param request the refresh request containing the token to revoke
     * @return empty response
     */
    ResponseEntity<Void> logout(RefreshRequest request);

    /**
     * Gets the current user's profile.
     *
     * @return user profile response
     */
    ResponseEntity<UserProfileResponse> getCurrentUser();
}


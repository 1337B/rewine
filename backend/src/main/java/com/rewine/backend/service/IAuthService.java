package com.rewine.backend.service;

import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.RefreshRequest;
import com.rewine.backend.dto.request.RegisterRequest;
import com.rewine.backend.dto.response.AuthResponse;

/**
 * Service interface for authentication operations.
 */
public interface IAuthService {

    /**
     * Registers a new user.
     *
     * @param request the registration request
     * @param ipAddress the client IP address
     * @param deviceInfo the client device information
     * @return authentication response with tokens
     */
    AuthResponse register(RegisterRequest request, String ipAddress, String deviceInfo);

    /**
     * Authenticates a user and returns tokens.
     *
     * @param request the login request
     * @param ipAddress the client IP address
     * @param deviceInfo the client device information
     * @return authentication response with tokens
     */
    AuthResponse login(LoginRequest request, String ipAddress, String deviceInfo);

    /**
     * Refreshes the access token using a refresh token.
     *
     * @param request the refresh request containing the refresh token
     * @param ipAddress the client IP address
     * @param deviceInfo the client device information
     * @return authentication response with new tokens
     */
    AuthResponse refresh(RefreshRequest request, String ipAddress, String deviceInfo);

    /**
     * Logs out a user by revoking their refresh token.
     *
     * @param refreshToken the refresh token to revoke
     */
    void logout(String refreshToken);

    /**
     * Logs out a user from all devices by revoking all their refresh tokens.
     *
     * @param username the username
     */
    void logoutAll(String username);
}


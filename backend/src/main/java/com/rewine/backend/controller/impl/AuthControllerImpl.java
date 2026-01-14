package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IAuthController;
import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.RefreshRequest;
import com.rewine.backend.dto.request.RegisterRequest;
import com.rewine.backend.dto.response.AuthResponse;
import com.rewine.backend.dto.response.UserProfileResponse;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.model.entity.RoleEntity;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.repository.IUserRepository;
import com.rewine.backend.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Authentication endpoints implementation.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthControllerImpl implements IAuthController {

    private final IAuthService authService;
    private final IUserRepository userRepository;

    public AuthControllerImpl(IAuthService authService, IUserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String deviceInfo = extractDeviceInfo(httpRequest);

        AuthResponse response = authService.register(request, ipAddress, deviceInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account disabled or locked")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String deviceInfo = extractDeviceInfo(httpRequest);

        AuthResponse response = authService.login(request, ipAddress, deviceInfo);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Gets a new access token using a refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String deviceInfo = extractDeviceInfo(httpRequest);

        AuthResponse response = authService.refresh(request, ipAddress, deviceInfo);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revokes the refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully")
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new RewineException(
                    ErrorCode.UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED,
                    "Not authenticated"
            );
        }

        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        UserProfileResponse profile = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl(),
                user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toSet()),
                user.isEmailVerified()
        );

        return ResponseEntity.ok(profile);
    }

    /**
     * Extracts client IP address from the request.
     */
    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (Objects.nonNull(xForwardedFor) && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extracts device info (User-Agent) from the request.
     */
    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (Objects.nonNull(userAgent) && userAgent.length() > 500) {
            return userAgent.substring(0, 500);
        }
        return userAgent;
    }
}


package com.rewine.backend.service.impl;

import com.rewine.backend.configuration.properties.JwtProperties;
import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.RefreshRequest;
import com.rewine.backend.dto.request.RegisterRequest;
import com.rewine.backend.dto.response.AuthResponse;
import com.rewine.backend.dto.response.UserProfileResponse;
import com.rewine.backend.exception.ErrorCode;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.model.entity.RefreshTokenEntity;
import com.rewine.backend.model.entity.RoleEntity;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.repository.IRefreshTokenRepository;
import com.rewine.backend.repository.IRoleRepository;
import com.rewine.backend.repository.IUserRepository;
import com.rewine.backend.service.IAuthService;
import com.rewine.backend.service.IJwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication service implementation.
 */
@Service
public class AuthServiceImpl implements IAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IJwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(
            IUserRepository userRepository,
            IRoleRepository roleRepository,
            IRefreshTokenRepository refreshTokenRepository,
            IJwtTokenService jwtTokenService,
            PasswordEncoder passwordEncoder,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, String ipAddress, String deviceInfo) {
        LOGGER.info("Registering new user: {}", request.username());

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new RewineException(
                    ErrorCode.CONFLICT,
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RewineException(
                    ErrorCode.CONFLICT,
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        // Get default user role
        RoleEntity userRole = roleRepository.findByName(RoleEntity.ROLE_USER)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.INTERNAL_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Default user role not found"
                ));

        // Create user entity
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .enabled(true)
                .emailVerified(false)
                .locked(false)
                .build();

        user.getRoles().add(userRole);
        user = userRepository.save(user);

        LOGGER.info("User registered successfully: {}", user.getUsername());

        return createAuthResponse(user, ipAddress, deviceInfo);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String deviceInfo) {
        LOGGER.info("Login attempt for: {}", request.usernameOrEmail());

        // Find user by username or email
        UserEntity user = userRepository.findByEmailOrUsername(
                        request.usernameOrEmail(),
                        request.usernameOrEmail())
                .orElseThrow(() -> new RewineException(
                        ErrorCode.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            LOGGER.warn("Invalid password for user: {}", user.getUsername());
            throw new RewineException(
                    ErrorCode.UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new RewineException(
                    ErrorCode.FORBIDDEN,
                    HttpStatus.FORBIDDEN,
                    "Account is disabled"
            );
        }

        // Check if user is locked
        if (user.isLocked()) {
            throw new RewineException(
                    ErrorCode.FORBIDDEN,
                    HttpStatus.FORBIDDEN,
                    "Account is locked: " + user.getLockReason()
            );
        }

        // Update last login
        userRepository.updateLastLoginAt(user.getId(), Instant.now());

        LOGGER.info("User logged in successfully: {}", user.getUsername());

        return createAuthResponse(user, ipAddress, deviceInfo);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request, String ipAddress, String deviceInfo) {
        String plainToken = request.refreshToken();
        String tokenHash = jwtTokenService.hashRefreshToken(plainToken);

        // Find valid token
        RefreshTokenEntity refreshToken = refreshTokenRepository
                .findValidTokenByHash(tokenHash, Instant.now())
                .orElseThrow(() -> new RewineException(
                        ErrorCode.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid or expired refresh token"
                ));

        UserEntity user = refreshToken.getUser();

        // Check if user is still active
        if (!user.isEnabled() || user.isLocked()) {
            throw new RewineException(
                    ErrorCode.FORBIDDEN,
                    HttpStatus.FORBIDDEN,
                    "Account is disabled or locked"
            );
        }

        // Generate new tokens (token rotation)
        String newPlainRefreshToken = jwtTokenService.generateRefreshToken();
        String newTokenHash = jwtTokenService.hashRefreshToken(newPlainRefreshToken);

        // Revoke old token and mark as replaced
        refreshToken.revokeAndReplace(newTokenHash);
        refreshTokenRepository.save(refreshToken);

        // Create new refresh token entity
        RefreshTokenEntity newRefreshToken = RefreshTokenEntity.builder()
                .user(user)
                .tokenHash(newTokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // Generate new access token
        String accessToken = jwtTokenService.generateAccessToken(user);

        LOGGER.info("Token refreshed for user: {}", user.getUsername());

        return AuthResponse.of(
                accessToken,
                newPlainRefreshToken,
                jwtProperties.getAccessTokenExpiration() / 1000,
                mapToUserProfile(user)
        );
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (Objects.isNull(refreshToken) || refreshToken.isBlank()) {
            return;
        }

        String tokenHash = jwtTokenService.hashRefreshToken(refreshToken);
        refreshTokenRepository.revokeTokenByHash(
                tokenHash,
                RefreshTokenEntity.REVOKED_LOGOUT,
                Instant.now()
        );

        LOGGER.info("User logged out");
    }

    @Override
    @Transactional
    public void logoutAll(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RewineException(
                        ErrorCode.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        refreshTokenRepository.revokeAllTokensForUser(
                user.getId(),
                RefreshTokenEntity.REVOKED_LOGOUT,
                Instant.now()
        );

        LOGGER.info("All sessions logged out for user: {}", username);
    }

    /**
     * Creates an auth response with new tokens.
     */
    private AuthResponse createAuthResponse(UserEntity user, String ipAddress, String deviceInfo) {
        String accessToken = jwtTokenService.generateAccessToken(user);
        String plainRefreshToken = jwtTokenService.generateRefreshToken();
        String refreshTokenHash = jwtTokenService.hashRefreshToken(plainRefreshToken);

        // Store refresh token
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .tokenHash(refreshTokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return AuthResponse.of(
                accessToken,
                plainRefreshToken,
                jwtProperties.getAccessTokenExpiration() / 1000,
                mapToUserProfile(user)
        );
    }

    /**
     * Maps a user entity to a profile response.
     */
    private UserProfileResponse mapToUserProfile(UserEntity user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl(),
                roleNames,
                user.isEmailVerified()
        );
    }
}


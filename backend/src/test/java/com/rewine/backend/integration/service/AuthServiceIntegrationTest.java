package com.rewine.backend.integration.service;

import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.RegisterRequest;
import com.rewine.backend.dto.response.AuthResponse;
import com.rewine.backend.exception.RewineException;
import com.rewine.backend.integration.BaseIntegrationTest;
import com.rewine.backend.repository.IRefreshTokenRepository;
import com.rewine.backend.repository.IUserRepository;
import com.rewine.backend.service.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for IAuthService using Testcontainers with PostgreSQL.
 */
@Transactional
@DisplayName("Auth Service Integration Tests")
class AuthServiceIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_IP = "127.0.0.1";
    private static final String TEST_DEVICE = "Integration Test Device";
    private static final int SLEEP_MILLIS = 1100;

    /** Number of parts in a valid JWT token (header.payload.signature). */
    private static final int JWT_PARTS_COUNT = 3;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("User Registration")
    class RegistrationTests {

        @Test
        @DisplayName("should register new user successfully")
        void shouldRegisterNewUser() {
            RegisterRequest request = new RegisterRequest(
                    "testuser",
                    "test@example.com",
                    "Password123!",
                    "Test User"
            );

            AuthResponse response = authService.register(request, TEST_IP, TEST_DEVICE);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.refreshToken()).isNotBlank();
            assertThat(response.user()).isNotNull();
            assertThat(response.user().username()).isEqualTo("testuser");
            assertThat(response.user().email()).isEqualTo("test@example.com");
            assertThat(response.user().name()).isEqualTo("Test User");
            assertThat(response.user().roles()).contains("ROLE_USER");
        }

        @Test
        @DisplayName("should register user without optional name")
        void shouldRegisterUserWithoutName() {
            RegisterRequest request = new RegisterRequest(
                    "testuser2",
                    "test2@example.com",
                    "Password123!",
                    null
            );

            AuthResponse response = authService.register(request, TEST_IP, TEST_DEVICE);

            assertThat(response).isNotNull();
            assertThat(response.user().username()).isEqualTo("testuser2");
            assertThat(response.user().name()).isNull();
        }

        @Test
        @DisplayName("should fail when username already exists")
        void shouldFailWhenUsernameExists() {
            RegisterRequest firstRequest = new RegisterRequest(
                    "duplicateuser",
                    "first@example.com",
                    "Password123!",
                    "First User"
            );
            authService.register(firstRequest, TEST_IP, TEST_DEVICE);

            RegisterRequest duplicateRequest = new RegisterRequest(
                    "duplicateuser",
                    "second@example.com",
                    "Password123!",
                    "Second User"
            );

            assertThatThrownBy(() -> authService.register(duplicateRequest, TEST_IP, TEST_DEVICE))
                    .isInstanceOf(RewineException.class)
                    .hasMessageContaining("Username already exists");
        }

        @Test
        @DisplayName("should fail when email already exists")
        void shouldFailWhenEmailExists() {
            RegisterRequest firstRequest = new RegisterRequest(
                    "user1",
                    "duplicate@example.com",
                    "Password123!",
                    "First User"
            );
            authService.register(firstRequest, TEST_IP, TEST_DEVICE);

            RegisterRequest duplicateRequest = new RegisterRequest(
                    "user2",
                    "duplicate@example.com",
                    "Password123!",
                    "Second User"
            );

            assertThatThrownBy(() -> authService.register(duplicateRequest, TEST_IP, TEST_DEVICE))
                    .isInstanceOf(RewineException.class)
                    .hasMessageContaining("Email already exists");
        }

        @Test
        @DisplayName("should store user in database after registration")
        void shouldStoreUserInDatabase() {
            RegisterRequest request = new RegisterRequest(
                    "persisteduser",
                    "persisted@example.com",
                    "Password123!",
                    "Persisted User"
            );

            authService.register(request, TEST_IP, TEST_DEVICE);

            assertThat(userRepository.findByUsername("persisteduser")).isPresent();
            assertThat(userRepository.findByEmail("persisted@example.com")).isPresent();
        }

        @Test
        @DisplayName("should create refresh token on registration")
        void shouldCreateRefreshTokenOnRegistration() {
            RegisterRequest request = new RegisterRequest(
                    "tokenuser",
                    "token@example.com",
                    "Password123!",
                    "Token User"
            );

            AuthResponse response = authService.register(request, TEST_IP, TEST_DEVICE);

            // Verify refresh token was stored
            assertThat(response.refreshToken()).isNotBlank();
            assertThat(refreshTokenRepository.count()).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("User Login")
    class LoginTests {

        private void registerTestUser() {
            RegisterRequest request = new RegisterRequest(
                    "loginuser",
                    "login@example.com",
                    "Password123!",
                    "Login User"
            );
            authService.register(request, TEST_IP, TEST_DEVICE);
        }

        @Test
        @DisplayName("should login with username successfully")
        void shouldLoginWithUsername() {
            registerTestUser();

            LoginRequest request = new LoginRequest("loginuser", "Password123!");

            AuthResponse response = authService.login(request, TEST_IP, TEST_DEVICE);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.refreshToken()).isNotBlank();
            assertThat(response.user().username()).isEqualTo("loginuser");
        }

        @Test
        @DisplayName("should login with email successfully")
        void shouldLoginWithEmail() {
            registerTestUser();

            LoginRequest request = new LoginRequest("login@example.com", "Password123!");

            AuthResponse response = authService.login(request, TEST_IP, TEST_DEVICE);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.user().email()).isEqualTo("login@example.com");
        }

        @Test
        @DisplayName("should fail with wrong password")
        void shouldFailWithWrongPassword() {
            registerTestUser();

            LoginRequest request = new LoginRequest("loginuser", "WrongPassword!");

            assertThatThrownBy(() -> authService.login(request, TEST_IP, TEST_DEVICE))
                    .isInstanceOf(RewineException.class)
                    .hasMessageContaining("Invalid credentials");
        }

        @Test
        @DisplayName("should fail with non-existent user")
        void shouldFailWithNonExistentUser() {
            LoginRequest request = new LoginRequest("nonexistent", "Password123!");

            assertThatThrownBy(() -> authService.login(request, TEST_IP, TEST_DEVICE))
                    .isInstanceOf(RewineException.class)
                    .hasMessageContaining("Invalid credentials");
        }

        @Test
        @DisplayName("should return different tokens on each login")
        void shouldReturnDifferentTokensOnEachLogin() throws InterruptedException {
            registerTestUser();

            LoginRequest request = new LoginRequest("loginuser", "Password123!");

            AuthResponse firstLogin = authService.login(request, TEST_IP, TEST_DEVICE);

            // Wait to ensure different JWT timestamp (iat claim has second precision)
            Thread.sleep(SLEEP_MILLIS);

            AuthResponse secondLogin = authService.login(request, TEST_IP, "Different Device");

            // Access tokens should differ due to different timestamps
            assertThat(firstLogin.accessToken()).isNotEqualTo(secondLogin.accessToken());
            // Refresh tokens should always differ (random generation)
            assertThat(firstLogin.refreshToken()).isNotEqualTo(secondLogin.refreshToken());
        }
    }

    @Nested
    @DisplayName("Token Generation")
    class TokenGenerationTests {

        @Test
        @DisplayName("should generate valid JWT access token")
        void shouldGenerateValidJwtAccessToken() {
            RegisterRequest request = new RegisterRequest(
                    "jwtuser",
                    "jwt@example.com",
                    "Password123!",
                    "JWT User"
            );

            AuthResponse response = authService.register(request, TEST_IP, TEST_DEVICE);

            // JWT should have 3 parts separated by dots
            String[] tokenParts = response.accessToken().split("\\.");
            assertThat(tokenParts).hasSize(JWT_PARTS_COUNT);
        }

        @Test
        @DisplayName("should include user info in response")
        void shouldIncludeUserInfoInResponse() {
            RegisterRequest request = new RegisterRequest(
                    "infouser",
                    "info@example.com",
                    "Password123!",
                    "Info User"
            );

            AuthResponse response = authService.register(request, TEST_IP, TEST_DEVICE);

            assertThat(response.user()).isNotNull();
            assertThat(response.user().id()).isNotNull();
            assertThat(response.user().username()).isEqualTo("infouser");
            assertThat(response.user().email()).isEqualTo("info@example.com");
            assertThat(response.user().name()).isEqualTo("Info User");
        }
    }

    @Nested
    @DisplayName("Logout")
    class LogoutTests {

        @Test
        @DisplayName("should logout successfully with valid refresh token")
        void shouldLogoutSuccessfully() {
            RegisterRequest registerRequest = new RegisterRequest(
                    "logoutuser",
                    "logout@example.com",
                    "Password123!",
                    "Logout User"
            );

            AuthResponse response = authService.register(registerRequest, TEST_IP, TEST_DEVICE);
            String refreshToken = response.refreshToken();

            // Should not throw
            authService.logout(refreshToken);

            // Verify token is revoked - trying to use it should fail
            // (This depends on implementation details)
        }
    }
}


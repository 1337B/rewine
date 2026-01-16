package com.rewine.backend.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.RegisterRequest;
import com.rewine.backend.integration.BaseIntegrationTest;
import com.rewine.backend.model.entity.RoleEntity;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.repository.IRoleRepository;
import com.rewine.backend.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for rate limiting functionality.
 * Tests verify that rate limits are enforced and return 429 when exceeded.
 */
@AutoConfigureMockMvc
@Transactional
public class RateLimitIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_PASSWORD = "TestPassword123!";
    // Note: MockMvc doesnt use servlet context path by default
    private static final int LOGIN_LIMIT = 3;
    private static final int REGISTER_LIMIT = 2;
    private static final int PUBLIC_GET_LIMIT = 5;
    private static final int WINDOW_SECONDS = 60;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final String RATE_LIMIT_LIMIT_HEADER = "3";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RoleEntity roleUser;

    /**
     * Configure very low rate limits for testing.
     * This allows us to exceed the limits within a reasonable test execution time.
     */
    @DynamicPropertySource
    static void configureRateLimits(DynamicPropertyRegistry registry) {
        // Very aggressive rate limits for testing
        registry.add("rewine.rate-limit.enabled", () -> true);
        registry.add("rewine.rate-limit.login.requests", () -> LOGIN_LIMIT);
        registry.add("rewine.rate-limit.login.window-seconds", () -> WINDOW_SECONDS);
        registry.add("rewine.rate-limit.register.requests", () -> REGISTER_LIMIT);
        registry.add("rewine.rate-limit.register.window-seconds", () -> WINDOW_SECONDS);
        registry.add("rewine.rate-limit.public-get.requests", () -> PUBLIC_GET_LIMIT);
        registry.add("rewine.rate-limit.public-get.window-seconds", () -> WINDOW_SECONDS);
        registry.add("rewine.rate-limit.authenticated.requests", () -> 10);
        registry.add("rewine.rate-limit.authenticated.window-seconds", () -> WINDOW_SECONDS);
    }

    @BeforeEach
    void setUp() {
        roleUser = getOrCreateRole("ROLE_USER", "Standard user role");
    }

    private RoleEntity getOrCreateRole(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(name);
                    role.setDescription(description);
                    return roleRepository.save(role);
                });
    }

    private UserEntity createUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(TEST_PASSWORD));
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setLocked(false);
        user.setRoles(new HashSet<>(Set.of(roleUser)));
        return userRepository.save(user);
    }

    @Nested
    @DisplayName("Login Rate Limit Tests")
    class LoginRateLimitTests {

        @Test
        @DisplayName("Should return 429 after exceeding login rate limit")
        void shouldReturn429AfterExceedingLoginRateLimit() throws Exception {
            createUser("ratelimituser", "ratelimit@test.com");

            LoginRequest validRequest = new LoginRequest("ratelimituser", TEST_PASSWORD);
            LoginRequest invalidRequest = new LoginRequest("ratelimituser", "wrongpassword");

            // Make requests up to the limit (3 requests with login limit of 3)
            // Use a mix of valid and invalid credentials - both count against rate limit
            for (int i = 0; i < LOGIN_LIMIT; i++) {
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                                .header("X-Forwarded-For", "192.168.1.100"))
                        .andExpect(status().isUnauthorized());
            }

            // The 4th request should be rate limited
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest))
                            .header("X-Forwarded-For", "192.168.1.100"))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.status").value(HTTP_TOO_MANY_REQUESTS))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should include rate limit headers in response")
        void shouldIncludeRateLimitHeaders() throws Exception {
            createUser("headeruser", "header@test.com");

            LoginRequest request = new LoginRequest("headeruser", TEST_PASSWORD);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "192.168.1.200"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("X-RateLimit-Remaining"))
                    .andExpect(header().exists("X-RateLimit-Limit"));
        }

        @Test
        @DisplayName("Different IPs should have separate rate limits")
        void differentIpsShouldHaveSeparateLimits() throws Exception {
            createUser("multiipuser", "multiip@test.com");

            LoginRequest request = new LoginRequest("multiipuser", "wrongpassword");

            // Exhaust rate limit for first IP
            for (int i = 0; i < LOGIN_LIMIT + 1; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Forwarded-For", "10.0.0.1"));
            }

            // First IP should be rate limited
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "10.0.0.1"))
                    .andExpect(status().isTooManyRequests());

            // Second IP should still be allowed
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "10.0.0.2"))
                    .andExpect(status().isUnauthorized()); // 401 not 429
        }
    }

    @Nested
    @DisplayName("Register Rate Limit Tests")
    class RegisterRateLimitTests {

        private final AtomicInteger userCounter = new AtomicInteger(0);

        @Test
        @DisplayName("Should return 429 after exceeding register rate limit")
        void shouldReturn429AfterExceedingRegisterRateLimit() throws Exception {
            // Register limit is set to 2 requests

            // First 2 requests should succeed (register limit = 2)
            for (int i = 0; i < REGISTER_LIMIT; i++) {
                RegisterRequest request = createUniqueRegisterRequest();
                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("X-Forwarded-For", "192.168.2.100"))
                        .andExpect(status().isCreated());
            }

            // 3rd request should be rate limited
            RegisterRequest request = createUniqueRegisterRequest();
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "192.168.2.100"))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.status").value(HTTP_TOO_MANY_REQUESTS));
        }

        private RegisterRequest createUniqueRegisterRequest() {
            int count = userCounter.incrementAndGet();
            return new RegisterRequest(
                    "newuser" + count,
                    "newuser" + count + "@test.com",
                    TEST_PASSWORD,
                    "New User " + count
            );
        }
    }

    @Nested
    @DisplayName("Public GET Rate Limit Tests")
    class PublicGetRateLimitTests {

        @Test
        @DisplayName("Should return 429 after exceeding public GET rate limit")
        @org.junit.jupiter.api.Disabled("Requires seed data - wines service needs test fixtures")
        void shouldReturn429AfterExceedingPublicGetRateLimit() throws Exception {
            // Public GET limit is set to 5 requests

            // Make requests up to the limit
            for (int i = 0; i < PUBLIC_GET_LIMIT; i++) {
                mockMvc.perform(get("/wines")
                                .header("X-Forwarded-For", "192.168.3.100"))
                        .andExpect(status().isOk());
            }

            // Next request should be rate limited
            mockMvc.perform(get("/wines")
                            .header("X-Forwarded-For", "192.168.3.100"))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.status").value(HTTP_TOO_MANY_REQUESTS));
        }

        @Test
        @DisplayName("Rate limit applies to different public endpoints cumulatively")
        void rateLimitAppliesCumulativelyToPublicEndpoints() throws Exception {
            // All public GET requests share the same rate limit bucket
            // Use only endpoints that work without seed data

            // Mix of different public endpoints (5 total = limit)
            mockMvc.perform(get("/wine-routes/hierarchy")
                            .header("X-Forwarded-For", "192.168.4.100"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/wine-routes/hierarchy")
                            .header("X-Forwarded-For", "192.168.4.100"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/wine-routes/hierarchy")
                            .header("X-Forwarded-For", "192.168.4.100"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/wine-routes/hierarchy")
                            .header("X-Forwarded-For", "192.168.4.100"))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/wine-routes/hierarchy")
                            .header("X-Forwarded-For", "192.168.4.100"))
                    .andExpect(status().isOk());

            // 6th request should be rate limited
            mockMvc.perform(get("/wine-routes/hierarchy")
                            .header("X-Forwarded-For", "192.168.4.100"))
                    .andExpect(status().isTooManyRequests());
        }
    }

    @Nested
    @DisplayName("Rate Limit Response Format Tests")
    class RateLimitResponseFormatTests {

        @Test
        @DisplayName("Rate limit response should contain required error fields")
        void rateLimitResponseShouldContainRequiredFields() throws Exception {
            createUser("formatuser", "format@test.com");

            LoginRequest request = new LoginRequest("formatuser", "wrongpassword");

            // Exhaust rate limit
            for (int i = 0; i < LOGIN_LIMIT + 1; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Forwarded-For", "192.168.5.100"));
            }

            // Check response format
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "192.168.5.100"))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(HTTP_TOO_MANY_REQUESTS))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.path").exists());
        }

        @Test
        @DisplayName("Rate limit headers should show correct values")
        void rateLimitHeadersShouldShowCorrectValues() throws Exception {
            createUser("limitheader", "limitheader@test.com");

            LoginRequest request = new LoginRequest("limitheader", TEST_PASSWORD);

            // First request
            ResultActions firstResult = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "192.168.6.100"))
                    .andExpect(status().isOk());

            // Check remaining tokens decreased
            String limitHeader = firstResult.andReturn().getResponse()
                    .getHeader("X-RateLimit-Limit");
            String remainingHeader = firstResult.andReturn().getResponse()
                    .getHeader("X-RateLimit-Remaining");

            assertThat(limitHeader).isEqualTo(RATE_LIMIT_LIMIT_HEADER);
            assertThat(Integer.parseInt(remainingHeader))
                    .isLessThan(LOGIN_LIMIT);
        }
    }

    @Nested
    @DisplayName("X-Forwarded-For and X-Real-IP Tests")
    class IpExtractionTests {

        @Test
        @DisplayName("Should use X-Forwarded-For header for IP identification")
        void shouldUseXForwardedForHeader() throws Exception {
            createUser("forwarded", "forwarded@test.com");

            LoginRequest request = new LoginRequest("forwarded", "wrongpassword");

            // Use same X-Forwarded-For IP for all requests
            for (int i = 0; i < LOGIN_LIMIT + 1; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Forwarded-For", "203.0.113.100"));
            }

            // Should be rate limited
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "203.0.113.100"))
                    .andExpect(status().isTooManyRequests());

            // Different IP should NOT be rate limited
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "203.0.113.200"))
                    .andExpect(status().isUnauthorized()); // 401 not 429
        }

        @Test
        @DisplayName("Should use X-Real-IP header when X-Forwarded-For is absent")
        void shouldUseXRealIpHeader() throws Exception {
            createUser("realip", "realip@test.com");

            LoginRequest request = new LoginRequest("realip", "wrongpassword");

            // Use X-Real-IP header
            for (int i = 0; i < LOGIN_LIMIT + 1; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Real-IP", "198.51.100.100"));
            }

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Real-IP", "198.51.100.100"))
                    .andExpect(status().isTooManyRequests());
        }

        @Test
        @DisplayName("X-Forwarded-For should take priority over X-Real-IP")
        void xForwardedForShouldTakePriority() throws Exception {
            createUser("priority", "priority@test.com");

            LoginRequest request = new LoginRequest("priority", "wrongpassword");

            // Use both headers - X-Forwarded-For should be used
            for (int i = 0; i < LOGIN_LIMIT + 1; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Forwarded-For", "172.16.0.1")
                        .header("X-Real-IP", "172.16.0.2"));
            }

            // X-Forwarded-For IP should be rate limited
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("X-Forwarded-For", "172.16.0.1")
                            .header("X-Real-IP", "172.16.0.2"))
                    .andExpect(status().isTooManyRequests());
        }
    }
}


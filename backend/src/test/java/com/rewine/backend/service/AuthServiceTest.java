package com.rewine.backend.service;

import com.rewine.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for AuthService.
 * This is a minimal test to verify the test pipeline works.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should create AuthService instance")
    void shouldCreateAuthServiceInstance() {
        assertNotNull(authService, "AuthService should not be null");
    }
}


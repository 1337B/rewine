package com.rewine.backend.utils;

import com.rewine.backend.utils.validation.impl.ValidationUtilsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ValidationUtils.
 */
@DisplayName("ValidationUtils Tests")
class ValidationUtilsTest {

    private ValidationUtilsImpl validationUtils;

    @BeforeEach
    void setUp() {
        validationUtils = new ValidationUtilsImpl();
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {

        @Test
        @DisplayName("Should return true for valid email")
        void shouldReturnTrueForValidEmail() {
            assertTrue(validationUtils.isValidEmail("test@example.com"));
            assertTrue(validationUtils.isValidEmail("user.name@domain.org"));
            assertTrue(validationUtils.isValidEmail("user+tag@example.co.uk"));
        }

        @Test
        @DisplayName("Should return false for invalid email")
        void shouldReturnFalseForInvalidEmail() {
            assertFalse(validationUtils.isValidEmail(null));
            assertFalse(validationUtils.isValidEmail(""));
            assertFalse(validationUtils.isValidEmail("   "));
            assertFalse(validationUtils.isValidEmail("invalid"));
            assertFalse(validationUtils.isValidEmail("@domain.com"));
        }
    }

    @Nested
    @DisplayName("Password Validation")
    class PasswordValidation {

        @Test
        @DisplayName("Should return true for valid password")
        void shouldReturnTrueForValidPassword() {
            assertTrue(validationUtils.isValidPassword("password123"));
            assertTrue(validationUtils.isValidPassword("12345678"));
        }

        @Test
        @DisplayName("Should return false for invalid password")
        void shouldReturnFalseForInvalidPassword() {
            assertFalse(validationUtils.isValidPassword(null));
            assertFalse(validationUtils.isValidPassword("short"));
            assertFalse(validationUtils.isValidPassword("1234567"));
        }
    }

    @Nested
    @DisplayName("UUID Validation")
    class UuidValidation {

        @Test
        @DisplayName("Should return true for valid UUID")
        void shouldReturnTrueForValidUuid() {
            assertTrue(validationUtils.isValidUuid("550e8400-e29b-41d4-a716-446655440000"));
            assertTrue(validationUtils.isValidUuid("6ba7b810-9dad-11d1-80b4-00c04fd430c8"));
        }

        @Test
        @DisplayName("Should return false for invalid UUID")
        void shouldReturnFalseForInvalidUuid() {
            assertFalse(validationUtils.isValidUuid(null));
            assertFalse(validationUtils.isValidUuid(""));
            assertFalse(validationUtils.isValidUuid("not-a-uuid"));
            assertFalse(validationUtils.isValidUuid("550e8400-e29b-41d4-a716"));
        }
    }

    @Nested
    @DisplayName("NotBlank Validation")
    class NotBlankValidation {

        @Test
        @DisplayName("Should return true for non-blank strings")
        void shouldReturnTrueForNonBlankStrings() {
            assertTrue(validationUtils.isNotBlank("hello"));
            assertTrue(validationUtils.isNotBlank("  hello  "));
        }

        @Test
        @DisplayName("Should return false for blank strings")
        void shouldReturnFalseForBlankStrings() {
            assertFalse(validationUtils.isNotBlank(null));
            assertFalse(validationUtils.isNotBlank(""));
            assertFalse(validationUtils.isNotBlank("   "));
        }
    }
}


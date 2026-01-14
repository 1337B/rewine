package com.rewine.backend.utils.validation.impl;

import com.rewine.backend.utils.validation.IValidationUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validation utilities implementation.
 */
@Component
public class ValidationUtilsImpl implements IValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    public boolean isValidEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPassword(String password) {
        if (Objects.isNull(password)) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    @Override
    public boolean isValidUuid(String uuid) {
        if (Objects.isNull(uuid) || uuid.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean isNotBlank(String value) {
        return Objects.nonNull(value) && !value.isBlank();
    }
}


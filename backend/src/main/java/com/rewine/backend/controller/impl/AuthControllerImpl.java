package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IAuthController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints implementation.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthControllerImpl implements IAuthController {

    // Placeholder implementation
    // Authentication endpoints will be implemented here
}


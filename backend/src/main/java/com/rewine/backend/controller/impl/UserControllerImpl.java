package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IUserController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User endpoints implementation.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserControllerImpl implements IUserController {

    // Placeholder implementation
    // User endpoints will be implemented here
}


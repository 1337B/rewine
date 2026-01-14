package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IWineController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wine endpoints implementation.
 */
@RestController
@RequestMapping("/wines")
@Tag(name = "Wines", description = "Wine catalog management endpoints")
public class WineControllerImpl implements IWineController {

    // Placeholder implementation
    // Wine endpoints will be implemented here
}


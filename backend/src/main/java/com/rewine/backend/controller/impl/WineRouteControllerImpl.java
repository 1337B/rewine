package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IWineRouteController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wine route endpoints implementation.
 */
@RestController
@RequestMapping("/wine-routes")
@Tag(name = "Wine Routes", description = "Wine route management endpoints")
public class WineRouteControllerImpl implements IWineRouteController {

    // Placeholder implementation
    // Wine route endpoints will be implemented here
}


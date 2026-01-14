package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IEventController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Event endpoints implementation.
 */
@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Wine event management endpoints")
public class EventControllerImpl implements IEventController {

    // Placeholder implementation
    // Event endpoints will be implemented here
}


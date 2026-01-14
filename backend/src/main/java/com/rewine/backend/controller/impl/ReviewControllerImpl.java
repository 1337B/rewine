package com.rewine.backend.controller.impl;

import com.rewine.backend.controller.IReviewController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Review endpoints implementation.
 */
@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Wine review management endpoints")
public class ReviewControllerImpl implements IReviewController {

    // Placeholder implementation
    // Review endpoints will be implemented here
}


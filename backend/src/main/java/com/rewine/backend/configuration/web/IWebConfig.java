package com.rewine.backend.configuration.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * Interface for web configuration.
 */
public interface IWebConfig {

    /**
     * Returns the allowed CORS origins.
     *
     * @return Array of allowed origins
     */
    String[] getAllowedOrigins();

    /**
     * Returns the allowed HTTP methods.
     *
     * @return Array of allowed HTTP methods
     */
    String[] getAllowedMethods();

    /**
     * Creates and configures the request logging filter registration.
     *
     * @return FilterRegistrationBean for the request logging filter
     */
    FilterRegistrationBean<?> requestLoggingFilterRegistration();
}


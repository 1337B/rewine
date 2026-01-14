package com.rewine.backend.configuration.web.impl;

import com.rewine.backend.configuration.web.IWebConfig;
import com.rewine.backend.utils.logging.IRequestLoggingFilter;
import com.rewine.backend.utils.logging.impl.RequestLoggingFilterImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration implementation.
 */
@Configuration
public class WebConfigImpl implements IWebConfig, WebMvcConfigurer {

    private final RequestLoggingFilterImpl requestLoggingFilter;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    public WebConfigImpl(RequestLoggingFilterImpl requestLoggingFilter) {
        this.requestLoggingFilter = requestLoggingFilter;
    }

    @Override
    public String[] getAllowedOrigins() {
        return allowedOrigins.split(",");
    }

    @Override
    public String[] getAllowedMethods() {
        return allowedMethods.split(",");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(getAllowedOrigins())
                .allowedMethods(getAllowedMethods())
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .exposedHeaders(IRequestLoggingFilter.REQUEST_ID_HEADER);
    }

    @Bean
    @Override
    public FilterRegistrationBean<RequestLoggingFilterImpl> requestLoggingFilterRegistration() {
        FilterRegistrationBean<RequestLoggingFilterImpl> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(requestLoggingFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setName("requestLoggingFilter");
        return registrationBean;
    }
}


package com.rewine.backend.configuration.web.impl;

import com.rewine.backend.configuration.properties.CorsProperties;
import com.rewine.backend.configuration.web.IWebConfig;
import com.rewine.backend.utils.logging.impl.RequestLoggingFilterImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * Web configuration implementation.
 * Configures CORS and request logging with environment-specific settings.
 */
@Slf4j
@Configuration
public class WebConfigImpl implements IWebConfig, WebMvcConfigurer {

    private final RequestLoggingFilterImpl requestLoggingFilter;
    private final CorsProperties corsProperties;

    public WebConfigImpl(RequestLoggingFilterImpl requestLoggingFilter, CorsProperties corsProperties) {
        this.requestLoggingFilter = requestLoggingFilter;
        this.corsProperties = corsProperties;

        log.info("CORS configuration initialized - enabled: {}, origins: {}, credentials: {}",
                corsProperties.isEnabled(),
                corsProperties.getAllowedOrigins(),
                corsProperties.isAllowCredentials());

        // Warn if wildcard is used (security concern in production)
        if (corsProperties.getAllowedOrigins().contains("*")) {
            log.warn("CORS is configured with wildcard origin (*). This is NOT recommended for production!");
        }
    }

    @Override
    public String[] getAllowedOrigins() {
        return corsProperties.getAllowedOriginsArray();
    }

    @Override
    public String[] getAllowedMethods() {
        return corsProperties.getAllowedMethodsArray();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (!corsProperties.isEnabled()) {
            return;
        }

        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOriginsArray())
                .allowedMethods(corsProperties.getAllowedMethodsArray())
                .allowedHeaders(corsProperties.getAllowedHeadersArray())
                .exposedHeaders(corsProperties.getExposedHeadersArray())
                .allowCredentials(corsProperties.isAllowCredentials())
                .maxAge(corsProperties.getMaxAge());
    }

    /**
     * CORS configuration source for Spring Security.
     * This is used by the security filter chain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if (corsProperties.isEnabled()) {
            log.info("CORS is enabled - configuring with origins: {}", corsProperties.getAllowedOrigins());
            // Use patterns instead of explicit origins for more flexibility
            configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
            configuration.setAllowedMethods(corsProperties.getAllowedMethods());
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setExposedHeaders(corsProperties.getExposedHeaders());
            configuration.setAllowCredentials(corsProperties.isAllowCredentials());
            configuration.setMaxAge(corsProperties.getMaxAge());
        } else {
            log.warn("CORS is disabled - applying permissive defaults");
            configuration.setAllowedOriginPatterns(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowCredentials(false);
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * CORS filter bean for explicit CORS handling.
     * This ensures CORS headers are applied before security filters.
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    /**
     * Register the CORS filter with highest priority to ensure it runs first.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
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


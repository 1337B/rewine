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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


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
            configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
            configuration.setAllowedMethods(corsProperties.getAllowedMethods());
            configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
            configuration.setExposedHeaders(corsProperties.getExposedHeaders());
            configuration.setAllowCredentials(corsProperties.isAllowCredentials());
            configuration.setMaxAge(corsProperties.getMaxAge());
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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


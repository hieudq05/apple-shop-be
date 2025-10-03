package com.web.appleshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures web-related settings for the Spring MVC framework.
 * <p>
 * This class implements {@link WebMvcConfigurer} to customize the default
 * Spring MVC configuration. It is primarily used to define resource handlers
 * for serving static content, such as uploaded files.
 */
@Configuration
class WebConfig implements WebMvcConfigurer {

    /**
     * Configures resource handlers to serve static files.
     * <p>
     * This method maps the URL pattern {@code /uploads/**} to the physical
     * directory {@code uploads/} located in the application's root directory.
     * This allows clients to access files stored in this directory via HTTP.
     *
     * @param registry The registry for resource handlers.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = "file:" + System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
    }
}
package com.hometail.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class that customizes Spring MVC settings.
 * Implements {@link WebMvcConfigurer} to provide custom resource handling
 * configurations for the application.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures resource handlers for serving static resources.
     * <p>
     * This method sets up a resource handler for serving uploaded files from the
     * filesystem. Files stored in the 'uploads' directory in the application's
     * working directory will be accessible via the '/uploads/**' URL pattern.
     *
     * @param registry the ResourceHandlerRegistry to configure
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the '/uploads/**' URL pattern to the 'uploads' directory in the application's working directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }
}

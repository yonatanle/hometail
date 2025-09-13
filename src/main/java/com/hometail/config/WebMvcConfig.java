package com.hometail.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for customizing Spring MVC message converters.
 * <p>
 * This configuration ensures proper handling of JSON serialization/deserialization,
 * particularly for Java 8 date/time types, across all controller endpoints.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configures the message converters used for request/response body conversion.
     * <p>
     * This method:
     * <ul>
     *     <li>Registers the JavaTimeModule for proper Java 8 date/time type support</li>
     *     <li>Disables the default timestamp-based date serialization in favor of ISO-8601 format</li>
     *     <li>Adds a custom MappingJackson2HttpMessageConverter with the configured ObjectMapper</li>
     * </ul>
     *
     * @param converters the list of configured converters to be extended
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Create and configure ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register Java 8 date/time support
        objectMapper.registerModule(new JavaTimeModule());
        
        // Use ISO-8601 format for dates instead of timestamps
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Create and configure the JSON message converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        
        // Add our custom converter to the beginning of the converters list
        // to ensure it takes precedence over default converters
        converters.add(0, converter);
    }
}
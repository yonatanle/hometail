package com.hometail.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for customizing Jackson's ObjectMapper settings.
 * This configuration ensures proper handling of Java 8 date/time types and
 * provides a more lenient deserialization approach for unknown properties.
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures the primary ObjectMapper bean for the application.
     * This configuration:
     * <ul>
     *     <li>Registers the JavaTimeModule for proper Java 8 date/time type support</li>
     *     <li>Disables writing dates as timestamps, using ISO-8601 format instead</li>
     *     <li>Configures the mapper to ignore unknown properties during deserialization</li>
     * </ul>
     *
     * @return A configured ObjectMapper instance
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java 8 date/time support
        mapper.registerModule(new JavaTimeModule());
        
        // Use ISO-8601 format for dates instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Ignore unknown properties in JSON input instead of failing
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return mapper;
    }
}

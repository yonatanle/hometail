package com.hometail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the HomeTail pet adoption platform.
 * 
 * <p>This is the entry point of the Spring Boot application. It enables auto-configuration,
 * component scanning, and defines the configuration class for the application.</p>
 *
 * <p>The application provides a platform for pet adoption, allowing users to browse available
 * animals, view their details, and facilitate the adoption process. It includes features
 * for user management, animal catalog, and administrative functions.</p>
 *
 * @SpringBootApplication is a convenience annotation that adds all of the following:
 * <ul>
 *   <li>@Configuration: Tags the class as a source of bean definitions</li>
 *   <li>@EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings</li>
 *   <li>@ComponentScan: Tells Spring to look for other components, configurations, and services</li>
 * </ul>
 *
 * @since 1.0
 */
@SpringBootApplication
public class HomeTailApplication {

    /**
     * Main method which serves as the entry point of the application.
     * 
     * <p>This method bootstraps the Spring Boot application by creating an application context
     * and starting all the necessary components. It enables auto-configuration and component
     * scanning as defined by the {@code @SpringBootApplication} annotation.</p>
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(HomeTailApplication.class, args);
    }

}

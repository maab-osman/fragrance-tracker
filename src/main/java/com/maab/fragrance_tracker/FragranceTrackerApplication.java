package com.maab.fragrance_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the Fragrance Tracker Spring Boot application.
 * 
 * This application manages a personal fragrance collection with recommendation features,
 * user authentication, and a community review system. It uses Spring Data JPA for
 * database operations and includes caching for performance optimization.
 * 
 * @author Maab Osman
 * @version 1.0
 */
@SpringBootApplication
@EnableCaching // Advanced Spring feature: Enables method-level caching
public class FragranceTrackerApplication {

	/**
	 * Application entry point.
	 * 
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(FragranceTrackerApplication.class, args);
	}

}

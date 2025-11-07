package com.sunbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot Application class for Sun Booking Tour
 * 
 * This application provides:
 * - Admin Site: MVC application with Thymeleaf for managing the platform
 * - User API: REST API for client-side applications
 */
@SpringBootApplication
@EnableJpaAuditing
public class SunBookingTourApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunBookingTourApplication.class, args);
    }
}

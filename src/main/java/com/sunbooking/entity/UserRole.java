package com.sunbooking.entity;

/**
 * Enum representing user roles in the system.
 * 
 * USER: Regular user with booking and review capabilities
 * ADMIN: Administrator with full system access
 */
public enum UserRole {
    /**
     * Regular user role - can book tours, write reviews, and manage own content
     */
    USER,

    /**
     * Administrator role - full system access including user management,
     * content moderation, and system configuration
     */
    ADMIN
}

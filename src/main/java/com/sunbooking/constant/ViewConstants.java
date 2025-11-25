package com.sunbooking.constant;

/**
 * Constants for view template paths.
 * Centralizes all view names to avoid hardcoding strings in controllers.
 */
public final class ViewConstants {

    private ViewConstants() {
        // Prevent instantiation
    }

    // Admin - User Management
    public static final String ADMIN_USERS_LIST = "admin/users/list";
    public static final String ADMIN_USERS_DETAIL = "admin/users/detail";
    public static final String ADMIN_USERS_FORM = "admin/users/form";

    // Admin - Admin Management
    public static final String ADMIN_ADMINS_LIST = "admin/admins/list";
    public static final String ADMIN_ADMINS_DETAIL = "admin/admins/detail";
    public static final String ADMIN_ADMINS_FORM = "admin/admins/form";

    // Admin - Category Management
    public static final String ADMIN_CATEGORIES_LIST = "admin/categories/list";
    public static final String ADMIN_CATEGORIES_FORM = "admin/categories/form";

    // Admin - Tour Management
    public static final String ADMIN_TOURS_LIST = "admin/tours/list";
    public static final String ADMIN_TOURS_DETAIL = "admin/tours/detail";
    public static final String ADMIN_TOURS_FORM = "admin/tours/form";

    // Admin - Booking Management
    public static final String ADMIN_BOOKINGS_LIST = "admin/bookings/list";
    public static final String ADMIN_BOOKINGS_DETAIL = "admin/bookings/detail";
    public static final String ADMIN_BOOKINGS_UPDATE_STATUS = "admin/bookings/update-status";

    // Admin - Review Management
    public static final String ADMIN_REVIEWS_LIST = "admin/reviews/list";
    public static final String ADMIN_REVIEWS_DETAIL = "admin/reviews/detail";

    // Admin - Dashboard
    public static final String ADMIN_DASHBOARD = "admin/dashboard";

    // Admin - Authentication
    public static final String ADMIN_LOGIN = "admin/auth/login";

    // Error Pages
    public static final String ERROR_400 = "error/400";
    public static final String ERROR_401 = "error/401";
    public static final String ERROR_403 = "error/403";
    public static final String ERROR_404 = "error/404";
    public static final String ERROR_500 = "error/500";

    // Redirect URLs
    public static final String REDIRECT_ADMIN_DASHBOARD = "redirect:/admin/dashboard";
    public static final String REDIRECT_ADMIN_USERS = "redirect:/admin/users";
    public static final String REDIRECT_ADMIN_ADMINS = "redirect:/admin/admins";
    public static final String REDIRECT_ADMIN_CATEGORIES = "redirect:/admin/categories";
    public static final String REDIRECT_ADMIN_TOURS = "redirect:/admin/tours";
    public static final String REDIRECT_ADMIN_BOOKINGS = "redirect:/admin/bookings";
    public static final String REDIRECT_ADMIN_REVIEWS = "redirect:/admin/reviews";
    public static final String REDIRECT_ADMIN_LOGIN = "redirect:/admin/login";
}

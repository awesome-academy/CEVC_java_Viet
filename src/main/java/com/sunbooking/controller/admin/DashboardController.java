package com.sunbooking.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunbooking.constant.ViewConstants;
import com.sunbooking.security.CustomUserDetails;
import com.sunbooking.service.admin.DashboardService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for admin dashboard.
 * Displays statistics, charts, and recent activities.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Display admin dashboard with statistics and recent activities.
     *
     * @param model       model to hold view data
     * @param userDetails authenticated user details
     * @return dashboard view
     */
    @GetMapping({ "/dashboard", "/" })
    public String showDashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Admin {} accessed dashboard", userDetails.getUsername());

        try {
            // Summary statistics
            model.addAttribute("totalUsers", dashboardService.getTotalUsers());
            model.addAttribute("totalBookings", dashboardService.getTotalBookings());
            model.addAttribute("pendingBookings", dashboardService.getPendingBookings());
            model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());

            // Charts data
            model.addAttribute("monthlyRevenue", dashboardService.getMonthlyRevenue());
            model.addAttribute("revenueBreakdown", dashboardService.getRevenueBreakdown());

            // Recent activities
            model.addAttribute("recentBookings", dashboardService.getRecentBookings(10));
            model.addAttribute("recentReviews", dashboardService.getRecentReviews(10));

            // System status
            model.addAttribute("activeTours", dashboardService.getActiveTours());
            model.addAttribute("totalCategories", dashboardService.getTotalCategories());
            model.addAttribute("pendingPayments", dashboardService.getPendingPayments());

            // User info
            model.addAttribute("username", userDetails.getUsername());

            log.debug("Dashboard data loaded successfully");
            return ViewConstants.ADMIN_DASHBOARD;

        } catch (Exception e) {
            log.error("Error loading dashboard data", e);
            model.addAttribute("error", "Failed to load dashboard data");
            return ViewConstants.ADMIN_DASHBOARD;
        }
    }
}

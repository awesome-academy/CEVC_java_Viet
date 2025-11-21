package com.sunbooking.controller.admin;

import static com.sunbooking.constant.ViewConstants.ADMIN_TOURS_DETAIL;
import static com.sunbooking.constant.ViewConstants.ADMIN_TOURS_FORM;
import static com.sunbooking.constant.ViewConstants.ADMIN_TOURS_LIST;
import static com.sunbooking.constant.ViewConstants.REDIRECT_ADMIN_TOURS;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunbooking.dto.admin.tour.TourDTO;
import com.sunbooking.dto.admin.tour.TourForm;
import com.sunbooking.dto.admin.tour.TourListDTO;
import com.sunbooking.dto.admin.tour.TourSearchCriteria;
import com.sunbooking.dto.admin.tour.TourStatisticsDTO;
import com.sunbooking.service.admin.TourManagementService;

/**
 * Controller for managing tours in admin panel.
 * Handles CRUD operations, search, and tour statistics.
 */
@Controller
@RequestMapping("/admin/tours")
public class TourManagementController {

    private static final Logger logger = LoggerFactory.getLogger(TourManagementController.class);

    @Autowired
    private TourManagementService tourManagementService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Display list of all tours with search and filter options.
     */
    @GetMapping
    public String listTours(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            Model model) {

        logger.debug("Listing tours - keyword: {}, status: {}, page: {}", keyword, status, page);

        TourSearchCriteria criteria = new TourSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setStatus(status);
        criteria.setMinPrice(minPrice);
        criteria.setMaxPrice(maxPrice);
        criteria.setPage(page);
        criteria.setSize(size);
        criteria.setSortBy(sortBy);
        criteria.setSortDir(sortDir);

        Page<TourListDTO> tours = tourManagementService.getAllTours(criteria);

        model.addAttribute("tours", tours.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tours.getTotalPages());
        model.addAttribute("totalItems", tours.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("ASC") ? "DESC" : "ASC");
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return ADMIN_TOURS_LIST;
    }

    /**
     * Display tour details with statistics.
     */
    @GetMapping("/{id}")
    public String viewTour(@PathVariable Long id, Model model) {
        logger.debug("Viewing tour detail for id: {}", id);

        TourDTO tour = tourManagementService.getTourById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        TourStatisticsDTO statistics = tourManagementService.getTourStatistics(id);

        model.addAttribute("tour", tour);
        model.addAttribute("statistics", statistics);

        return ADMIN_TOURS_DETAIL;
    }

    /**
     * Show form for creating a new tour.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.debug("Showing create tour form");
        model.addAttribute("tourForm", new TourForm());
        return ADMIN_TOURS_FORM;
    }

    /**
     * Handle tour creation.
     */
    @PostMapping("/create")
    public String createTour(
            @Valid @ModelAttribute("tourForm") TourForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        logger.debug("Creating tour with title: {}", form.getTitle());

        if (result.hasErrors()) {
            logger.warn("Validation errors in tour form: {}", result.getAllErrors());
            return ADMIN_TOURS_FORM;
        }

        TourDTO createdTour = tourManagementService.createTour(form);
        addSuccessMessage(redirectAttributes, "success.tour.created");
        return "redirect:/admin/tours/" + createdTour.getId();
    }

    /**
     * Show form for editing an existing tour.
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Showing edit form for tour id: {}", id);

        TourDTO tour = tourManagementService.getTourById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        TourForm form = TourForm.fromDTO(tour);
        model.addAttribute("tourForm", form);
        model.addAttribute("tourId", id);

        return ADMIN_TOURS_FORM;
    }

    /**
     * Handle tour update.
     */
    @PostMapping("/{id}/edit")
    public String updateTour(
            @PathVariable Long id,
            @Valid @ModelAttribute("tourForm") TourForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.debug("Updating tour id: {}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors in tour form: {}", result.getAllErrors());
            model.addAttribute("tourId", id);
            return ADMIN_TOURS_FORM;
        }

        tourManagementService.updateTour(id, form);
        addSuccessMessage(redirectAttributes, "success.tour.updated");
        return "redirect:/admin/tours/" + id;
    }

    /**
     * Handle tour soft deletion (deactivation).
     */
    @PostMapping("/{id}/delete")
    public String deleteTour(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete tour id: {}", id);

        try {
            tourManagementService.deleteTour(id);
            addSuccessMessage(redirectAttributes, "success.tour.deleted");
        } catch (IllegalStateException e) {
            logger.warn("Cannot delete tour {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return REDIRECT_ADMIN_TOURS;
    }

    /**
     * Handle tour activation.
     */
    @PostMapping("/{id}/activate")
    public String activateTour(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Activating tour id: {}", id);

        tourManagementService.activateTour(id);
        addSuccessMessage(redirectAttributes, "success.tour.activated");

        return "redirect:/admin/tours/" + id;
    }

    /**
     * Utility method to add internationalized success messages.
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String messageKey) {
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
    }
}

package com.sunbooking.controller.admin;

import javax.validation.Valid;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunbooking.dto.admin.admin.AdminDTO;
import com.sunbooking.dto.admin.admin.AdminForm;
import com.sunbooking.dto.admin.admin.AdminListDTO;
import com.sunbooking.dto.admin.admin.AdminSearchCriteria;
import com.sunbooking.mapper.AdminMapper;
import com.sunbooking.service.admin.AdminManagementService;

/**
 * Controller for admin account management in admin panel.
 * Handles all CRUD operations with security restrictions.
 */
@Controller
@RequestMapping("/admin/admins")
public class AdminManagementController {

    private static final Logger logger = LoggerFactory.getLogger(AdminManagementController.class);

    @Autowired
    private AdminManagementService adminManagementService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Validator validator;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * List all admins with search and pagination.
     */
    @GetMapping
    public String listAdmins(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "25") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        logger.debug("Listing admins - keyword: {}, isActive: {}, page: {}", keyword, isActive, page);

        AdminSearchCriteria criteria = new AdminSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setIsActive(isActive);
        criteria.setPage(page);
        criteria.setSize(size);
        criteria.setSortBy(sortBy);
        criteria.setSortDir(sortDir);

        Page<AdminListDTO> admins = adminManagementService.getAllAdmins(criteria);
        AdminManagementService.AdminStatistics statistics = adminManagementService.getStatistics();

        model.addAttribute("admins", admins);
        model.addAttribute("statistics", statistics);
        model.addAttribute("criteria", criteria);

        return "admin/admins/list";
    }

    /**
     * Show admin details.
     */
    @GetMapping("/{id}")
    public String viewAdmin(@PathVariable Long id, Model model) {
        logger.debug("Viewing admin detail: {}", id);

        AdminDTO admin = adminManagementService.getAdminById(id);
        model.addAttribute("admin", admin);

        return "admin/admins/detail";
    }

    /**
     * Show create admin form.
     */
    @GetMapping("/create")
    public String createAdminForm(Model model) {
        logger.debug("Displaying create admin form");

        AdminForm adminForm = new AdminForm();
        adminForm.setIsActive(true); // Default to active

        model.addAttribute("adminForm", adminForm);
        model.addAttribute("isEdit", false);

        return "admin/admins/form";
    }

    /**
     * Process create admin form submission.
     */
    @PostMapping("/create")
    public String createAdmin(
            @Valid @ModelAttribute("adminForm") AdminForm adminForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.debug("Processing create admin: {}", adminForm.getEmail());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/admins/form";
        }

        AdminDTO createdAdmin = adminManagementService.createAdmin(adminForm);
        String successMessage = messageSource.getMessage("success.admin.created",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/admins/" + createdAdmin.getId();
    }

    /**
     * Show edit admin form.
     */
    @GetMapping("/{id}/edit")
    public String editAdminForm(@PathVariable Long id, Model model) {
        logger.debug("Displaying edit admin form for ID: {}", id);

        AdminDTO admin = adminManagementService.getAdminById(id);
        AdminForm adminForm = adminMapper.dtoToForm(admin);

        model.addAttribute("adminForm", adminForm);
        model.addAttribute("isEdit", true);

        return "admin/admins/form";
    }

    /**
     * Process edit admin form submission.
     */
    @PostMapping("/{id}/edit")
    public String editAdmin(
            @PathVariable Long id,
            @ModelAttribute("adminForm") AdminForm adminForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.debug("Processing edit admin ID: {}", id);

        // Normalize password fields - convert empty strings to null
        if (adminForm.getPassword() != null && adminForm.getPassword().trim().isEmpty()) {
            adminForm.setPassword(null);
        }
        if (adminForm.getConfirmPassword() != null && adminForm.getConfirmPassword().trim().isEmpty()) {
            adminForm.setConfirmPassword(null);
        }

        // Manually trigger bean validation (includes @PasswordMatches check)
        SpringValidatorAdapter validatorAdapter = new SpringValidatorAdapter(validator);
        validatorAdapter.validate(adminForm, bindingResult);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("isEdit", true);
            return "admin/admins/form";
        }

        adminManagementService.updateAdmin(id, adminForm);
        String successMessage = messageSource.getMessage("success.admin.updated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/admins/" + id;
    }

    /**
     * Soft delete an admin.
     */
    @PostMapping("/{id}/delete")
    public String deleteAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Deleting admin ID: {}", id);

        adminManagementService.deleteAdmin(id);
        String successMessage = messageSource.getMessage("success.admin.deactivated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:/admin/admins";
    }

    /**
     * Reactivate a soft-deleted admin.
     */
    @PostMapping("/{id}/activate")
    public String activateAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Activating admin ID: {}", id);

        adminManagementService.activateAdmin(id);
        String successMessage = messageSource.getMessage("success.admin.activated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:/admin/admins/" + id;
    }
}

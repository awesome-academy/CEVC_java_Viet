package com.sunbooking.controller.admin;

import static com.sunbooking.constant.ViewConstants.ADMIN_USERS_DETAIL;
import static com.sunbooking.constant.ViewConstants.ADMIN_USERS_FORM;
import static com.sunbooking.constant.ViewConstants.ADMIN_USERS_LIST;
import static com.sunbooking.constant.ViewConstants.REDIRECT_ADMIN_USERS;

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

import com.sunbooking.dto.admin.user.UserDTO;
import com.sunbooking.dto.admin.user.UserForm;
import com.sunbooking.dto.admin.user.UserListDTO;
import com.sunbooking.dto.admin.user.UserSearchCriteria;
import com.sunbooking.entity.UserRole;
import com.sunbooking.mapper.UserMapper;
import com.sunbooking.service.admin.UserManagementService;

/**
 * Controller for user management in admin panel.
 * Handles all CRUD operations for users.
 */
@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Validator validator;

    @Autowired
    private UserMapper userMapper;

    /**
     * List all users with search and pagination.
     *
     * @param keyword  search keyword (optional)
     * @param role     role filter (optional)
     * @param isActive active status filter (optional)
     * @param page     page number (default: 0)
     * @param size     page size (default: 25)
     * @param sortBy   sort field (default: createdAt)
     * @param sortDir  sort direction (default: desc)
     * @param model    the model
     * @return view name
     */
    @GetMapping
    public String listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "25") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        logger.debug("Listing users - keyword: {}, role: {}, isActive: {}, page: {}", keyword, role, isActive, page);

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setRole(role);
        criteria.setIsActive(isActive);
        criteria.setPage(page);
        criteria.setSize(size);
        criteria.setSortBy(sortBy);
        criteria.setSortDir(sortDir);

        Page<UserListDTO> users = userManagementService.getAllUsers(criteria);
        UserManagementService.UserStatistics statistics = userManagementService.getStatistics();

        model.addAttribute("users", users);
        model.addAttribute("statistics", statistics);
        model.addAttribute("criteria", criteria);

        return ADMIN_USERS_LIST;
    }

    /**
     * Show user details.
     *
     * @param id    the user ID
     * @param model the model
     * @return view name
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        logger.debug("Viewing user detail: {}", id);

        UserDTO user = userManagementService.getUserById(id);
        model.addAttribute("user", user);

        return ADMIN_USERS_DETAIL;
    }

    /**
     * Show edit user form.
     *
     * @param id    the user ID
     * @param model the model
     * @return view name
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        logger.debug("Displaying edit user form for ID: {}", id);

        UserDTO user = userManagementService.getUserById(id);
        UserForm userForm = userMapper.dtoToForm(user);

        model.addAttribute("userForm", userForm);
        model.addAttribute("isEdit", true);

        return ADMIN_USERS_FORM;
    }

    /**
     * Process edit user form submission.
     *
     * @param id                 the user ID
     * @param userForm           the user form
     * @param bindingResult      binding result
     * @param redirectAttributes redirect attributes
     * @param model              the model
     * @return redirect URL or view name
     */
    @PostMapping("/{id}/edit")
    public String editUser(
            @PathVariable Long id,
            @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.debug("Processing edit user ID: {}", id);

        // Force role to USER (cannot be changed) - must be set before validation
        userForm.setRole(UserRole.USER);

        // Normalize password fields - convert empty strings to null
        if (userForm.getPassword() != null && userForm.getPassword().trim().isEmpty()) {
            userForm.setPassword(null);
        }
        if (userForm.getConfirmPassword() != null && userForm.getConfirmPassword().trim().isEmpty()) {
            userForm.setConfirmPassword(null);
        }

        // Manually trigger bean validation (includes @PasswordMatches check)
        SpringValidatorAdapter validatorAdapter = new SpringValidatorAdapter(validator);
        validatorAdapter.validate(userForm, bindingResult);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("isEdit", true);
            return ADMIN_USERS_FORM;
        }

        userManagementService.updateUser(id, userForm);
        String successMessage = messageSource.getMessage("success.user.updated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/users/" + id;
    }

    /**
     * Soft delete a user.
     *
     * @param id                 the user ID
     * @param redirectAttributes redirect attributes
     * @return redirect URL
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Deleting user ID: {}", id);

        userManagementService.deleteUser(id);
        String successMessage = messageSource.getMessage("success.user.deactivated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return REDIRECT_ADMIN_USERS;
    }

    /**
     * Reactivate a soft-deleted user.
     *
     * @param id                 the user ID
     * @param redirectAttributes redirect attributes
     * @return redirect URL
     */
    @PostMapping("/{id}/activate")
    public String activateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Activating user ID: {}", id);

        userManagementService.activateUser(id);
        String successMessage = messageSource.getMessage("success.user.activated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:/admin/users/" + id;
    }
}

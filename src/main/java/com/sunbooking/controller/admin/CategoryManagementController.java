package com.sunbooking.controller.admin;

import static com.sunbooking.constant.ViewConstants.ADMIN_CATEGORIES_FORM;
import static com.sunbooking.constant.ViewConstants.ADMIN_CATEGORIES_LIST;
import static com.sunbooking.constant.ViewConstants.REDIRECT_ADMIN_CATEGORIES;

import javax.validation.Valid;

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

import com.sunbooking.dto.admin.category.CategoryDTO;
import com.sunbooking.dto.admin.category.CategoryForm;
import com.sunbooking.dto.admin.category.CategoryListDTO;
import com.sunbooking.dto.admin.category.CategorySearchCriteria;
import com.sunbooking.entity.CategoryType;
import com.sunbooking.service.admin.CategoryManagementService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryManagementController {

    @Autowired
    private CategoryManagementService categoryService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Add category types to model for all requests.
     * This method is called before each handler method.
     */
    @ModelAttribute("categoryTypes")
    public CategoryType[] getCategoryTypes() {
        return CategoryType.values();
    }

    @GetMapping
    public String listCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false, defaultValue = "false") Boolean showDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            Model model) {

        CategorySearchCriteria criteria = new CategorySearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setType(type);
        criteria.setShowDeleted(showDeleted);
        criteria.setPage(page);
        criteria.setSize(size);
        criteria.setSortBy(sortBy);
        criteria.setSortDir(sortDir);

        Page<CategoryListDTO> categories = categoryService.getAllCategories(criteria);

        model.addAttribute("categories", categories.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("totalItems", categories.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("ASC") ? "DESC" : "ASC");
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", type);
        model.addAttribute("showDeleted", showDeleted);
        model.addAttribute("statistics", categoryService.getStatistics());

        return ADMIN_CATEGORIES_LIST;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return ADMIN_CATEGORIES_FORM;
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("categoryForm") CategoryForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return ADMIN_CATEGORIES_FORM;
        }

        categoryService.createCategory(form);
        String message = messageSource.getMessage("success.category.created",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return REDIRECT_ADMIN_CATEGORIES;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(id);
            CategoryForm form = CategoryForm.fromDTO(categoryDTO);
            model.addAttribute("categoryForm", form);
            return ADMIN_CATEGORIES_FORM;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return REDIRECT_ADMIN_CATEGORIES;
        }
    }

    @PostMapping("/{id}/edit")
    public String editCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoryForm") CategoryForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        form.setId(id);

        if (result.hasErrors()) {
            return ADMIN_CATEGORIES_FORM;
        }

        categoryService.updateCategory(id, form);
        String message = messageSource.getMessage("success.category.updated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return REDIRECT_ADMIN_CATEGORIES;
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        String message = messageSource.getMessage("success.category.deleted",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return REDIRECT_ADMIN_CATEGORIES;
    }

    @PostMapping("/{id}/activate")
    public String activateCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.activateCategory(id);
        String message = messageSource.getMessage("success.category.activated",
                null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return REDIRECT_ADMIN_CATEGORIES;
    }
}

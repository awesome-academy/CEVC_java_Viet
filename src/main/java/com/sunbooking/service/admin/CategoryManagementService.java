package com.sunbooking.service.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sunbooking.dto.admin.category.CategoryDTO;
import com.sunbooking.dto.admin.category.CategoryForm;
import com.sunbooking.dto.admin.category.CategoryListDTO;
import com.sunbooking.dto.admin.category.CategorySearchCriteria;
import com.sunbooking.entity.Category;
import com.sunbooking.entity.CategoryType;
import com.sunbooking.exception.DuplicateResourceException;
import com.sunbooking.exception.ResourceNotFoundException;
import com.sunbooking.mapper.CategoryMapper;
import com.sunbooking.repository.CategoryRepository;
import com.sunbooking.specification.CategorySpecification;

@Service
@Transactional
public class CategoryManagementService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryManagementService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public Page<CategoryListDTO> getAllCategories(CategorySearchCriteria criteria) {
        logger.debug("Fetching categories with criteria: {}", criteria);
        Pageable pageable = createPageable(criteria);
        Specification<Category> spec = Specification.where(null);
        if (criteria.hasTypeFilter()) {
            spec = spec.and(CategorySpecification.hasType(criteria.getType()));
        }
        if (criteria.hasKeyword()) {
            spec = spec.and(CategorySpecification.nameContains(criteria.getTrimmedKeyword()));
        }
        if (criteria.isShowDeleted()) {
            spec = spec.and(CategorySpecification.isDeleted());
        } else {
            spec = spec.and(CategorySpecification.isActive());
        }
        Page<Category> categories = categoryRepository.findAll(spec, pageable);
        logger.debug("Found {} categories", categories.getTotalElements());
        return categories.map(CategoryListDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        logger.debug("Fetching category by ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.category.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        logger.debug("Found category: {} ({})", category.getName(), category.getType());
        return categoryMapper.toDTO(category);
    }

    public CategoryDTO createCategory(CategoryForm form) {
        logger.debug("Creating new category: {} ({})", form.getName(), form.getType());
        if (categoryRepository.existsByNameAndType(form.getName().trim(), form.getType())) {
            String message = messageSource.getMessage("error.category.name.type.exists",
                    new Object[] { form.getName(), form.getType().name() },
                    LocaleContextHolder.getLocale());
            throw new DuplicateResourceException(message, "Category",
                    "name-type", form.getName() + "-" + form.getType());
        }
        Category category = categoryMapper.toEntity(form);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Created new category with ID: {}, Name: {}, Type: {} by user: {}",
                savedCategory.getId(), savedCategory.getName(),
                savedCategory.getType(), getCurrentUsername());
        return categoryMapper.toDTO(savedCategory);
    }

    public CategoryDTO updateCategory(Long id, CategoryForm form) {
        logger.debug("Updating category ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.category.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        if (categoryRepository.existsByNameAndTypeAndIdNot(
                form.getName().trim(), form.getType(), id)) {
            String message = messageSource.getMessage("error.category.name.type.exists",
                    new Object[] { form.getName(), form.getType().name() },
                    LocaleContextHolder.getLocale());
            throw new DuplicateResourceException(message, "Category",
                    "name-type", form.getName() + "-" + form.getType());
        }
        categoryMapper.updateEntityFromForm(form, category);
        Category updatedCategory = categoryRepository.save(category);
        logger.info("Updated category with ID: {}, Name: {}, Type: {} by user: {}",
                updatedCategory.getId(), updatedCategory.getName(),
                updatedCategory.getType(), getCurrentUsername());
        return categoryMapper.toDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        logger.debug("Soft deleting category ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.category.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        if (categoryRepository.hasActiveReviews(id)) {
            String message = messageSource.getMessage("error.category.has.active.reviews",
                    null, LocaleContextHolder.getLocale());
            throw new IllegalStateException(message);
        }
        category.softDelete();
        categoryRepository.save(category);
        logger.info("Soft deleted category with ID: {}, Name: {}, Type: {} by user: {}",
                category.getId(), category.getName(),
                category.getType(), getCurrentUsername());
    }

    public void activateCategory(Long id) {
        logger.debug("Reactivating category ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.category.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        category.restore();
        categoryRepository.save(category);
        logger.info("Reactivated category with ID: {}, Name: {}, Type: {} by user: {}",
                category.getId(), category.getName(),
                category.getType(), getCurrentUsername());
    }

    @Transactional(readOnly = true)
    public CategoryStatistics getStatistics() {
        long totalCategories = categoryRepository.count();
        long tourCategories = categoryRepository.countByType(CategoryType.TOUR);
        long newsCategories = categoryRepository.countByType(CategoryType.NEWS);
        long foodCategories = categoryRepository.countByType(CategoryType.FOOD);
        long placeCategories = categoryRepository.countByType(CategoryType.PLACE);
        return new CategoryStatistics(totalCategories, tourCategories,
                newsCategories, foodCategories, placeCategories);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    private Pageable createPageable(CategorySearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortDir()), criteria.getSortBy());
        return PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
    }

    public static class CategoryStatistics {
        private final long totalCategories;
        private final long tourCategories;
        private final long newsCategories;
        private final long foodCategories;
        private final long placeCategories;

        public CategoryStatistics(long totalCategories, long tourCategories,
                long newsCategories, long foodCategories, long placeCategories) {
            this.totalCategories = totalCategories;
            this.tourCategories = tourCategories;
            this.newsCategories = newsCategories;
            this.foodCategories = foodCategories;
            this.placeCategories = placeCategories;
        }

        public long getTotalCategories() { return totalCategories; }
        public long getTourCategories() { return tourCategories; }
        public long getNewsCategories() { return newsCategories; }
        public long getFoodCategories() { return foodCategories; }
        public long getPlaceCategories() { return placeCategories; }
    }
}

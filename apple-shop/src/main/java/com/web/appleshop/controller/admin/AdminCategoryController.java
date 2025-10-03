package com.web.appleshop.controller.admin;


import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.request.AdminCategoryRequest;
import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.CategoryInfoDto;
import com.web.appleshop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Handles administrative operations for product categories.
 * <p>
 * This controller provides CRUD (Create, Read, Update, Delete) and search
 * functionalities for managing categories within the admin panel.
 */
@RestController
@RequestMapping("admin/categories")
@RequiredArgsConstructor
class AdminCategoryController {
    private final CategoryService categoryService;

    /**
     * Retrieves a paginated list of categories for the admin panel.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of categories per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link CategoryInfoView}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryInfoView>>> getAllCategories(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<CategoryInfoView> categoryDtos = categoryService.getCategoriesForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                categoryDtos.getNumber(),
                categoryDtos.getSize(),
                categoryDtos.getTotalPages(),
                categoryDtos.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(categoryDtos.getContent(), "Get all categories successfully", pageableResponse));
    }

    /**
     * Searches for categories based on specified criteria for the admin panel.
     *
     * @param page The page number for pagination (optional, defaults to 0).
     * @param size The page size for pagination (optional, defaults to 6).
     * @param criteria The criteria to search categories by.
     * @return A {@link ResponseEntity} with a paginated list of found {@link CategoryInfoDto} objects.
     */
    @GetMapping("search")
    public ResponseEntity<ApiResponse<List<CategoryInfoDto>>> searchCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody CategorySearchCriteria criteria
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<CategoryInfoDto> categoryDtos = categoryService.searchCategoryForAdmin(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                categoryDtos.getNumber(),
                categoryDtos.getSize(),
                categoryDtos.getTotalPages(),
                categoryDtos.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(categoryDtos.getContent(), "Search categories successfully", pageableResponse));
    }

    /**
     * Creates a new product category.
     *
     * @param category The category details.
     * @param image The image file for the category.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> createCategory(@Valid @RequestPart AdminCategoryRequest category, @RequestPart MultipartFile image) {
        categoryService.createCategory(category, image);
        return ResponseEntity.ok(ApiResponse.success(null, "Create category successfully"));
    }

    /**
     * Updates an existing product category.
     *
     * @param id The ID of the category to update.
     * @param category The updated category details.
     * @param image The new image file for the category.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable Integer id, @Valid @RequestPart AdminCategoryRequest category, @RequestPart MultipartFile image) {
        categoryService.updateCategory(id, category, image);
        return ResponseEntity.ok(ApiResponse.success(null, "Update category successfully"));
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete category successfully"));
    }
}

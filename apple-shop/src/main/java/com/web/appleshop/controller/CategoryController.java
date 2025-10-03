package com.web.appleshop.controller;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.CategoryWProductResponse;
import com.web.appleshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles HTTP requests related to product categories.
 * <p>
 * This controller provides endpoints for retrieving a list of all categories,
 * including their associated products, and for fetching a specific category by its ID.
 * These endpoints are publicly accessible.
 */
@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Retrieves a list of all categories, each with a list of associated products.
     *
     * @return A {@link ResponseEntity} containing a list of {@link CategoryWProductResponse}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryWProductResponse>>> getAllCategories() {
        List<CategoryWProductResponse> categoryDtos = categoryService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(categoryDtos, "Get all categories successfully"));
    }

    /**
     * Retrieves a single category by its unique identifier.
     *
     * @param categoryId The ID of the category to retrieve.
     * @return A {@link ResponseEntity} containing the {@link CategoryInfoView} if found.
     */
    @GetMapping("{categoryId}")
    public ResponseEntity<ApiResponse<CategoryInfoView>> getCategoryById(@PathVariable Integer categoryId) {
        CategoryInfoView categoryDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(categoryDto, "Get category successfully"));
    }

}

package com.web.appleshop.controller;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.CategoryWProductResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryWProductResponse>>> getAllCategories() {
        List<CategoryWProductResponse> categoryDtos = categoryService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(categoryDtos, "Get all categories successfully"));
    }

    @GetMapping("{categoryId}")
    public ResponseEntity<ApiResponse<CategoryInfoView>> getCategoryById(@PathVariable Integer categoryId) {
        CategoryInfoView categoryDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(categoryDto, "Get category successfully"));
    }

}

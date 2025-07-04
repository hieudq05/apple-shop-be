package com.web.appleshop.controller.admin;


import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.CategoryInfoDto;
import com.web.appleshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/categories")
@RequiredArgsConstructor
class AdminCategoryController {
    private final CategoryService categoryService;

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
}

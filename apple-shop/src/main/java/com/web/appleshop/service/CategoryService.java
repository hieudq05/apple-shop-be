package com.web.appleshop.service;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.request.AdminCategoryRequest;
import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.dto.response.CategoryWProductResponse;
import com.web.appleshop.dto.response.admin.CategoryInfoDto;
import com.web.appleshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    Page<CategoryInfoView> getCategoriesForAdmin(Pageable pageable);

    List<CategoryWProductResponse> getCategories();

    CategoryInfoView getCategoryById(Integer categoryId);

    Category createCategory(AdminCategoryRequest request, MultipartFile file);

    Category updateCategory(Integer categoryId, AdminCategoryRequest request, MultipartFile file);

    void deleteCategory(Integer categoryId);

    Page<CategoryInfoDto> searchCategoryForAdmin(CategorySearchCriteria criteria, Pageable pageable);
}

package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.UpdateProductRequest;
import com.web.appleshop.entity.Category;
import com.web.appleshop.repository.CategoryRepository;
import com.web.appleshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<UpdateProductRequest.CategoryDto> getCategoriesForAdmin(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(this::convertCategoryToCategoryDto);
    }

    public UpdateProductRequest.CategoryDto convertCategoryToCategoryDto(Category category) {
        return new UpdateProductRequest.CategoryDto(
                category.getId(),
                category.getName(),
                category.getImage()
        );
    }
}

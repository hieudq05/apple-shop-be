package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.dto.response.admin.CategoryInfoDto;
import com.web.appleshop.entity.Category;
import com.web.appleshop.repository.CategoryRepository;
import com.web.appleshop.service.CategoryService;
import com.web.appleshop.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<CategoryInfoView> getCategoriesForAdmin(Pageable pageable) {
        return categoryRepository.findAllBy(pageable);
    }

    @Override
    public Page<CategoryInfoDto> searchCategoryForAdmin(CategorySearchCriteria criteria, Pageable pageable) {
        Specification<Category> spec = CategorySpecification.createSpecification(criteria);
        Page<Category> categories = categoryRepository.findAll(spec, pageable);
        return categories.map(this::convertCategoryToCategoryInfoDto);
    }

    private CategoryInfoDto convertCategoryToCategoryInfoDto(Category category) {
        return new CategoryInfoDto(
                category.getId(),
                category.getName(),
                category.getImage()
        );
    }
}

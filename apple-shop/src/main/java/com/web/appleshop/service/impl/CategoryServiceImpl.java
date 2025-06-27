package com.web.appleshop.service.impl;

import com.web.appleshop.dto.projection.CategoryInfoView;
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
    public Page<CategoryInfoView> getCategoriesForAdmin(Pageable pageable) {
        return categoryRepository.findAllBy(pageable);
    }
}

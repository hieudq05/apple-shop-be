package com.web.appleshop.service;

import com.web.appleshop.dto.projection.CategoryInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryInfoView> getCategoriesForAdmin(Pageable pageable);
}

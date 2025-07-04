package com.web.appleshop.service;

import com.web.appleshop.dto.projection.CategoryInfoView;
import com.web.appleshop.dto.request.CategorySearchCriteria;
import com.web.appleshop.dto.response.admin.CategoryInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryInfoView> getCategoriesForAdmin(Pageable pageable);

    Page<CategoryInfoDto> searchCategoryForAdmin(CategorySearchCriteria criteria, Pageable pageable);
}

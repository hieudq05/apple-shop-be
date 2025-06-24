package com.web.appleshop.service;

import com.web.appleshop.dto.request.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<UpdateProductRequest.CategoryDto> getCategoriesForAdmin(Pageable pageable);
}

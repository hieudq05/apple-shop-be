package com.web.appleshop.service;

import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColorService {
    Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> getColorsForAdmin(Pageable pageable);
}

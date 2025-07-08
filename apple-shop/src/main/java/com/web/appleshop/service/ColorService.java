package com.web.appleshop.service;

import com.web.appleshop.dto.request.AdminColorRequest;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColorService {
    Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> getColorsForAdmin(Pageable pageable);

    void deleteColor(Integer colorId);

    Color createColor(AdminColorRequest request);

    Color updateColor(Integer colorId, AdminColorRequest request);
}

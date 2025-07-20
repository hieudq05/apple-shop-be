package com.web.appleshop.service;

import com.web.appleshop.dto.request.AdminColorRequest;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColorService {
    Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> getColorsForAdmin(Pageable pageable);

    List<UserReviewDto.StockDto.ColorDto> getAllColors();

    void deleteColor(Integer colorId);

    ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse createColor(AdminColorRequest request);

    ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse updateColor(Integer colorId, AdminColorRequest request);
}

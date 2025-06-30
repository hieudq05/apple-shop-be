package com.web.appleshop.service.impl;

import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.Color;
import com.web.appleshop.repository.ColorRepository;
import com.web.appleshop.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {
    private final ColorRepository colorRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> getColorsForAdmin(Pageable pageable) {
        return colorRepository.findAll(pageable).map(ColorServiceImpl::convertColorToColorAdmin);
    }

    public static ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse convertColorToColorAdmin(Color color) {
        return new ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse(
                color.getId(),
                color.getName(),
                color.getHexCode()
        );
    }
}

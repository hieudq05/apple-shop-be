package com.web.appleshop.service.impl;

import com.web.appleshop.dto.request.AdminColorRequest;
import com.web.appleshop.dto.request.UpdateProductRequest;
import com.web.appleshop.dto.response.UserReviewDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.Color;
import com.web.appleshop.repository.ColorRepository;
import com.web.appleshop.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {
    private final ColorRepository colorRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> getColorsForAdmin(Pageable pageable) {
        return colorRepository.findAll(pageable).map(ColorServiceImpl::convertColorToColorAdmin);
    }

    @Override
    public List<UserReviewDto.StockDto.ColorDto> getAllColors() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<Color> colors = colorRepository.findAll(sort);
        return colors.stream().map(
                this::mapToColorDto
        ).toList();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public void deleteColor(Integer colorId) {
        colorRepository.deleteById(colorId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Color createColor(AdminColorRequest request) {
        Color color = new Color();
        color.setName(request.getName());
        color.setHexCode(request.getHexCode());
        return colorRepository.save(color);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    @Transactional
    public Color updateColor(Integer colorId, AdminColorRequest request) {
        Color color = colorRepository.findById(colorId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy màu.")
        );
        color.setName(request.getName());
        color.setHexCode(request.getHexCode());
        return colorRepository.save(
                color
        );
    }

    public UserReviewDto.StockDto.ColorDto mapToColorDto(Color color) {
        return new UserReviewDto.StockDto.ColorDto(
                color.getId(),
                color.getName(),
                color.getHexCode()
        );
    }

    public static ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse convertColorToColorAdmin(Color color) {
        return new ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse(
                color.getId(),
                color.getName(),
                color.getHexCode()
        );
    }
}

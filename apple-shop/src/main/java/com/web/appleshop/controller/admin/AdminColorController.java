package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/colors")
@RequiredArgsConstructor
class AdminColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse>>> getAllColors(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductAdminResponse.ProductStockAdminResponse.ColorAdminResponse> colors = colorService.getColorsForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                colors.getNumber(),
                colors.getSize(),
                colors.getTotalPages(),
                colors.getTotalElements()
        );
        return ResponseEntity.ok(
                ApiResponse.success(colors.getContent(), "Get all colors successfully", pageableResponse)
        );
    }
}

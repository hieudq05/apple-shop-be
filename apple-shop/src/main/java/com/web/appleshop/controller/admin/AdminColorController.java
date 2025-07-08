package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.AdminColorRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createColor(@Valid @RequestBody AdminColorRequest request) {
        colorService.createColor(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Create color successfully")
        );
    }

    @PutMapping("{colorId}")
    public ResponseEntity<ApiResponse<String>> updateColor(@PathVariable Integer colorId, @RequestBody AdminColorRequest request) {
        colorService.updateColor(colorId, request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Update color successfully")
        );
    }

    @DeleteMapping("{colorId}")
    public ResponseEntity<ApiResponse<String>> deleteColor(@PathVariable Integer colorId) {
        colorService.deleteColor(colorId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete color successfully")
        );
    }
}

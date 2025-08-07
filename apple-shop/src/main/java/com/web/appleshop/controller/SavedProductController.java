package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.UserSavedProductDto;
import com.web.appleshop.service.SavedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("saved-products")
@RequiredArgsConstructor
public class SavedProductController {
    private final SavedProductService savedProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSavedProductDto>>> savedProduct(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<UserSavedProductDto> savedProductPage = savedProductService.getMySavedProducts(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                savedProductPage.getNumber(),
                savedProductPage.getSize(),
                savedProductPage.getTotalPages(),
                savedProductPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(savedProductPage.getContent(), "Lấy danh sách sản phẩm đã lưu thành công!", pageableResponse));
    }

    @PostMapping("{stockId}")
    public ResponseEntity<ApiResponse<String>> saveProduct(@PathVariable Integer stockId) {
        savedProductService.saveProduct(stockId);
        return ResponseEntity.ok(ApiResponse.success(null, "Lưu sản phẩm thành công!"));
    }

    @DeleteMapping("{stockId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer stockId) {
        savedProductService.removeSavedProduct(stockId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xoá sản phẩm khỏi danh sách sản phẩm đã lưu!"));
    }

    @GetMapping("{stockId}/is-saved")
    public ResponseEntity<ApiResponse<Boolean>> isSavedProduct(@PathVariable Integer stockId) {
        boolean isSaved = savedProductService.isSaved(stockId);
        return ResponseEntity.ok(ApiResponse.success(isSaved, "Lấy thông tin trạng thái sản phẩm đã lưu thành công!"));
    }
}

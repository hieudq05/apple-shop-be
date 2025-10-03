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

/**
 * Manages products saved by users for later viewing.
 * <p>
 * This controller provides endpoints for authenticated users to save, view,
 * remove, and check the status of their saved products.
 */
@RestController
@RequestMapping("saved-products")
@RequiredArgsConstructor
public class SavedProductController {
    private final SavedProductService savedProductService;

    /**
     * Retrieves a paginated list of products saved by the currently authenticated user.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of saved products per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link UserSavedProductDto}.
     */
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

    /**
     * Saves a product (by its stock ID) to the user's list of saved products.
     *
     * @param stockId The ID of the stock item to save.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("{stockId}")
    public ResponseEntity<ApiResponse<String>> saveProduct(@PathVariable Integer stockId) {
        savedProductService.saveProduct(stockId);
        return ResponseEntity.ok(ApiResponse.success(null, "Lưu sản phẩm thành công!"));
    }

    /**
     * Removes a product (by its stock ID) from the user's list of saved products.
     *
     * @param stockId The ID of the stock item to remove.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("{stockId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer stockId) {
        savedProductService.removeSavedProduct(stockId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xoá sản phẩm khỏi danh sách sản phẩm đã lưu!"));
    }

    /**
     * Checks if a specific product (by its stock ID) is already saved by the user.
     *
     * @param stockId The ID of the stock item to check.
     * @return A {@link ResponseEntity} containing a boolean indicating if the product is saved.
     */
    @GetMapping("{stockId}/is-saved")
    public ResponseEntity<ApiResponse<Boolean>> isSavedProduct(@PathVariable Integer stockId) {
        boolean isSaved = savedProductService.isSaved(stockId);
        return ResponseEntity.ok(ApiResponse.success(isSaved, "Lấy thông tin trạng thái sản phẩm đã lưu thành công!"));
    }
}

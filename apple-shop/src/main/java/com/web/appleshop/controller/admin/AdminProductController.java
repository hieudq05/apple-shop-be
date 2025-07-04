package com.web.appleshop.controller.admin;

import com.web.appleshop.dto.request.AdminProductSearchCriteria;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.admin.ProductAdminListDto;
import com.web.appleshop.dto.response.admin.ProductAdminResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.service.ProductSearchService;
import com.web.appleshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<String>> createProduct(
            @RequestPart("product") String productJson,
            @RequestParam Map<String, MultipartFile> files
    ) {
        productService.createProduct(productJson, files);
        return ResponseEntity.ok(ApiResponse.success(null, "Product created successfully"));
    }

    @PutMapping(consumes = {"multipart/form-data"}, path = "{categoryId}/{productId}")
    public ResponseEntity<ApiResponse<String>> updateProduct(
            @PathVariable Integer categoryId,
            @PathVariable Integer productId,
            @RequestPart("product") String productJson,
            @RequestPart("productPhotoDeletions") Integer[] productPhotoDeletions,
            @RequestParam Map<String, MultipartFile> files
    ) {
        User updatedBy = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productService.updateProduct(categoryId, productId, productJson, files, productPhotoDeletions, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(null, "Product updated successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminListDto>>> getAllProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductAdminListDto> productAdminResponsePage = productService.getAllProductsForAdminV2(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productAdminResponsePage.getNumber(),
                productAdminResponsePage.getSize(),
                productAdminResponsePage.getTotalPages(),
                productAdminResponsePage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productAdminResponsePage.getContent(), "Get all products successfully", pageableResponse));
    }

    @GetMapping("{categoryId}/{productId}")
    public ResponseEntity<ApiResponse<ProductAdminResponse>> getProduct(@PathVariable Integer categoryId, @PathVariable Integer productId) {
        ProductAdminResponse productAdminResponse = productService.getProductByProductIdForAdmin(categoryId, productId);
        return ResponseEntity.ok(ApiResponse.success(productAdminResponse, "Get product successfully"));
    }

    @DeleteMapping("{categoryId}/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer categoryId, @PathVariable Integer productId) {
        productService.deleteProduct(categoryId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Delete product successfully"));
    }

    @GetMapping("search")
    public ResponseEntity<ApiResponse<List<ProductAdminListDto>>> searchProduct(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestBody AdminProductSearchCriteria criteria
    ) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductAdminListDto> productsPage = productSearchService.searchProductsForAdmin(criteria, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalPages(),
                productsPage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productsPage.getContent(), "Search products successfully", pageableResponse));
    }
}

package com.web.appleshop.controller;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Product created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAdminResponse>>> getAllProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductAdminResponse> productAdminResponsePage = productService.getAllProductsForAdmin(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productAdminResponsePage.getNumber(),
                productAdminResponsePage.getSize(),
                productAdminResponsePage.getTotalPages(),
                productAdminResponsePage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productAdminResponsePage.getContent(), "Get all products successfully", pageableResponse));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<String>> deleteProductById(@PathVariable("id") Integer id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa Sản phẩm thành công với id : " + id));
    }
}

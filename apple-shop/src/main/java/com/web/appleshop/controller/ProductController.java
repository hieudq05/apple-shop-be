package com.web.appleshop.controller;

import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Product created successfully"));
    }


}

package com.web.appleshop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.appleshop.dto.request.CreateProductRequest;
import com.web.appleshop.dto.request.UpdateProductRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.ProductAdminResponse;
import com.web.appleshop.entity.User;
import com.web.appleshop.security.JwtAuthenticationFilter;
import com.web.appleshop.service.ProductService;
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
public class AdminProductController {

    private final ProductService productService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public AdminProductController(ProductService productService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.productService = productService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

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
}

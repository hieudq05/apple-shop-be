package com.web.appleshop.controller;

import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.dto.response.ProductUserResponse;
import com.web.appleshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductUserResponse>>> getAllProducts(@PathVariable Integer categoryId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<ProductUserResponse> productUserResponsePage = productService.getProductsByCategoryIdForUser(categoryId, pageable);
        PageableResponse pageableResponse = new PageableResponse(
                productUserResponsePage.getNumber(),
                productUserResponsePage.getSize(),
                productUserResponsePage.getTotalPages(),
                productUserResponsePage.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(productUserResponsePage.getContent(), "Get all products successfully", pageableResponse));
    }

    @GetMapping("{categoryId}/{productId}")
    public ResponseEntity<ApiResponse<ProductUserResponse>> getProduct(@PathVariable Integer categoryId, @PathVariable Integer productId) {
        ProductUserResponse productUserResponse = productService.getProductByProductIdForUser(categoryId, productId);
        return ResponseEntity.ok(ApiResponse.success(productUserResponse, "Get product successfully"));
    }
}

package com.web.appleshop.controller;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("items")
    public ResponseEntity<ApiResponse<String>> addProductToCart(@RequestBody AddCartItemRequest request) {
        cartService.addCartItem(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Product added to cart successfully"));
    }
}

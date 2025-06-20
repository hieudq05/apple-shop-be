package com.web.appleshop.controller;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.dto.request.UpdateCartItemRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("items")
    public ResponseEntity<ApiResponse<String>> addProductToCart(@Valid @RequestBody AddCartItemRequest request) {
        cartService.addCartItem(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Product added to cart successfully"));
    }

    @PutMapping("items/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> updateCartItem(@PathVariable Integer cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {
        cartService.updateCartItem(cartItemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart item updated successfully"));
    }

    @DeleteMapping("items/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> deleteCartItem(@PathVariable Integer cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart item deleted successfully"));
    }
}

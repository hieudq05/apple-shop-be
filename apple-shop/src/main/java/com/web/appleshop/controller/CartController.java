package com.web.appleshop.controller;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.dto.request.UpdateCartItemRequest;
import com.web.appleshop.dto.response.ApiResponse;
import com.web.appleshop.dto.response.CartItemResponse;
import com.web.appleshop.dto.response.PageableResponse;
import com.web.appleshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllCartItems() {
        cartService.deleteAllCartItems();
        return ResponseEntity.ok(ApiResponse.success(null, "All cart items deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItems(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable pageable = Pageable.ofSize(size != null ? size : 6).withPage(page != null ? page : 0);
        Page<CartItemResponse> cartItemResponses = cartService.getCartItems(pageable);
        PageableResponse pageableResponse = new PageableResponse(
                cartItemResponses.getNumber(),
                cartItemResponses.getSize(),
                cartItemResponses.getTotalPages(),
                cartItemResponses.getTotalElements()
        );
        return ResponseEntity.ok(ApiResponse.success(cartItemResponses.getContent(), "Get cart items successfully", pageableResponse));
    }
}

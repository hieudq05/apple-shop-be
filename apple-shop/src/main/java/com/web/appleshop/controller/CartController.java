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

/**
 * Manages the shopping cart for authenticated users.
 * <p>
 * This controller provides endpoints to add, update, delete, and view items in a
 * user's shopping cart. All operations are performed in the context of the
 * currently authenticated user.
 */
@RestController
@RequestMapping("cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Adds a product to the current user's shopping cart.
     *
     * @param request The request body containing the product ID and quantity to add.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PostMapping("items")
    public ResponseEntity<ApiResponse<String>> addProductToCart(@Valid @RequestBody AddCartItemRequest request) {
        cartService.addCartItem(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Product added to cart successfully"));
    }

    /**
     * Updates the quantity of an item in the shopping cart.
     *
     * @param cartItemId The ID of the cart item to update.
     * @param request The request body containing the new quantity.
     * @return A {@link ResponseEntity} with a success message.
     */
    @PutMapping("items/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> updateCartItem(@PathVariable Integer cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {
        cartService.updateCartItem(cartItemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart item updated successfully"));
    }

    /**
     * Deletes a specific item from the shopping cart.
     *
     * @param cartItemId The ID of the cart item to delete.
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping("items/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> deleteCartItem(@PathVariable Integer cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart item deleted successfully"));
    }

    /**
     * Deletes all items from the current user's shopping cart.
     *
     * @return A {@link ResponseEntity} with a success message.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllCartItems() {
        cartService.deleteAllCartItems();
        return ResponseEntity.ok(ApiResponse.success(null, "All cart items deleted successfully"));
    }

    /**
     * Retrieves a paginated list of items in the current user's shopping cart.
     *
     * @param page The page number to retrieve (optional, defaults to 0).
     * @param size The number of items per page (optional, defaults to 6).
     * @return A {@link ResponseEntity} containing a paginated list of {@link CartItemResponse}.
     */
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

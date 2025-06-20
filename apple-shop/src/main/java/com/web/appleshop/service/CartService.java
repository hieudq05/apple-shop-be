package com.web.appleshop.service;

import com.web.appleshop.dto.request.AddCartItemRequest;
import com.web.appleshop.dto.response.CartItemResponse;
import com.web.appleshop.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartItem addCartItem(AddCartItemRequest cartItemRequest);

    CartItem updateCartItem(Integer cartItemId, Integer quantity);

    void deleteCartItem(Integer cartItemId);

    void deleteAllCartItems();

    Page<CartItemResponse> getCartItems(Pageable pageable);
}
